package edu.uiowa.csense.compiler.transformations.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import components.basic.CopyRefC;
import components.basic.TapComponentC;
import components.conversions.ShortsToDoublesC;
import components.conversions.ShortsToFloatsC;
import components.conversions.SplitC;
import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.PortC;
import edu.uiowa.csense.compiler.model.PortPair;
import edu.uiowa.csense.compiler.model.PortPath;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.targets.Target;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.PrimitiveType;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.types.constraints.Equal;
import edu.uiowa.csense.compiler.types.constraints.GreaterEqual;
import edu.uiowa.csense.compiler.types.constraints.LessEqual;
import edu.uiowa.csense.compiler.types.constraints.MultipleOf;
import edu.uiowa.csense.compiler.types.constraints.SFGreaterEqual;
import edu.uiowa.csense.compiler.types.constraints.Variable;
import edu.uiowa.csense.runtime.api.Frame;

/**
 * This class will optimize how frames are allocated in memory.
 * The class will generate an ILP that will be solved to find the assignments of super-frames, frames, and multipliers.
 * Variables are setup as follows:
 * 	- there is a superframe variable per path
 * 	- there is a frame variable for each port on the path as long as they have constraints
 * 	- there is a multiplier per component
 * 
 * Currently, the matrix implementation is not as expressive
 * 	- the number of rows is always fixed
 * 	- the number of columns may vary
 * 
 * Notes:
 * 	- have to set the conversions and taps to use superframes and have multipliers equal to 1
 * 	- otherwise, taps will see only the intermediary frames and not the child frames
 * 	- similarly, conversions must work on superframes to account for different multiplier used by adjacent components
 * 
 * @author ochipara
 *
 */
public class OptimizedTypeInference {
    protected static Logger logger = Logger.getLogger("types");
    protected LinearProgram linearProgram = new LinearProgram();

    //protected List<Variable> variables = new LinkedList<Variable>();
    protected HashMap<IComponentC, MultiplierVar> multiplierVars = new HashMap<IComponentC, MultiplierVar>();
    protected List<Inequality> inequalities = new LinkedList<Inequality>();
    private int splitterCounter = 1;
    private int converterCount = 1;

    private Map<String, FrameVar> frameVars = new HashMap<String, FrameVar>();
    private HashMap<PortC, SuperFrameVar> superFrames = new HashMap<PortC, SuperFrameVar>();

    public void convertTypes(CSenseGroupC main, Target target) throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();	
	List<PortPath> paths = allPaths(main);

	for (PortPath path : paths) {
	    System.out.println(path);
	    analyze(path);
	}

	linearProgram.solve(target);
	fixRates(paths, main.getComponentGraph());	
	paths = allPaths(main);
	fixBaseTypes(main); 
    }


    private List<PortPath> allPaths(CSenseGroupC main) throws CompilerException {
	List<PortPath> allPaths = new LinkedList<PortPath>();
	for (IComponentC component : main.getComponents()) {

	    for (OutputPortC output : component.getOutputPorts()) {
		if (output.isSource()) {
		    List<PortPath> paths = PortPath.pathFrom(output);
		    allPaths.addAll(paths);
		}
	    }
	}
	return allPaths;
    }


    private void fixBaseTypes(CSenseGroupC main) throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();

	List<PortPath> paths = new LinkedList<PortPath>();
	for (IComponentC component : main.getComponents()) {
	    if (component.isSource()) {
		for (OutputPortC out : component.getOutputPorts()) {
		    if (out.getInternalInput() == null) {
			// this is the beginning of the path
			paths.addAll(PortPath.pathFrom(out));
		    }
		}
	    }
	}

	for (PortPath path : paths) {
	    fixBaseTypes(path, graph);
	    fixSplitters(path, graph);
	}
    }



    private void fixSplitters(PortPath path, ComponentGraph graph) throws CompilerException {
	for (int i = 0; i < path.size() - 1; i++) {	    
	    PortPair curr = path.elementAt(i);
	    IComponentC component = curr.getOutput().getComponent();
	    if (component instanceof SplitC) {
		FrameTypeC inputType = (FrameTypeC) curr.getInput().getType();
		FrameTypeC outputType = (FrameTypeC) curr.getOutput().getType();

		if (inputType.getBaseType() != outputType.getBaseType()) {
		    // we need to add a conversion
		    SuperFrameVar superFrame = getSuperFrameVar(curr.getInput());
		    CSenseComponentC converter = newConverter(inputType.getBaseType(), outputType.getBaseType(), inputType.getNumberOfElements());
		    //CSenseComponentC converter = newConverter(inputType.getBaseType(), outputType.getBaseType(), superFrame.getValue(Constraint.COLUMN_DIMENSION));

		    graph.addComponent("converter" + converterCount, converter);
		    converterCount = converterCount + 1;

		    // fix the links
		    OutputPortC output = curr.getInput().getIncoming();
		    InputPortC input = curr.getInput();		
		    output.removeOutgoing();
		    input.removeIncoming();

		    graph.link(output, converter.getInputPort(0));		    		    		    
		    graph.toTap(converter.getVariableName() + "::" + inputType.getBaseType() + "Out" , inputType);
		    OutputPortC outputPort = converter.getOutputPort(outputType.getBaseType() + "Out");
		    graph.link(outputPort, input);

		    // fix the input type of the splitter
		    FrameTypeC cInput = new FrameTypeC(inputType);
		    cInput.setMultiplier(1, 1);
		    cInput.setRows(superFrame.getValue(Constraint.ROW_DIMENSION));		    
		    cInput.setColumns(superFrame.getValue(Constraint.COLUMN_DIMENSION));

		    FrameTypeC cOutput = new FrameTypeC(outputType);
		    cOutput.setMultiplier(1, 1);
		    cOutput.setRows(superFrame.getValue(Constraint.ROW_DIMENSION));
		    cOutput.setColumns(superFrame.getValue(Constraint.COLUMN_DIMENSION));		    
		    input.setType(cOutput);

		    //		    // fix the types
		    //		    converter.getInputPort(0).setType(input.getType());
		    //		    converter.getOutputPort(inputType.getBaseType() + "Out").setType(inputType);
		    //		    converter.getOutputPort(outputType.getBaseType() + "Out").setType(input.getType());
		}
	    }
	}
    }


    /**
     * Fixes the type conversions along the path
     * @param path
     * @param graph
     * @throws CompilerException 
     */
    protected void fixBaseTypes(PortPath path, ComponentGraph graph) throws CompilerException {
	PortPair prev = path.elementAt(0);
	for (int i = 1; i < path.size(); i++) {
	    PortPair curr = path.elementAt(i);

	    BaseTypeC prevType = prev.getOutput().getType();
	    BaseTypeC currType = curr.getInput().getType();

	    if (sameBaseType(prevType, currType) == false) {
		PrimitiveType prevPrimitive = getPrimitive(prevType);
		PrimitiveType currPrimitive = getPrimitive(currType);

		// we need to add a conversion
		SuperFrameVar superFrame = getSuperFrameVar(curr.getInput());
		//CSenseComponentC converter = newConverter(prevPrimitive, currPrimitive, prevType.getNumberOfElements());
		CSenseComponentC converter = newConverter(prevPrimitive, currPrimitive, superFrame.getValue(Constraint.COLUMN_DIMENSION));
		graph.addComponent("converter" + converterCount, converter);
		converterCount = converterCount + 1;

		// fix the links
		OutputPortC output = prev.getOutput();
		InputPortC input = curr.getInput();		
		output.removeOutgoing();
		input.removeIncoming();

		graph.link(output, converter.getInputPort(0));
		graph.toTap(converter.getVariableName() + "::" + prevPrimitive + "Out" , prevType);
		graph.link(converter.getOutputPort(currPrimitive + "Out"), input);

		// fix the types
		FrameTypeC cSrc = new FrameTypeC((FrameTypeC) output.getType());
		cSrc.setMultiplier(1, 1);
		cSrc.setRows(superFrame.getValue(Constraint.ROW_DIMENSION));		    
		cSrc.setColumns(superFrame.getValue(Constraint.COLUMN_DIMENSION));

		FrameTypeC cDst = new FrameTypeC((FrameTypeC) currType);
		cDst.setMultiplier(1, 1);
		cDst.setRows(superFrame.getValue(Constraint.ROW_DIMENSION));
		cDst.setColumns(superFrame.getValue(Constraint.COLUMN_DIMENSION));		    

		converter.getInputPort(0).setType(cSrc);
		converter.getOutputPort(((FrameTypeC) prevType).getBaseType() + "Out").setType(cSrc);
		converter.getOutputPort(((FrameTypeC) currType).getBaseType() + "Out").setType(cDst);

		//		converter.getInputPort(0).setType(output.getType());
		//		converter.getOutputPort(((FrameTypeC) prevType).getBaseType() + "Out").setType(prevType);
		//		converter.getOutputPort(((FrameTypeC) currType).getBaseType() + "Out").setType(input.getType());
	    }
	    //	    }

	    prev = curr;
	}
    }

    private boolean sameBaseType(BaseTypeC prevType, BaseTypeC currType) throws CompilerException {
	if ((prevType == null) || (currType == null)) {
	    throw new IllegalArgumentException("Inputs cannot be null");
	}

	if ((prevType instanceof JavaTypeC) && (currType instanceof JavaTypeC)) {	    
	    if (prevType.getMessageType() == Frame.class) return true;
	    if (currType.getMessageType() == Frame.class) return true;

	    return (prevType.getMessageType() == currType.getMessageType());
	} else if ((prevType instanceof FrameTypeC) && (currType instanceof FrameTypeC))  {
	    PrimitiveType prevPrimitive = getPrimitive(prevType);
	    PrimitiveType currPrimitive = getPrimitive(currType);

	    return prevPrimitive.getType() == currPrimitive.getType();
	} else {
	    throw new CompilerException("Invalid comparison");
	}
    }

    private PrimitiveType getPrimitive(BaseTypeC type) throws CompilerException {
	if (type instanceof FrameTypeC) {
	    return ((FrameTypeC) type).getBaseType();
	} else if (type instanceof PrimitiveType) {
	    return (PrimitiveType) type;
	} else if (type instanceof JavaTypeC) {
	    throw new CompilerException("Found java type");
	} else {
	    throw new CompilerException("Fix me");
	}
    }


    protected CSenseComponentC newConverter(PrimitiveType from, PrimitiveType to, int size) throws CompilerException {
	if (from.getType() == PrimitiveType.PRIMITIVE_SHORT && to.getType() == PrimitiveType.PRIMITIVE_DOUBLE) {
	    return new ShortsToDoublesC(size, Options.useNativeConversions);
	} else if (from.getType() == PrimitiveType.PRIMITIVE_SHORT && to.getType() == PrimitiveType.PRIMITIVE_FLOAT) {
	    return new ShortsToFloatsC(size, Options.useNativeConversions);
	}

	throw new CompilerException("Convertor not yet written.");
    }



    protected void fixRates(List<PortPath> allPaths, ComponentGraph graph) throws CompilerException {
	for (PortPath p : allPaths) {
	    fixRates(p, graph);
	}
    }


    /**
     * Fixes the data rates along the path
     * 
     * @param path
     * @param graph
     * @throws CompilerException
     */
    protected void fixRates(PortPath path, ComponentGraph graph) throws CompilerException {
	PortPair prev = null;
	int columns = -1;
	int rows = -1;
	int mRows = 1;
	int mCols = 1;

	for (int i = 0; i < path.size(); i++) {
	    PortPair curr = path.elementAt(i);

	    BaseTypeC type;	    
	    PortC port;
	    if (i == 0) {
		type = curr.getOutput().getType();
		port = curr.getOutput();
	    } else {
		type = curr.getInput().getType();
		port = curr.getInput();
	    }

	    SuperFrameVar superFrame = getSuperFrameVar(port);
	    FrameVar frame = getFrameVar(port);	    
	    if ((frame == null) || (superFrame == null)) {		
		throw new CompilerException("Frame cannot be null");
	    }

	    // SplitC splitter = null;
	    if (i == 0) {
		columns = superFrame.getValue(Constraint.COLUMN_DIMENSION);
		rows = superFrame.getValue(Constraint.ROW_DIMENSION);

		BaseTypeC materializedType = type;
		materializedType.setColumns(columns);
		materializedType.setRows(rows);
		materializedType.setMultiplier(1, 1);
		port.setType(materializedType);

		logger.info("matterializing " + port + " to " + materializedType);
	    } else {
		columns = frame.getValue(Constraint.COLUMN_DIMENSION);
		rows = frame.getValue(Constraint.ROW_DIMENSION);
		mCols = superFrame.getValue(Constraint.COLUMN_DIMENSION) / frame.getValue(Constraint.COLUMN_DIMENSION);
		mRows = superFrame.getValue(Constraint.ROW_DIMENSION) / frame.getValue(Constraint.ROW_DIMENSION);

		BaseTypeC materializedType = (BaseTypeC) type.clone();
		materializedType.setColumns(columns);
		materializedType.setRows(rows);
		materializedType.setMultiplier(mRows, mCols);
		port.setType(materializedType);
		InputPortC input = (InputPortC) port;
		logger.info("matterializing " + port + " to " + materializedType);

		if (input.getComponent() instanceof CopyRefC == false) {
		    if (input.getInternalOutput() != null) {		    
			logger.info("matterializing " + input.getInternalOutput() + " to " + materializedType);
			input.getInternalOutput().setType(materializedType);
		    }
		} else {
		    for (OutputPortC output : input.getComponent().getOutputPorts()) {
			output.setType(materializedType);
		    }		    
		}

		logger.info("matterializing " + port + " to " + materializedType);
	    }

	    prev = curr;
	}
    }

    protected boolean validateFrameSize(BaseTypeC type, int currentFrameSize) throws CompilerException {
	for (Constraint constraint : type.getConstraints()) {
	    if (constraint.getDimension() == Constraint.COLUMN_DIMENSION) {
		int value = constraint.getValue();
		if (constraint instanceof Equal) {
		    if (currentFrameSize != value) return false;
		} else if (constraint instanceof GreaterEqual) {
		    if (currentFrameSize < value) return false; 
		} else if (constraint instanceof LessEqual) {
		    if (currentFrameSize > value) return false;
		} else if (constraint instanceof MultipleOf) {
		    if (currentFrameSize % value == 0) return false;
		} else throw new CompilerException("Unknown constraint");
	    }	
	}

	return true;
    }


    /**
     * Creates the constraints for the ILP
     * 
     * @param path
     * @throws CompilerException
     */
    protected void analyze(PortPath path) throws CompilerException {
	SuperFrameVar superFrame = null; 

	PortPair prevPair = null;
	for (int i = 0; i < path.size(); i++) {
	    PortPair pair = path.elementAt(i);

	    // check the mapping between prevPair.outputport and part.inputport 
	    BaseTypeC srcT = null;
	    BaseTypeC dstT = null;
	    IComponentC component;
	    PortC port;
	    if (prevPair != null) {
		srcT = prevPair.getOutput().getType();
		dstT = pair.getInput().getType();
		component = pair.getInput().getComponent();
		port = pair.getInput();

		if (sameBaseType(srcT, dstT) == false) {
		    //		    try {
		    //			newConverter(getPrimitive(srcT), getPrimitive(dstT), 1);
		    //		    } catch (CompilerException e) {
		    //			System.err.println("=====================================================");
		    //			System.err.println("Cannot convert from " + srcT + " to " + dstT);
		    //			System.err.println("Link: " + prevPair.getOutput().getVariableName() + " => " + component.getVariableName());
		    //			/**
		    //			 * If this happens on a merge component,
		    //			 * you should try to change the type of the next component
		    //			 */
		    //			throw new CompilerException("Cannot convert from " + srcT + " to " + dstT);
		    //		    }
		}
	    } else {
		component = pair.getOutput().getComponent();
		dstT = pair.getOutput().getType();
		port = pair.getOutput();		
	    }



	    if (getSuperFrameVar(port) == null) {
		if (superFrame == null) superFrame = newSuperFrame();
		superFrames.put(port, superFrame);
	    }  else {
		superFrame = superFrames.get(port);
	    }

	    List<Constraint> constraints = dstT.getConstraints();
	    FrameVar frameVar = newFrameVar(superFrame, port); //null;

	    if (constraints.size() == 0) {
		// automatically constrain the type to have frames that exceed zero
		dstT.addConstraint(new GreaterEqual(Constraint.ROW_DIMENSION, 1));
		dstT.addConstraint(new GreaterEqual(Constraint.COLUMN_DIMENSION, 1));
		constraints = dstT.getConstraints();
	    }

	    boolean hasMultipleOfConstraint = false;
	    if (component instanceof TapComponentC == false) {		
		for (Constraint constraint : constraints ) {
		    int dim = constraint.getDimension();

		    frameVar = newFrameVar(superFrame, port);
		    switch(constraint.getType()) {
		    case Constraint.CONSTRAINT_EQUAL: {			
			Equal equal = (Equal) constraint;
			Inequality inequality = linearProgram.newEquation(); 
			inequality.addVariable(frameVar, dim, 1.0);
			inequality.setConstant(equal.getValue());
			inequalities.add(inequality);
			break;
		    }
		    case Constraint.CONSTRAINT_GTE: {
			GreaterEqual gte = (GreaterEqual) constraint;
			Inequality inequality = linearProgram.newInequality(Constraint.CONSTRAINT_GTE); 
			inequality.addVariable(frameVar, dim, 1.0);
			inequality.setConstant(gte.getValue());
			inequalities.add(inequality);
			break;
		    }		    
		    case Constraint.CONSTRAINT_LTE: {
			LessEqual gte = (LessEqual) constraint;
			Inequality inequality = linearProgram.newInequality(Constraint.CONSTRAINT_LTE);
			inequality.addVariable(frameVar, dim, 1.0);
			inequality.setConstant(gte.getValue());
			inequalities.add(inequality);
			break;
		    }
		    case Constraint.CONSTRAINT_MULTIPLEOF: {
			MultipleOf multipleOf = (MultipleOf) constraint;
			Inequality frameInequality = linearProgram.newEquation();
			frameInequality.addVariable(frameVar, dim, 1.0);
			frameInequality.setConstant(multipleOf.getValue());
			inequalities.add(frameInequality);

			Inequality superFrameEquality = linearProgram.newEquation();
			superFrameEquality.addVariable(superFrame, dim, -1);
			superFrameEquality.addVariable(newMultiplier(component), dim, multipleOf.getValue());
			superFrameEquality.setConstant(0);
			inequalities.add(superFrameEquality);

			hasMultipleOfConstraint = true;
			break;
		    }
		    case Constraint.SF_CONSTRAINT_GTE: {
			SFGreaterEqual gte = (SFGreaterEqual) constraint;
			Inequality inequality = linearProgram.newInequality(Constraint.CONSTRAINT_GTE); 
			inequality.addVariable(superFrame, dim, 1.0);
			inequality.setConstant(gte.getValue());
			inequalities.add(inequality);
			break;
		    }
		    default:
			throw new CompilerException("Cannot handle constraint " + constraint);
		    }
		}
	    }

	    if (hasMultipleOfConstraint == false) {
		// this can be multiple of anything, an indication that we need to work on superframes
		for (int dim = 0; dim < 2; dim++) {
		    Inequality superFrameEquality = linearProgram.newEquation();
		    superFrameEquality.addVariable(superFrame, dim, -1);
		    superFrameEquality.addVariable(frameVar, dim, 1);
		    superFrameEquality.setConstant(0);
		    inequalities.add(superFrameEquality);
		}
	    }
	    //	    } else {
	    //		// this is a tap component
	    //		// due to the way we are doing reference counting (just for superframes and not for frames)
	    //		// the taps must see superframes rather than frames
	    //		//
	    //		for (int dim = 0; dim < 2; dim++) {
	    //		    Inequality superFrameEquality = linearProgram.newEquation();
	    //		    superFrameEquality.addVariable(superFrame, dim, -1);
	    //		    superFrameEquality.addVariable(frameVar, dim, 1);
	    //		    superFrameEquality.setConstant(0);
	    //		    inequalities.add(superFrameEquality);
	    //		}
	    //
	    //	    }

	    superFrame.addPort(port);
	    prevPair = pair;
	}
    }	

    /**
     * Creates a new superframe
     * @return
     */
    protected SuperFrameVar newSuperFrame() {
	SuperFrameVar sf = new SuperFrameVar();
	linearProgram.addVariable(sf);

	Inequality superFrameConstraintCol = linearProgram.newInequality(Constraint.CONSTRAINT_GTE);
	superFrameConstraintCol.addVariable(sf, Constraint.COLUMN_DIMENSION, 1);
	superFrameConstraintCol.setConstant(1);

	Inequality superFrameConstraintRow = linearProgram.newInequality(Constraint.CONSTRAINT_GTE);
	superFrameConstraintRow.addVariable(sf, Constraint.ROW_DIMENSION, 1);
	superFrameConstraintRow.setConstant(1);

	return sf;
    }

    /**
     * returns the superframe for the port
     * 
     * @param port
     * @return
     * @throws CompilerException
     */
    protected SuperFrameVar getSuperFrameVar(PortC port) throws CompilerException {
	SuperFrameVar theSuperFrame = null;
	for (Variable var : linearProgram.getVariables()) {
	    if (var instanceof SuperFrameVar) {
		SuperFrameVar sf = (SuperFrameVar) var;
		if (sf.pathContainsPort(port)) {
		    if (theSuperFrame != null) {
			throw new CompilerException("Multiple superframe matches");
		    }
		    theSuperFrame = sf;
		}
	    }
	}

	return theSuperFrame;
    }

    /**
     * 
     * @param port
     * @return the frame variable for the specified port
     */
    protected FrameVar getFrameVar(PortC port) {
	String name = "F_" + port.getComponent().getVariableName() + "_" + port.getName() + "_col";
	Variable var = linearProgram.getVariableByName(name);

	return (FrameVar) var;
    }

    /**
     * Creates a new frame 
     * Adds the constraint that the size of the superframe should be >= size of frame
     * 
     * @param superFrame
     * @param port
     * @return
     * @throws CompilerException
     */
    protected FrameVar newFrameVar(SuperFrameVar superFrame, PortC port) throws CompilerException {
	String name = "F_" + port.getComponent().getVariableName() + "_" + port.getName();
	FrameVar var;
	if (frameVars.containsKey(name) == false) {
	    var = new FrameVar(name);
	    linearProgram.addVariable(var);
	    frameVars.put(name, var);

	    Inequality superFrameConstraintCol = linearProgram.newInequality(Constraint.CONSTRAINT_GTE);
	    superFrameConstraintCol.addVariable(superFrame, Constraint.COLUMN_DIMENSION, 1);
	    superFrameConstraintCol.addVariable(var, Constraint.COLUMN_DIMENSION, -1);
	    superFrameConstraintCol.setConstant(0);

	    //	    Inequality superFrameConstraintCol2 = linearProgram.newEquation();
	    //	    superFrameConstraintCol2.addVariable(superFrame, Constraint.COLUMN_DIMENSION, 1);
	    //	    superFrameConstraintCol2.addVariable(var, Constraint.COLUMN_DIMENSION, -1);
	    //	    superFrameConstraintCol.setConstant(0);


	    Inequality superFrameConstraintRow = linearProgram.newEquation();
	    superFrameConstraintRow.addVariable(superFrame, Constraint.ROW_DIMENSION, 1);
	    superFrameConstraintRow.addVariable(var, Constraint.ROW_DIMENSION, -1);
	    superFrameConstraintRow.setConstant(0);
	} else {
	    var = frameVars.get(name);
	}

	return var;
    }


    /**
     * Creates a multiplier variable. 
     * Adds the constraint the constraint the multiplier should be at least one.
     * 
     * @param component
     * @return
     */
    protected MultiplierVar newMultiplier(IComponentC component) {
	MultiplierVar m = multiplierVars.get(component);
	if (m == null) {
	    m = new MultiplierVar(component.getVariableName());
	    multiplierVars.put(component, m);
	    linearProgram.addVariable(m);

	    Inequality multiplierConstraintCol = linearProgram.newInequality(Constraint.CONSTRAINT_GTE);
	    multiplierConstraintCol.addVariable(m, Constraint.COLUMN_DIMENSION, 1);
	    multiplierConstraintCol.setConstant(1);

	    Inequality multiplierConstraintRow = linearProgram.newInequality(Constraint.CONSTRAINT_GTE);
	    multiplierConstraintRow.addVariable(m, Constraint.ROW_DIMENSION, 1);
	    multiplierConstraintRow.setConstant(1);	    
	}

	return m;	
    }
}
