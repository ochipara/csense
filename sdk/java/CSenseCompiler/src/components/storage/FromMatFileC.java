package components.storage;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import components.matlab.FromMatFile;


public class FromMatFileC extends CSenseComponentC {

    public FromMatFileC(BaseTypeC portType, String fileName, String varName)
	    throws CompilerException {
	super(FromMatFile.class);

	addGenericType(portType);

	addOutputPort(portType, "out");

	addTypeInfoArgument(portType);
	// addArgument(new ArgumentC(portType));
	addArgument(new ArgumentC(fileName));
	addArgument(new ArgumentC(varName));

	addPermission("android.permission.READ_EXTERNAL_STORAGE");
    }

}
