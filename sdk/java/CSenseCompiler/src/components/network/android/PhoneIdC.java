package components.network.android;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.model.Project;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;
import components.network.desktop.PhoneId;


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
