package components.basic;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.BaseTypeC;
import components.basic.DisplayFrame;


public class DisplayFrameC extends CSenseComponentC {
    public DisplayFrameC(BaseTypeC type) throws CompilerException {
	super(DisplayFrame.class);
	addGenericType(type);

	addInputPort(type, "in");
	addOutputPort(type, "out");

	addTypeInfoArgument(type);
	// addArgument(new ArgumentC(String.class, val));
    }
}
