package components.network.android;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;
import components.network.HTMLFormMessage;


public class HTTPFormUploadC extends CSenseComponentC {

    public HTTPFormUploadC(String url, long[] timeoutsMs) throws CompilerException {
	super("components.network.android.HTTPFormUpload2");
	BaseTypeC type = TypeInfoC.newJavaMessage(HTMLFormMessage.class);
	addIOPort(type, "data");
	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(url));
	addArgument(new ArgumentC(timeoutsMs));
	addPermission("android.permission.INTERNET");
	addPermission("android.permission.ACCESS_NETWORK_STATE");
	setThreadingOption(ThreadingOption.CSENSE);
    }
    
    
    public HTTPFormUploadC(String url) throws CompilerException {
	this(url, new long[] {1000, 1000, 2000, 2000, 5000, 5000, 10000, 10000} );
    }
}
