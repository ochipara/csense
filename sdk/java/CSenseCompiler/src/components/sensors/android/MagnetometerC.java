package components.sensors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.FrameTypeC;

public class MagnetometerC extends CSenseSourceC {
    public MagnetometerC(FrameTypeC frameT) throws CompilerException {
	super("components.sensors.android.Magnetometer", frameT);
	addArgument(ArgumentC.self());	
	addOutputPort(frameT, "mag");
	addPermission("android.permission.WAKE_LOCK");
	setThreadingOption(ThreadingOption.ANDROID);	

	addResource("components.sensors.android.MotionSensor");
    }
}
