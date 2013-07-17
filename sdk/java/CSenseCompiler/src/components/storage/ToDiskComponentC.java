package components.storage;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.FrameTypeC;
import compiler.types.TypeInfoC;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.SFGreaterEqual;
import components.storage.ToDiskComponent;

import base.workspace.Variable;

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
