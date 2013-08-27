package components.basic;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.basic.CopyRefComponent;


public class CopyRefC extends CSenseComponentC {
    protected CopyRefC(Class cls) {
	super(cls);
    }

    public CopyRefC(BaseTypeC portType, int fanout) throws CompilerException {
	super(CopyRefComponent.class);

	// create the appropriate ports
	addInputPort(portType, "in");
	for (int i = 0; i < fanout; i++) {
	    addOutputPort(portType, "out" + i);
	}
	addGenericType(portType);

	// addArgument(new ArgumentC(portType));
	addArgument(new ArgumentC(fanout));
    }

}
