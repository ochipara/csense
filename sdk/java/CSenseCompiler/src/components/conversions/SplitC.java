package components.conversions;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;

public class SplitC extends CSenseComponentC {
    public SplitC(BaseTypeC type, int numSplits, int frameSize) throws CompilerException {
	super(Split.class);
	addGenericType(type);
	addIOPort(type, "data");
	addArgument(new ArgumentC(numSplits));
	addArgument(new ArgumentC(frameSize));
    }
}
