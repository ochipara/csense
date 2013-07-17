package components.basic;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import components.basic.CopyRefComponent;


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
