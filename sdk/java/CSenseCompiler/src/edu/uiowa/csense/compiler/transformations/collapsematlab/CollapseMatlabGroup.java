package edu.uiowa.csense.compiler.transformations.collapsematlab;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import components.basic.CopyRefC;
import components.basic.MemorySourceC;
import components.basic.SliceC;
import components.basic.TapComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabConstant;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.matlab.MatlabPersistent;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.PortC;
import edu.uiowa.csense.compiler.model.PortPair;
import edu.uiowa.csense.compiler.model.PortPath;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.utils.Coder;
import edu.uiowa.csense.compiler.utils.MatlabCoder;

public class CollapseMatlabGroup {
    protected static final Logger logger = Logger.getLogger("matlab-collapse");
    protected static final String _groupPrefix = "mgroup";
    protected static int _groupCount = 0;
    protected static final boolean trace = true;
    public static int MIN_GROUP_SIZE = 2;

    public static void collapseGroup(Project project, CSenseGroupC mainGroup)
	    throws CompilerException {
	ComponentGraph graph = mainGroup.getComponentGraph();

	boolean hasToMerge = true;
	while (hasToMerge) {
	    MatlabGroup bestGroup = null;
	    Collection<IComponentC> components = graph.components();

	    hasToMerge = false;
	    for (IComponentC component : graph.components()) {
		if (component instanceof MatlabComponentC) {
		    MatlabComponentC matlabComponent = (MatlabComponentC) component;

		    MatlabGroup matlabGroup = new MatlabGroup();
		    matlabGroup.addComponent(matlabComponent);
		    groupComponent(matlabComponent, matlabGroup, components);
		    if (bestGroup == null) {
			if (matlabGroup.size() > MIN_GROUP_SIZE)
			    bestGroup = matlabGroup;
		    } else {
			if (bestGroup.size() < matlabGroup.size()) {
			    bestGroup = matlabGroup;
			}
		    }

		    logger.debug("base [" + component.getVariableName() + "] group: " + matlabGroup);
		}
	    }

	    if (bestGroup != null) {
		hasToMerge = true;
		logger.info("collapsing: " + bestGroup);

		// collapse the component
		collapseGroup(project, bestGroup, graph);
	    }
	}
    }

    private static void collapseGroup(Project project, MatlabGroup matlabGroup,
	    ComponentGraph graph) throws CompilerException {
	String matlabFunctionName = _groupPrefix + _groupCount;
	_groupCount += 1;
	matlabGroup.setName(matlabFunctionName);

	logger.info("*************************************************** " + matlabFunctionName);
	logger.info(matlabGroup + "[" + matlabGroup.size() + "] ");
	logger.info(matlabGroup.displayIn());
	logger.info(matlabGroup.displayOut());

	MatlabComponentC mcomponent = new MatlabComponentC(matlabFunctionName);
	mcomponent.setVariableName(matlabFunctionName);

	fixParameters(matlabGroup, mcomponent);

	for (IComponentC parentComponent : matlabGroup.components()) {
	    if (parentComponent instanceof MatlabComponentC) {
		for (MatlabArgument arg : ((MatlabComponentC) parentComponent).allMatlabArguments()) {
		    if (arg instanceof MatlabPersistent) {
			MatlabPersistent persistent = (MatlabPersistent) arg;
			String name = parentComponent.getVariableName() + "_" + persistent.getName();
			MatlabPersistent newPersistent = new MatlabPersistent(name, arg.getMatlabType());
			mcomponent.addMatlabInput(newPersistent);
			mcomponent.addMatlabOutput(newPersistent);
			logger.debug("adding output " + newPersistent);
		    } else if (arg instanceof MatlabConstant) {
			MatlabConstant constant = (MatlabConstant) arg;
			String name = parentComponent.getVariableName() + "_" + constant.getName();
			MatlabConstant newConstant = new MatlabConstant(name, arg.getMatlabType());
			mcomponent.addMatlabInput(newConstant);
			logger.debug("adding output " + newConstant);
		    }
		}
	    }
	}

	mcomponent.sortMatlabArguments();

	// generate the code
	File functionFile = new File(project.getTarget().getJniDirectory(),
		matlabFunctionName + ".m");
	MatlabCoder coder = new MatlabCoder();
	generateSignature(mcomponent, coder);
	generateCode(matlabGroup, graph, coder);
	try {
	    coder.saveToFile(functionFile);
	} catch (IOException e) {
	    throw new CompilerException(e);
	}

	// graph transformations
	graph.addComponent(matlabFunctionName, mcomponent);

	for (OutputPortC output : matlabGroup.outputPorts()) {
	    OutputPortC newOutput = matlabGroup.mapOld2NewOutput().get(output);

	    if (newOutput != null) {
		InputPortC in = output.getSingleOutgoing();
		graph.relink(in, output, newOutput);
	    }
	}

	for (InputPortC input : matlabGroup.inputPorts()) {
	    InputPortC newInput = matlabGroup.mapOld2NewInput().get(input);

	    OutputPortC out = input.getIncoming();
	    graph.relink(out, input, newInput);
	}

	// remove all these components from graph
	for (IComponentC c : matlabGroup.components()) {
	    graph.removeComponentAndClean(c);
	}

	// remove any disconnected taps
	List<IComponentC> removeTaps = new LinkedList<IComponentC>();
	for (IComponentC c : graph.components()) {
	    if (c instanceof TapComponentC) {
		if (c.getInputPorts().get(0).getIncoming() == null) {
		    removeTaps.add(c);
		}
	    } else if (c instanceof MemorySourceC) {
		if (c.getOutputPorts().get(0).outLinks().size() == 0) {
		    removeTaps.add(c);
		}
	    }
	}
	for (IComponentC c : removeTaps) {
	    graph.removeComponent(c);
	    logger.debug("remove " + c);
	}
    }

    /**
     * INPUT/OUTPUT parameters are handled as follows 
     * 	- input/output ports are on the same component => a single INPUT/OUTPUT port is created -
     *  - input/output ports are on different components => an input and an output argument are created
     * 
     * 
     * @param matlabGroup
     * @param groupComponent
     * @throws CompilerException
     */
    private static void fixParameters(MatlabGroup matlabGroup, MatlabComponentC groupComponent) throws CompilerException {
	for (IComponentC component : matlabGroup.components()) {
	    if (component instanceof MatlabComponentC) {
		MatlabComponentC matlabComponent = (MatlabComponentC) component;
		for (MatlabArgument arg : matlabComponent.allMatlabArguments()) {
		    if (arg instanceof MatlabParameter) {
			MatlabParameter param = (MatlabParameter) arg;

			// we need to create an equivalent parameter
			//
			InputPortC originalInput = param.getInputPort();
			IComponentC prevComponentC = originalInput.getIncoming().getComponent();
			if (matlabGroup.contains(prevComponentC) == false) {
			    // this is a component on the edge if the input does
			    // not link within the group

			    List<PortPath> paths = PortPath.pathFromWithin(originalInput, matlabGroup.components());

			    IComponentC source = originalInput.getIncoming().getComponent();

			    boolean firstPath = true;
			    for (PortPath path : paths) {
				PortPair pair = path.lastElement();
				OutputPortC output = pair.getOutput();

				IComponentC tap = output.getSingleOutgoing().getComponent();
				boolean internal = (source instanceof MemorySourceC)
					&& (tap instanceof TapComponentC);

				if (internal) {
				    matlabGroup.addInternalInput(originalInput);
				} else {
				    OutputPortC newOutput = getOrCreateOutput(output, groupComponent, matlabGroup);
				    InputPortC newInput = getOrCreateInput(originalInput, groupComponent, matlabGroup);
				    newInput.setInternalOutput(newOutput);
				    newOutput.setInternalInput(newInput);
				    switch (param.getOutputType()) {
				    case MatlabArgument.INPUT: {
					String name = portName(output);
					MatlabParameter newParam = new MatlabParameter(
						name, param.getOutputType(),
						newInput, newOutput);

					groupComponent.addMatlabInput(newParam);
					logger.debug("adding input " + newParam);
					break;
				    }
				    case MatlabArgument.OUTPUT: {
					String name = portName(output);
					MatlabParameter newParam = new MatlabParameter(
						name, param.getOutputType(),
						newInput, newOutput);

					groupComponent .addMatlabOutput(newParam);
					logger.debug("adding output " + newParam);

					break;
				    }
				    case MatlabArgument.INPUT_OUTPUT: {
					if (path.size() == 1) {
					    String name = portName(output);
					    MatlabParameter newParam = new MatlabParameter(
						    name,
						    param.getOutputType(),
						    newInput, newOutput);
					    groupComponent.addMatlabInput(newParam);
					    groupComponent.addMatlabOutput(newParam);
					    logger.debug("adding input/output " + newParam);
					} else {
					    if (firstPath) {
						// the input is added only for
						// the first path, otherwise you
						// will get duplicate parameters
						String name = portName(originalInput);
						MatlabParameter newParamIn = new MatlabParameter(
							name,
							MatlabArgument.INPUT,
							newInput, newOutput);
						groupComponent.addMatlabInput(newParamIn);
						logger.debug("adding input " + newParamIn);
						firstPath = false;
					    }

					    // the output is added for each path
					    String name = portName(output);
					    MatlabParameter newParamOut = new MatlabParameter(
						    name,
						    MatlabArgument.OUTPUT,
						    newInput, newOutput);
					    
					    groupComponent.addMatlabOutput(newParamOut);
					    logger.debug("adding output " + newParamOut);
					}

					break;
				    }
				    } // end switch
				} // end if
			    }
			}
		    }
		}
	    }

	}
    }

    private static OutputPortC getOrCreateOutput(OutputPortC output,
	    MatlabComponentC groupComponent, MatlabGroup matlabGroup)
		    throws CompilerException {
	OutputPortC newOutput;
	try {
	    newOutput = groupComponent.getOutputPort(portName(output));
	} catch (CompilerException ce) {
	    newOutput = groupComponent.addOutputPort(output.getType(),
		    portName(output));
	    matlabGroup.mapOutput(output, newOutput);
	}

	return newOutput;
    }

    private static InputPortC getOrCreateInput(InputPortC originalInput,
	    MatlabComponentC groupComponent, MatlabGroup matlabGroup)
		    throws CompilerException {
	InputPortC newInput = null;
	try {
	    newInput = groupComponent.getInputPort(portName(originalInput));
	} catch (CompilerException ce) {
	    newInput = groupComponent.addInputPort(originalInput.getType(),
		    portName(originalInput));
	    matlabGroup.mapInput(originalInput, newInput);
	}

	return newInput;
    }

    /**
     * Data is pushed from the inputs of a component to the inputs of next
     * component
     * 
     * @param group
     * @param graph
     * @param coder
     * @throws CompilerException
     */
    private static void generateCode(MatlabGroup group, ComponentGraph graph,
	    MatlabCoder coder) throws CompilerException {
	logger.info("*************************************************** " + group.getName());
	logger.info(group.displayIn());
	logger.info(group.displayOut());

	HashMap<IComponentC, ReachableComponent> toInvoke = new HashMap<IComponentC, ReachableComponent>();
	for (IComponentC member : group.components()) {
	    toInvoke.put(member, new ReachableComponent(member));
	}

	// push the input ports
	List<InputPortC> ginputs = new LinkedList<InputPortC>(
		group.inputPorts());
	ginputs.addAll(group.internalInputPorts());
	for (InputPortC input : ginputs) {
	    IComponentC component = input.getComponent();
	    ReachableComponent rc = toInvoke.get(component);
	    assert (rc != null);

	    rc.reachInput(input);
	}

	List<IComponentC> toRemove = new LinkedList<IComponentC>();
	while (toInvoke.size() > 0) {
	    for (ReachableComponent rcomponent : toInvoke.values()) {
		if (rcomponent.allInputsReached()) {

		    if (rcomponent.component() instanceof MatlabComponentC) {
			MatlabComponentC mcomponent = (MatlabComponentC) rcomponent
				.component();
			invokeFunction(mcomponent, coder, true);
			coder.code(";");

			for (MatlabArgument arg : mcomponent
				.allMatlabArguments()) {
			    if (arg instanceof MatlabParameter) {
				MatlabParameter param = (MatlabParameter) arg;
				if (param.getOutputType() == MatlabArgument.INPUT) {
				    coder.code(portName(param.getOutputPort())
					    + " = "
					    + portName(param.getInputPort())
					    + ";");
				}
			    }
			}

			// push the outputs to the next inputs
			for (OutputPortC out : mcomponent.getOutputPorts()) {
			    InputPortC nextIn = out.getSingleOutgoing();
			    if (nextIn.getComponent() instanceof MatlabComponentC) {
				MatlabParameter param = mcomponent
					.getParameter(out);
				if (param.getOutputType() != MatlabArgument.INPUT) {
				    coder.code(portName(nextIn) + " = "
					    + portName(out) + ";");
				} else {
				    coder.code(portName(nextIn) + " = "
					    + portName(out.getInternalInput())
					    + ";");
				}
			    } else if (nextIn.getComponent() instanceof CopyRefC) {
				coder.code(portName(nextIn) + " = "
					+ portName(out) + ";");
			    } else {
				// throw new CompilerException("Fix me!");
			    }
			}
			coder.newline();
		    } else if (rcomponent.component() instanceof CopyRefC) {
			CopyRefC copyref = (CopyRefC) rcomponent.component();
			// OutputPortC prevOutput =
			// copyref.getInputPort(0).incoming();
			//
			// // push the outputs to the next inputs
			// for (OutputPortC out : copyref.outputPorts()) {
			// InputPortC nextIn = out.singleOutgoing();
			// coder.code(portName(nextIn) + " = " +
			// portName(prevOutput) + ";");
			// }

			for (OutputPortC out : copyref.getOutputPorts()) {
			    InputPortC nextIn = out.getSingleOutgoing();

			    coder.code(portName(nextIn) + " = "
				    + portName(copyref.getInputPort(0)) + ";");
			}
		    } else if (rcomponent.component() instanceof SliceC) {
			SliceC slice = (SliceC) rcomponent.component();
			OutputPortC prevOutput = slice.getInputPort(0).getIncoming();

			InputPortC nextIn = slice.getOutputPort(0).getSingleOutgoing();
			if (prevOutput.getInternalInput() != null) {
			    coder.code(portName(nextIn) + " = "
				    + portName(prevOutput)
				    + "(" + (1 + slice.lower()) + ":"
				    + slice.upper() + ");");
			} else {
			    // we must have a ref
			    CopyRefC copyref = (CopyRefC) prevOutput
				    .getComponent();
			    coder.code(portName(nextIn) + " = "
				    + portName(copyref.getInputPort(0)) + "("
				    + (1 + slice.lower()) + ":" + slice.upper()
				    + ");");
			}
		    } else
			throw new CompilerException("fix me");

		    toRemove.add(rcomponent.component());

		    for (IComponentC next : rcomponent.component()
			    .nextComponents()) {
			ReachableComponent rnext = toInvoke.get(next);
			if (rnext != null) {
			    List<InputPortC> inputs = rnext.component().getInputPortsFrom(rcomponent.component());
			    rnext.reachInput(inputs);
			} else {
			    // throw new
			    // CompilerException("this should not happen");
			}
		    }
		}
	    }

	    for (IComponentC comp : toRemove)
		toInvoke.remove(comp);
	}

	coder.code("end");
    }

    private static void invokeFunction(MatlabComponentC component,
	    MatlabCoder coder, boolean isNotSignature) throws CompilerException {
	coder.code("[");
	List<String> args = new LinkedList<String>();
	for (MatlabArgument arg : component.matlabOutputs()) {
	    if (arg instanceof MatlabParameter) {
		MatlabParameter param = (MatlabParameter) arg;
		assert (param.getOutputType() != MatlabArgument.INPUT);
		if (isNotSignature) {
		    args.add(portName(param.getOutputPort()));
		} else {
		    args.add(param.getOutputPort().getName());
		}

	    } else if (arg instanceof MatlabPersistent) {
		MatlabPersistent persistent = (MatlabPersistent) arg;
		String name = component.getVariableName() + "_"
			+ persistent.getName();
		if (isNotSignature)
		    args.add(name);
		else
		    args.add(persistent.getName());

	    } else if (arg instanceof MatlabConstant) {
		throw new CompilerException("Constants cannot be outputs");
	    } else {
		throw new CompilerException("fix me");
	    }
	}

	coder.code(Coder.list2string(args));
	coder.code("] = " + component.getMatlabFunctionName() + "(");

	args.clear();
	for (MatlabArgument arg : component.matlabInputs()) {
	    if (arg instanceof MatlabParameter) {
		MatlabParameter param = (MatlabParameter) arg;
		if (param.getOutputType() != MatlabArgument.OUTPUT) {
		    if (isNotSignature) {
			args.add(portName(param.getInputPort()));
		    } else {
			args.add(param.getInputPort().getName());
		    }
		}
	    } else if (arg instanceof MatlabPersistent) {
		MatlabPersistent persistent = (MatlabPersistent) arg;
		String name = component.getVariableName() + "_"
			+ persistent.getName();

		if (isNotSignature)
		    args.add(name);
		else
		    args.add(persistent.getName());
	    } else if (arg instanceof MatlabConstant) {
		MatlabConstant constant = (MatlabConstant) arg;
		String name = component.getVariableName() + "_"
			+ constant.getName();

		if (isNotSignature)
		    args.add(name);
		else
		    args.add(constant.getName());
	    } else {
		throw new CompilerException("fix me");
	    }
	}

	coder.code(Coder.list2string(args));
	coder.code(")");
    }

    /**
     * Traverses the graph and generates a connected matlab group Components are
     * initially added to the reachable list When all the inputs of a component
     * are reached, the component is added to the group. The process ends when
     * the number of reached components stops increasing
     * 
     * @param baseComponent
     * @param matlabGroup
     * @param components
     * @throws CompilerException
     */
    private static void groupComponent(MatlabComponentC baseComponent,
	    MatlabGroup matlabGroup, Collection<IComponentC> components)
		    throws CompilerException {
	HashMap<IComponentC, ReachableComponent> reachableComponents = new HashMap<IComponentC, ReachableComponent>();
	reachableComponents.put(baseComponent, new ReachableComponent(
		baseComponent));

	List<ReachableComponent> toAdd = new LinkedList<ReachableComponent>();
	int prevSize = 0, iter = 0;
	while (reachableComponents.size() > prevSize) {
	    iter = iter + 1;
	    prevSize = reachableComponents.size();

	    for (IComponentC component : reachableComponents.keySet()) {
		for (IComponentC next : component.nextComponents()) {
		    ReachableComponent nextR = reachComponent(next,
			    reachableComponents);
		    if (nextR != null) {
			toAdd.add(nextR);

			List<InputPortC> inputs = next
				.getInputPortsFrom(component);
			nextR.reachInput(inputs);
		    }
		} // end matlab component iter

		for (IComponentC prev : component.prevComponents()) {
		    ReachableComponent nextR = reachComponent(prev,
			    reachableComponents);
		    if (nextR != null) {
			toAdd.add(nextR);

			List<InputPortC> inputs = component
				.getInputPortsFrom(prev);
			nextR = reachableComponents.get(component);
			assert (inputs.size() > 0);
			nextR.reachInput(inputs);
		    }
		}
	    }

	    for (ReachableComponent rc : toAdd) {
		if (reachableComponents.containsValue(rc) == false) {
		    reachableComponents.put(rc.component(), rc);
		    if (trace)
			logger.debug(iter + "  "
				+ baseComponent.getVariableName() + " reached "
				+ rc);
		}
	    }
	    toAdd.clear();

	    // add those elements that have all inputs satisfied
	    for (ReachableComponent rc : reachableComponents.values()) {
		if (rc.allInputsReached()) {
		    if ((rc.component() instanceof MemorySourceC == false)
			    && (rc.component() instanceof TapComponentC == false)) {
			if (matlabGroup.addComponent(rc.component())) {
			    if (trace)
				logger.debug(iter + "  "
					+ baseComponent.getVariableName()
					+ " grouped " + rc.component());
			}
		    }
		}
	    }
	}
    }

    private static ReachableComponent reachComponent(IComponentC next,
	    HashMap<IComponentC, ReachableComponent> reachableComponents) {
	if ((next instanceof MatlabComponentC)
		|| (next instanceof MemorySourceC)
		|| (next instanceof CopyRefC)
		|| (next instanceof TapComponentC) || (next instanceof SliceC)) {
	    IComponentC matlabNext = next;

	    ReachableComponent nextR = reachableComponents.get(matlabNext);
	    if (nextR == null) {
		nextR = new ReachableComponent(matlabNext);
	    }
	    return nextR;
	}

	return null;
    }

    private static void generateSignature(MatlabComponentC component,
	    MatlabCoder coder) throws CompilerException {
	coder.code("function ");
	invokeFunction(component, coder, false);
	coder.newline();
    }

    private static String portName(PortC input) {
	String r = input.getComponent().getVariableName() + "_"
		+ input.getName();
	return r;
    }
}
