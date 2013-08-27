package components.monitors.android;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class DisplayMonitorC extends CSenseSourceC {
    public static final JavaTypeC displayT = TypeInfoC.newJavaMessage(DisplayMonitorMessage.class);
    public DisplayMonitorC() throws CompilerException {
	super(DisplayMonitor.class, displayT);
	addOutputPort(displayT, "out");
	
	addArgument(ArgumentC.self());	
    }
}
