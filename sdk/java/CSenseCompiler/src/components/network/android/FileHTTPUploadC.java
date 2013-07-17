package components.network.android;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.FrameTypeC;
import compiler.types.TypeInfoC;

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
