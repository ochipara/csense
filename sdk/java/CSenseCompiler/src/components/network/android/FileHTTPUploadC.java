package components.network.android;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class FileHTTPUploadC extends CSenseComponentC {

    public FileHTTPUploadC(String url) throws CompilerException {
	super("components.network.android.FileHTTPUpload");

	FrameTypeC t = TypeInfoC.newCharVector(1024);
	addInputPort(t, "fileInput");
	addOutputPort(t, "fileOutput");

	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(url));

	addPermission("android.permission.INTERNET");
	addPermission("android.permission.ACCESS_NETWORK_STATE");
    }
}
