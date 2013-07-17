package components.sensors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

public class GPSComponentC extends CSenseSourceC {
    public final static BaseTypeC gpsType = TypeInfoC.newJavaMessage(GPSMessage.class);

    public GPSComponentC() throws CompilerException {
	super("components.sensors.android.GPSComponent", gpsType);

	addOutputPort(gpsType, "out");
	addArgument(ArgumentC.self());
	addPermission("android.permission.ACCESS_FINE_LOCATION");
	setThreadingOption(ThreadingOption.CSENSE);
    }

}
