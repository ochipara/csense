package components.storage;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.matlab.FromMatFile;


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
