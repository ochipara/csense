package components.basic;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;


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
