package components.basic;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.basic.CBQSyncQueue;

public class SyncQueueC extends CSenseComponentC {

    public SyncQueueC(BaseTypeC portType, int capacity) throws CompilerException {
	String api = Project.getProject().getConfiguration().getApi();
	if ("v2".equals(api)) {
//	    setComponent(ABQSyncQueue.class);
//	    Project.getProject().getResourceManager().addClass(ABQSyncQueue.class);
	    throw new IllegalArgumentException();
	} else if ("v4".equals(api)) {
	    setComponent(CBQSyncQueue.class);
	    Project.getProject().getResourceManager().addClass(CBQSyncQueue.class);
	} else throw new CompilerException("Invalid version " + api);	

	// add the generic types
	addGenericType(portType);

	// add the ports
	addIOPort(portType, "data");

	// add argument
	addArgument(new ArgumentC(capacity));
    }

}
