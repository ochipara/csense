package components.basic;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;


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