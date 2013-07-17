package components.storage;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

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
