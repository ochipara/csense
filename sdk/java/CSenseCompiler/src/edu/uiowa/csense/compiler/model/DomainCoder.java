package edu.uiowa.csense.compiler.model;

import java.io.IOException;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.utils.JavaCoder;

public class DomainCoder {
    public static void codeDomains(CSenseGroupC main, JavaCoder coder)
	    throws IOException, CompilerException {
	ComponentGraph cgraph = main.getComponentGraph();
	DomainManager domainManger = cgraph.getDomainManager();

	coder.comment("Instantiate the components");
	instantiateComponents(cgraph, coder);
	coder.newline();

	for (Domain domain : domainManger.domains()) {
	    codeDomain(domain, cgraph, coder);
	    coder.newline();
	}
    }

    public static void codeDomain(Domain domain, ComponentGraph cgraph, JavaCoder coder) throws IOException, CompilerException {
	coder.comment("");
	coder.comment("Generating code for " + domain);
	coder.comment("");

	String schedulerName = domain.schedulerName();

	coder.code(schedulerName + " = csense.newScheduler(\""
		+ domain.toString() + "\");");
	coder.newline();

	for (IComponentC component : domain.components()) {
	    String variableName = component.getVariableName();
	    coder.code(schedulerName + ".addComponent(" + variableName + ");\n");
	    for (InputPortC input : component.getInputPorts()) {
		BaseTypeC frameType = input.getType();
		int mCol = frameType.getMultipler(Constraint.COLUMN_DIMENSION);
		int mRow = frameType.getMultipler(Constraint.ROW_DIMENSION);
		if (mRow != 1) {
		    throw new CompilerException("Expected the row multiplier to 1");
		}
// 		coder.code(variableName + ".getInputPort(\"" + input.getName() + "\").setMultiplier(" + mCol + ");");
 		
 		coder.code(variableName + ".setMultiplier(" + mCol + ");");
	    }
	    
//	    for (OutputPortC input : component.getOutputPorts()) {
//		BaseTypeC frameType = input.getType();
//		int mCol = frameType.getMultipler(Constraint.COLUMN_DIMENSION);
//		int mRow = frameType.getMultipler(Constraint.ROW_DIMENSION);
//		if (mRow != 1) {
//		    throw new CompilerException("Expected the row multiplier to 1");
//		}
//		coder.code(variableName + ".getOutputPort(\"" + input.getName() + "\").setMultiplier(" + mCol + ");");
//	    }
	}

	// link the components
	linkComponents(domain, cgraph, coder);
    }

    public static void instantiateComponents(ComponentGraph cgraph, JavaCoder coder) throws IOException, CompilerException {
	// instantiate all components
	for (IComponentC component : cgraph.components()) {
	    // get the component that we will instantiate
	    String variableName = component.getVariableName();
	    String className = component.getName();

	    // print the left hand side of the assignment operator
	    coder.code(className);

	    // add the generic type definition
	    component.getCoder().genericSignature(component, coder);
	    coder.code(" " + variableName + " = new " + className);
	    component.getCoder().genericSignature(component, coder);

	    // check if the user provided arguments for a component!
	    coder.code("(");
	    component.getCoder().arguments(component, coder);
	    coder.code(")");
	    coder.code(";");

	    // set the name of the component
	    coder.code(variableName + ".setName(\"" + variableName + "\");");

	    // print the component's relationship with the scheduler

	}
    }

    public static void linkComponents(Domain domain, ComponentGraph cgraph,
	    JavaCoder coder) throws CompilerException {
	for (IComponentC component : domain.components()) {
	    for (OutputPortC source : component.getOutputPorts()) {
		if (source.outLinks().size() > 1) {
		    final String errMsg = source.getQName()
			    + " violates the constraint that an output port can have a single outgoing link.";
		    System.err.println(errMsg);
		    System.err.println("Included ports:");
		    for (InputPortC out : source.outLinks()) {
			System.err.println(out);
		    }
		    throw new CompilerException(errMsg);
		} else if (source.outLinks().size() == 1) {
		    InputPortC destination = source.outLinks().get(0);
		    coder.code(source.getComponent().getVariableName()
			    + ".getOutputPort(\"" + source.getName()
			    + "\").link("
			    + destination.getComponent().getVariableName()
			    + ".getInputPort(\"" + destination.getName()
			    + "\"));");
		}
	    }
	}
    }
}
