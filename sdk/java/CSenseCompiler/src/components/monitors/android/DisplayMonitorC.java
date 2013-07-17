package components.monitors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.JavaTypeC;
import compiler.types.TypeInfoC;

public class DisplayMonitorC extends CSenseSourceC {
    public static final JavaTypeC displayT = TypeInfoC.newJavaMessage(DisplayMonitorMessage.class);
    public DisplayMonitorC() throws CompilerException {
	super(DisplayMonitor.class, displayT);
	addOutputPort(displayT, "out");
	
	addArgument(ArgumentC.self());	
    }
}
