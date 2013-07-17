package components.basic;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;

public class MergeC extends CSenseComponentC {
    public MergeC(BaseTypeC portType, int fanin) throws CompilerException {
	super(Merge.class);

	// create the appropriate ports
	for (int i = 0; i < fanin; i++) {
	    addInputPort(portType, "in" + i);
	}
	addOutputPort(portType, "out");
	addGenericType(portType);

	addArgument(new ArgumentC(fanin));
    }
}
