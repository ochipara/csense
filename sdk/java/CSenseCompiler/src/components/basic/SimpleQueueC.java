package components.basic;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;

public class SimpleQueueC extends CSenseComponentC {
    public SimpleQueueC(BaseTypeC portType, int numElements)
	    throws CompilerException {
	super(SimpleQueue.class);

	// add the generic types
	addGenericType(portType);

	// add the ports
	addInputPort(portType, "in");
	addOutputPort(portType, "out");

	// add the arguments
	addArgument(new ArgumentC(numElements));
    }

}