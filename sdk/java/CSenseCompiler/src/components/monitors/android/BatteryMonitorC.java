package components.monitors.android;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.android.monitors.BatteryMonitor;
import edu.uiowa.csense.components.android.monitors.BatteryMonitorMessage;

public class BatteryMonitorC extends CSenseSourceC {
    public static final JavaTypeC batteryT = TypeInfoC.newJavaMessage(BatteryMonitorMessage.class); 
    public BatteryMonitorC(long monitoringPeriodMs) throws CompilerException {
	super(BatteryMonitor.class, batteryT);
	addOutputPort(batteryT, "out");
		
	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(monitoringPeriodMs));
    }
}
