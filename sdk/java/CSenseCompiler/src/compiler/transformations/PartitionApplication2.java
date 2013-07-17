package compiler.transformations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.CSenseComponentC.ThreadingOption;
import compiler.matlab.types.MLIntegerMatrix;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.Domain;
import compiler.model.DomainManager;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.model.Path;
import compiler.utils.MatlabCommand;
import components.basic.CopyRefC;
import components.basic.SyncQueueC;

import api.IComponentC;

public class PartitionApplication2 {
    protected static Logger logger = Logger
	    .getLogger(PartitionApplication2.class);
    static int queueCounter = 0;

    private static void constructPaths(OutputPortC out, Path currentPath,
	    List<Path> paths) throws CompilerException {
	IComponentC component = out.getComponent();
	currentPath.push(component);

	InputPortC in = out.getSingleOutgoing();
	IComponentC next = in.getComponent();
	OutputPortC internalOut = in.getInternalOutput();
	boolean shouldPop = true;

	if (internalOut != null) {
	    constructPaths(internalOut, currentPath, paths);
	} else {
	    if (next.getOutputPorts().size() == 0) {
		currentPath.push(next);
		paths.add((Path) currentPath.clone());
	    } else {
		logger.warn("Something went wrong. Probably you did not transition to IOPorts in "
			+ next);
		for (OutputPortC inOut : next.getOutputPorts()) {
		    constructPaths(inOut, currentPath, paths);
		    currentPath.pop();
		    shouldPop = false;
		}
	    }
	}
	if (shouldPop)
	    currentPath.pop();
    }

    public static void partitionApplication(CSenseGroupC main)
	    throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();
	List<IComponentC> components = new ArrayList<IComponentC>(
		graph.components());
	int N = graph.components().size();

	int numSources = 0;
	for (IComponentC c : graph.components()) {
	    if (c.getInputPorts().size() < c.getOutputPorts().size()) {
		numSources += 1;
	    }
	    if (c.getThreadType() != ThreadingOption.NONE) {
		logger.debug(c.getVariableName() + "[" + components.indexOf(c)
			+ "] requires threading");
	    }
	}

	List<Path> allPaths = new LinkedList<Path>();
	for (IComponentC c : graph.components()) {
	    if ((c.getInputPorts().size() < c.getOutputPorts().size())
		    && (c instanceof CopyRefC == false)) {
		int cIndex = components.indexOf(c);
		// this is a source, find a path from it

		List<Path> paths = new LinkedList<Path>();

		for (OutputPortC out : c.getOutputPorts()) {
		    if (out.getInternalInput() == null) {
			if (out.outLinks().size() > 0) {
			    Path currentPath = new Path();

			    constructPaths(out, currentPath, paths);
			    for (Path p : paths) {
				System.out.println(p);
			    }
			    allPaths.addAll(paths);
			} else {
			    if (out.isOptional() == false) {
				throw new CompilerException(
					"Expected this to be an optional port");
			    }
			}

		    }
		}
		// constructPaths(c., currentPath, paths);

		// for (int p = 0; p < paths.size(); p++) {
		// Path path = paths.get(p);
		// StringBuffer sb = new StringBuffer(c.getVariableName() + "["
		// + cIndex + "] :");
		// for (IComponentC pathElem : path) {
		// int index = components.indexOf(pathElem);
		// sb.append(" " + pathElem.getVariableName() + "[" + index +
		// "]");
		// }
		// logger.debug(sb.toString());
		// }
	    }
	}

	MLIntegerMatrix m = MLIntegerMatrix.zeros(allPaths.size(), N);
	MLIntegerMatrix P = MLIntegerMatrix.zeros(allPaths.size(), 1);
	for (int r = 0; r < allPaths.size(); r++) {
	    Path p = allPaths.get(r);
	    StringBuffer sb = new StringBuffer((r + 1) + " :");
	    int col = 0;
	    int cost = 1;
	    for (IComponentC pathElem : p) {
		if (pathElem.getThreadType() != ThreadingOption.NONE)
		    cost = 5;
		int c = components.indexOf(pathElem) + 1;
		m.set(r, col, c);
		col = col + 1;

		int index = components.indexOf(pathElem) + 1;
		sb.append(" " + pathElem.getVariableName() + "[" + index + "]");
	    }
	    P.set(r, 0, cost);
	    logger.debug(sb.toString() + " cost=" + cost);
	}

	// System.exit(-1);
	// System.out.println(m.getStringValue());
	// System.out.println("numComponents " + N);
	// System.out.println("numPaths " + allPaths.size());

	MLIntegerMatrix C = MLIntegerMatrix.zeros(1, N);
	// Assignments assignments = new Assignments();
	// for (Path p : allPaths) {
	// if (p.numThreadedComponents() == 1) {
	// // check if it is already partaining to a domain
	// int domain = -1;
	// for (IComponentC c : p) {
	// if (assignments.containsKey(c)) {
	// assert(domain == -1);
	// domain = assignments.get(c).getDomain();
	// }
	// }
	//
	// if (domain == -1) {
	// domain = assignments.newDomain();
	// }
	//
	// for (IComponentC c : p) {
	// int index = components.indexOf(c);
	// C.set(0, index, domain);
	// assignments.put(c, new Assignment(domain));
	// }
	// } else if (p.numThreadedComponents() > 1) {
	// throw new CompilerException("Fix me");
	// }
	// }

	// int index = 0;
	// for (IComponentC c : components) {
	// Assignment a = assignments.get(c);
	// if (a != null) {
	// System.out.println((index + 1) + " " + c.getVariableName() + " " +
	// a.getDomain() + " " + C.getValue(0, index));
	// } else {
	// System.out.println((index + 1) + " " + c.getVariableName() + " none"
	// + " " + C.getValue(0, index));
	// }
	// index += 1;
	// }

	int index = 0;
	int domain = 1;
	for (IComponentC c : graph.components()) {
	    if (c.getThreadType() != ThreadingOption.NONE) {
		System.out.println(index + " " + c.getVariableName()
			+ " **** threaded");
		C.set(0, index, domain);
		domain += 1;
	    } else {
		System.out.println(index + " " + c.getVariableName());
	    }
	    index += 1;
	}

	logger.debug("C:" + C.getStringValue());
	logger.debug("M:" + m.getStringValue());

	MatlabCommand cmd = new MatlabCommand();
	cmd.command("M = " + m.getStringValue());
	cmd.command("C = " + C.getStringValue());
	cmd.command("P = " + P.getStringValue());
	cmd.command("rng(1234);");
	cmd.command("d = csense_partition(M, C, P)");

	DomainManager manager = DomainManager.domainManager();
	try {
	    MatlabTypeConverter processor = new MatlabTypeConverter(
		    cmd.getProxy());
	    MatlabNumericArray array = processor.getNumericArray("d");
	    System.out.println("d=" + array.toString());
	    int s[] = array.getLengths();

	    for (int d = 0; d < s[0]; d++) {
		Domain cdomain = manager.newDomain();
		for (int c = 0; c < s[1]; c++) {
		    if (array.getRealValue(d, c) > 0) {
			IComponentC component = components.get(c);
			cdomain.addComponent(component);
		    }
		}
	    }
	} catch (MatlabInvocationException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to partition application");
	}

	cmd.disconnect();

	// insert sync queues after each ANDROID source
	fixTransitions(graph, manager);
	// fixAndroidSources(graph, manager);
    }

    public static void fixTransitions(ComponentGraph graph,
	    DomainManager manager) throws CompilerException {
	List<IComponentC> componentList = new LinkedList<IComponentC>(
		graph.components());
	for (IComponentC component : componentList) {
	    Domain componentDomain = component.getDomain();

	    for (OutputPortC out : component.getOutputPorts()) {
		if (out.outLinks().size() > 0) {
		    InputPortC in = out.getSingleOutgoing();
		    IComponentC next = in.getComponent();
		    Domain nextDomain = next.getDomain();

		    if (componentDomain != nextDomain) {
			IComponentC syncQueue = new SyncQueueC(out.getType(), 10);
			graph.addComponent("syncQueue" + queueCounter, syncQueue);

			out.removeOutgoingLink(in);
			in.removeIncoming();

			graph.link(out, syncQueue.getInputPort("dataIn"));
			graph.link(syncQueue.getOutputPort("dataOut"), in);

			manager.update(syncQueue, nextDomain);
			queueCounter += 1;
		    }
		} else {
		    if (out.isOptional() == false) {
			throw new CompilerException(
				"expected port to be optional");
		    }
		}
	    }
	}

    }

    public static void fixAndroidSources(ComponentGraph graph, DomainManager manager) throws CompilerException {
	
	List<IComponentC> componentList = new LinkedList<IComponentC>(graph.components());
	for (IComponentC component : componentList) {
	    if (component.getThreadType() == ThreadingOption.ANDROID) {
		for (OutputPortC out : component.getOutputPorts()) {
		    Domain componentDomain = component.getDomain();
		    InputPortC in = out.getSingleOutgoing();
		    if (in.getComponent() instanceof SyncQueueC == false) {

			IComponentC syncQueue = new SyncQueueC(out.getType(), 10);
			graph.addComponent("syncQueue" + queueCounter, syncQueue);

			out.removeOutgoingLink(in);
			in.removeIncoming();
			
			graph.link(out, syncQueue.getInputPort("dataIn"));
			graph.link(syncQueue.getOutputPort("dataOut"), in);			

			manager.update(syncQueue, componentDomain);

			queueCounter += 1;
		    }
		}
	    }
	}
    }
}
