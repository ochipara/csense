package components.network.desktop;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.network.HTMLFormMessage;
import edu.uiowa.csense.components.network.desktop.HTMLFormUpload;

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
