package components.sensors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.FrameTypeC;

public class AccelerometerC extends CSenseSourceC {
    public AccelerometerC(FrameTypeC frameT) throws CompilerException {
	super("components.sensors.android.Accelerometer", frameT);
	addArgument(ArgumentC.self());
	addOutputPort(frameT, "acc");
	addPermission("android.permission.WAKE_LOCK");
	setThreadingOption(ThreadingOption.ANDROID);
	
	addResource("components.sensors.android.MotionSensor");
    }

}