package components.monitors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.JavaTypeC;
import compiler.types.TypeInfoC;

public class BatteryMonitorC extends CSenseSourceC {
    public static final JavaTypeC batteryT = TypeInfoC.newJavaMessage(BatteryMonitorMessage.class); 
    public BatteryMonitorC(long monitoringPeriodMs) throws CompilerException {
	super(BatteryMonitor.class, batteryT);
	addOutputPort(batteryT, "out");
		
	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(monitoringPeriodMs));
    }
}
