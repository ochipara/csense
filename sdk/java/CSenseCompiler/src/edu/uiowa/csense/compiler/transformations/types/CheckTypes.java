package edu.uiowa.csense.compiler.transformations.types;

import java.util.LinkedList;
import java.util.List;

import components.basic.SliceC;
import components.conversions.ShortsToDoublesC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.PortPair;
import edu.uiowa.csense.compiler.model.PortPath;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.targets.Target;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.runtime.api.Frame;

public class CheckTypes {

    public static void checkTypes(CSenseGroupC main, Target target) throws CompilerException {
	List<PortPath> paths = computePaths(main);
	for (PortPath path : paths) {
	    checkTypes(path);
	}

	for (IComponentC component : main.getComponents()) {
	    checkInternals(component);
	}
    }

    private static void checkInternals(IComponentC component) throws CompilerException {
	for (InputPortC input : component.getInputPorts()) {
	    OutputPortC output = input.getInternalOutput();
	    if (output != null) {
		BaseTypeC inputType = input.getType();
		BaseTypeC outputType = output.getType();

		if ((component instanceof SliceC == false) && (component instanceof ShortsToDoublesC == false)) {
		    if ((inputType instanceof FrameTypeC) && (outputType instanceof FrameTypeC)) {	    
			verifyTypes((FrameTypeC) inputType, (FrameTypeC) outputType);
		    } else if ((inputType instanceof JavaTypeC) && (outputType instanceof JavaTypeC)) {
			verifyTypes((JavaTypeC) inputType, (JavaTypeC) outputType);
		    } else throw new CompilerException("Invalid comparison");
		}
	    }
	}
    }

    private static void checkTypes(PortPath path) throws CompilerException {
	PortPair prev = path.elementAt(0);
	for (int i = 1; i < path.size() - 1; i++) {
	    PortPair curr = path.elementAt(i);

	    BaseTypeC inputType = prev.getOutput().getType();
	    BaseTypeC outputType = curr.getInput().getType();

	    if ((inputType instanceof FrameTypeC) && (outputType instanceof FrameTypeC)) {	    
		verifyTypes((FrameTypeC) inputType, (FrameTypeC) outputType);
	    } else if ((inputType instanceof JavaTypeC) && (outputType instanceof JavaTypeC)) {
		verifyTypes((JavaTypeC) inputType, (JavaTypeC) outputType);
	    } else throw new CompilerException("Invalid comparison");

	    prev = curr;
	}

    }

    private static void verifyTypes(JavaTypeC inputType, JavaTypeC outputType) throws CompilerException {
	if (inputType.getNumberOfElements() != 1) {
	    throw new CompilerException("Expected type to have one element");
	}

	if (outputType.getNumberOfElements() != 1) {
	    throw new CompilerException("Expected type to have one element");
	}

	if (inputType.getMessageType() != outputType.getMessageType()) {
	    if ((inputType.getMessageType() != Frame.class) && (outputType.getMessageType() != Frame.class)) { 
		throw new CompilerException("Incompatible types");
	    }
	}

    }

    private static void verifyTypes(FrameTypeC inputType, FrameTypeC outputType) throws CompilerException {
	if (inputType.getBaseType().getType() != outputType.getBaseType().getType()) {
	    throw new CompilerException("Base types do not match");
	}

	if (inputType.getRows() * inputType.getMultipler(Constraint.ROW_DIMENSION) != outputType.getRows() * outputType.getMultipler(Constraint.ROW_DIMENSION)) {
	    throw new CompilerException("Number of rows do not match");
	}

	if (inputType.getColumns() * inputType.getMultipler(Constraint.COLUMN_DIMENSION) != outputType.getColumns() * outputType.getMultipler(Constraint.COLUMN_DIMENSION)) {
	    throw new CompilerException("Number of columns do not match");
	}	
    }


    private static List<PortPath> computePaths(CSenseGroupC main) throws CompilerException {
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
	return paths;
    }
}
