package components.basic;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;

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