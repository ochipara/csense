package components.network.desktop;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;
import components.network.HTMLFormMessage;

public class HTTPFormUploadC extends CSenseComponentC {

    public HTTPFormUploadC(String url) throws CompilerException {
	super(HTMLFormUpload.class);
	BaseTypeC type = TypeInfoC.newJavaMessage(HTMLFormMessage.class);

	addInputPort(type, "in");
	addOutputPort(type, "out");

	addArgument(new ArgumentC(url));

	setThreadingOption(ThreadingOption.CSENSE);

	addPermission("android.permission.INTERNET");
	addPermission("android.permission.ACCESS_NETWORK_STATE");
    }
}
