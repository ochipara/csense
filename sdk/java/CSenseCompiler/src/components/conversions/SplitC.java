package components.conversions;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import components.conversions.Split;

public class SplitC extends CSenseComponentC {
    public SplitC(BaseTypeC type, int numSplits, int frameSize) throws CompilerException {
	super(Split.class);
	addGenericType(type);
	addIOPort(type, "data");
	addArgument(new ArgumentC(numSplits));
	addArgument(new ArgumentC(frameSize));
    }
}
