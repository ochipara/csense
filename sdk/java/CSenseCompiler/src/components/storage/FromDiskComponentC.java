package components.storage;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.storage.FromDiskComponent;
import edu.uiowa.csense.runtime.workspace.Variable;

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
