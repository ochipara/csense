package components.basic;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.basic.Merge;

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
