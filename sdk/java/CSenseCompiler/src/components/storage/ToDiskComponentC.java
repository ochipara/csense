package components.storage;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.types.constraints.SFGreaterEqual;
import edu.uiowa.csense.components.storage.ToDiskComponent;
import edu.uiowa.csense.runtime.workspace.Variable;

public class ToDiskComponentC extends CSenseComponentC {
    public static int SPLIT_BY_FILESIZE = ToDiskComponent.SPLIT_BY_FILESIZE;
    public static int SPLIT_BY_COUNT = ToDiskComponent.SPLIT_BY_INVOCATION_COUNT;

    public ToDiskComponentC(BaseTypeC type, String path, String fileName,
	    String extension, int splitType, long splitSize)
	    throws CompilerException {
	super(ToDiskComponent.class);
	addGenericType(type);

	// ports
	addIOPort(type, "data");
	addOutputPort(TypeInfoC.newFilenameType(), "fileOutput", true);

	addArgument(new ArgumentC(path));
	addArgument(new ArgumentC(fileName));
	addArgument(new ArgumentC(extension));
	addArgument(new ArgumentC(splitType));
	addArgument(new ArgumentC(splitSize));

	addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public ToDiskComponentC(BaseTypeC type, String fileName) throws CompilerException {
	this(type, fileName, false);
    }
    
    public ToDiskComponentC(BaseTypeC type, String fileName, boolean append) throws CompilerException {
	super(ToDiskComponent.class);
	addGenericType(type);

	// ports
	addIOPort(type, "data");

	addArgument(new ArgumentC(fileName));
	addArgument(new ArgumentC(append));
	addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public ToDiskComponentC(BaseTypeC type, Variable variable)
	    throws CompilerException {
	super(ToDiskComponent.class);
	addGenericType(type);

	// ports
	addIOPort(type, "data");
	addOutputPort(TypeInfoC.newFilenameType(), "fileOutput", true);

	addArgument(new ArgumentC(variable));
	addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
    }

}
