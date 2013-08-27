package components.storage;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.storage.DirectoryMonitorComponent;

public class DirectoryMonitorC extends CSenseComponentC {
    public BaseTypeC filenameType;

    public DirectoryMonitorC(String dir, String pattern)
	    throws CompilerException {
	super(DirectoryMonitorComponent.class);
	filenameType = TypeInfoC.newCharVector(1024);
	addOutputPort(filenameType, "fileName");

	addArgument(new ArgumentC(dir));
	addArgument(new ArgumentC(pattern));
    }

}
