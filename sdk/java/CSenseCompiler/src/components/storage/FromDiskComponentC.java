package components.storage;

import base.workspace.Variable;
import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import components.storage.FromDiskComponent;

public class FromDiskComponentC extends CSenseComponentC {
    public FromDiskComponentC(BaseTypeC portType, Variable variable) throws CompilerException {
	super(FromDiskComponent.class);
	addOutputPort(portType, "out");
	addGenericType(portType);
	
	
	addTypeInfoArgument(portType);
	addArgument(new ArgumentC(variable));
	
	addPermission("android.permission.READ_EXTERNAL_STORAGE");
    }
    
    
    public FromDiskComponentC(BaseTypeC portType, String filename) throws CompilerException {
  	super(FromDiskComponent.class);
  	addOutputPort(portType, "out");
  	addGenericType(portType);
  	
  	
  	addTypeInfoArgument(portType);
  	addArgument(new ArgumentC(filename));
  	
  	addPermission("android.permission.READ_EXTERNAL_STORAGE");
      }
}
