package components.network.android;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.network.desktop.PhoneId;


public class PhoneIdC extends CSenseComponentC {
    public static BaseTypeC type;

    public PhoneIdC() throws CompilerException {
	type = TypeInfoC.newCharVector(16);
	if ("desktop".equals(Project.getPlatform())) {
	    setComponent(PhoneId.class);
	} else if ("android".equals(Project.getPlatform())) {
	    setComponent("components.network.android.PhoneId");
	    addArgument(ArgumentC.self());
	} else
	    throw new CompilerException("Unsupported platform ["
		    + Project.getPlatform() + "]");

	addOutputPort(PhoneIdC.type, "out");
    }
}
