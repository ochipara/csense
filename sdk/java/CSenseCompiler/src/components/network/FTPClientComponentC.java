package components.network;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.runtime.types.FilenameType;


public class FTPClientComponentC extends CSenseComponentC {
    public FTPClientComponentC(String lwd, String host, String user,
	    String pass, String rwd) throws CompilerException {
	this(lwd, host, 21, user, pass, rwd);
    }

    public FTPClientComponentC(String lwd, String host, int port, String user,
	    String pass, String rwd) throws CompilerException {
	setComponent("components.network.FTPClientComponent");
	addArgument(new ArgumentC(lwd));
	addArgument(new ArgumentC(host));
	addArgument(new ArgumentC(port));
	addArgument(new ArgumentC(user));
	addArgument(new ArgumentC(pass));
	addArgument(new ArgumentC(rwd));
	addInputPort(TypeInfoC.newJavaMessage(FilenameType.class), "in");
	addOutputPort(TypeInfoC.newJavaMessage(FilenameType.class), "out");
	addPermission("android.permission.INTERNET");
    }
}
