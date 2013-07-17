package compiler.transformations.types;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import api.IComponentC;
import project.targets.Target;

import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.model.PortC;
import compiler.model.PortPair;
import compiler.model.PortPath;
import compiler.types.FrameTypeC;
import compiler.types.TypeMaterialization;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Equal;
import compiler.types.constraints.GreaterEqual;
import compiler.types.constraints.LessEqual;
import compiler.types.constraints.MultipleOf;
import components.conversions.SimpleConverterC;

public class SimpleTypeInference {
    protected static Logger logger = Logger.getLogger("types");
    protected int converterCounter = 1;

    public void convertTypes(CSenseGroupC main, Target target) throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();

	List<PortPath> paths = computePaths(main);

	for (PortPath path : paths) {
	    materializeTypes(path, graph);
	}
	for (PortPath path : paths) {
	    fixRates(path, graph);
	}
    }

    private void materializeTypes(PortPath path, ComponentGraph graph) throws CompilerException {
	for (int i = 0; i < path.size(); i++) {
	    PortPair curr = path.elementAt(i);
	    if (i == 0) {
		materializePort(curr.getOutput());		
	    } else {
		materializePort(curr.getInput());
		if (curr.getOutput() != null) materializePort(curr.getOutput());
	    }	    
	}
    }

    private void materializePort(PortC port) throws CompilerException {
	FrameTypeC type = (FrameTypeC) port.getType();
	if (type.isMaterialized() == false) {
	    TypeMaterialization mat = type.simpleSolution();
	    type.setColumns(mat.getColumns());
	    type.setRows(mat.getRows());
	    type.setMultiplier(1, 1);
	    type.validate();		    
	}
    }

    private void fixRates(PortPath path, ComponentGraph graph) throws CompilerException {
	PortPair prev = null;
	int currentColumns = 0;
	int currentRows = 0;

	for (int i = 0; i < path.size(); i++) {
	    PortPair curr = path.elementAt(i);

	    FrameTypeC type;	    
	    PortC port;
	    if (i == 0) {
		type = (FrameTypeC) curr.getOutput().getType();
		port = curr.getOutput();
	    } else {
		type = (FrameTypeC) curr.getInput().getType();
		port = curr.getInput();
	    }

	    SimpleConverterC converter = null;
	    if (i == 0) {
		TypeMaterialization mat = type.simpleSolution();
		currentRows = mat.getRows();
		currentColumns = mat.getColumns();
	    } else {
		boolean valid = validateFrameSize(type, currentRows, currentColumns);

		if (valid == false) {
		    valid = validateFrameSize(type, currentRows, currentColumns);
		    FrameTypeC prevType = (FrameTypeC) prev.getOutput().getType();
		    //currentColumns = type.getConstraint().getValue();
		    TypeMaterialization mat = type.simpleSolution();
		    currentRows = mat.getRows();
		    currentColumns = mat.getColumns();

		    // add a splitter between prev and curr
		    converter = new SimpleConverterC(prevType, type);		    
		    graph.addComponent("converter" + converterCounter, converter);
		    converterCounter += 1;

		    OutputPortC output = prev.getOutput();
		    InputPortC input = (InputPortC) port;		    

		    output.removeOutgoing();
		    input.removeIncoming();


		    graph.link(output, converter.getInputPort(0));
		    graph.toTap(converter.getVariableName() + "::srcOut", prevType);
		    graph.link(converter.getOutputPort("dstOut"), input);		  
		}		
	    }

	    FrameTypeC materializedType = new FrameTypeC(type);	    
	    materializedType.setColumns(currentColumns);
	    materializedType.setRows(currentRows);

	    logger.info("matterializing " + port + " to " + materializedType);

	    prev = curr;
	}

    }


    private boolean validateFrameSize(FrameTypeC type, int rows, int columns) throws CompilerException {
	return validateType(type, rows, Constraint.ROW_DIMENSION) & validateType(type, columns, Constraint.COLUMN_DIMENSION);
    }

    private boolean validateType(FrameTypeC type, int solution, int dimension) throws CompilerException {

	for (Constraint constraint : type.getConstraints()) {	    
	    int value = constraint.getValue();
	    if (constraint.getDimension() == dimension) {
		if (constraint instanceof Equal) {
		    return solution == value;
		} else if (constraint instanceof GreaterEqual) {
		    return solution >= value; 
		} else if (constraint instanceof LessEqual) {
		    return solution <= value;
		} else if (constraint instanceof MultipleOf) {
		    return value % solution == 0;
		} else throw new CompilerException("Unknown constraint");
	    }
	}

	return true;
    }

    private List<PortPath> computePaths(CSenseGroupC main) throws CompilerException {
	List<PortPath> paths = new LinkedList<PortPath>();
	for (IComponentC component : main.getComponents()) {
	    if (component.isSource()) {
		for (OutputPortC out : component.getOutputPorts()) {
		    if (out.getInternalInput() == null) {
			if (component instanceof CSenseSourceC) {
			    // this is the beginning of the path
			    paths.addAll(PortPath.pathFrom(out));
			} else {
			    throw new CompilerException("Expected this to be a Source");
			}
		    }
		}
	    }
	}
	return paths;
    }
}
