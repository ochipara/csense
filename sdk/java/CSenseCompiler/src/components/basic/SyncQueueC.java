package components.basic;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.model.Project;
import compiler.types.BaseTypeC;

public class SyncQueueC extends CSenseComponentC {

    public SyncQueueC(BaseTypeC portType, int capacity) throws CompilerException {
	String api = Project.getProject().getConfiguration().getApi();
	if ("v2".equals(api)) {
	    setComponent(ABQSyncQueue.class);
	    Project.getProject().getResourceManager().addClass(ABQSyncQueue.class);
	} else if ("v3".equals(api)) {
	    setComponent(CBQSyncQueue.class);
	    Project.getProject().getResourceManager().addClass(CBQSyncQueue.class);
	} else throw new CompilerException("Invalid version" + api);	

	// add the generic types
	addGenericType(portType);

	// add the ports
	addIOPort(portType, "data");

	// add argument
	addArgument(new ArgumentC(capacity));
    }

}
