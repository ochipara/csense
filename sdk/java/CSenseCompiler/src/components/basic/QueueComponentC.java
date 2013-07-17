package components.basic;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import components.basic.QueueComponent;


public class QueueComponentC extends CSenseComponentC {
    public QueueComponentC(BaseTypeC portType, int numElements)
	    throws CompilerException {
	super(QueueComponent.class);

	// add the generic types
	addGenericType(portType);

	// add the ports
	addInputPort(portType, "in0");
	addOutputPort(portType, "out");

	// add the arguments
	addArgument(new ArgumentC(numElements));
    }

}