package components.network.android;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.network.HTMLFormMessage;


public class HTTPFormUploadC extends CSenseComponentC {

    public HTTPFormUploadC(String url, long[] timeoutsMs) throws CompilerException {
	super("edu.uiowa.csense.components.android.network.HTTPFormUpload3");
	BaseTypeC type = TypeInfoC.newJavaMessage(HTMLFormMessage.class);
	addIOPort(type, "data");
	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(url));
	addArgument(new ArgumentC(timeoutsMs));
	addPermission("android.permission.INTERNET");
	addPermission("android.permission.ACCESS_NETWORK_STATE");
	setThreadingOption(ThreadingOption.CSENSE);
	
	
	addResource("edu.uiowa.csense.components.android.network.HTTPFormUpload3");
	addResource("edu.uiowa.csense.components.network.HTMLFormMessage");
    }
    
    
    public HTTPFormUploadC(String url) throws CompilerException {
	this(url, new long[] {1000, 1000, 2000, 2000, 5000, 5000, 10000, 10000} );
    }
}
