package components.sensors.android;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.FrameTypeC;

public class GyroscopeC extends CSenseSourceC {
    public GyroscopeC(FrameTypeC frameT) throws CompilerException {
	super("components.sensors.android.Gyroscope", frameT);
	addArgument(ArgumentC.self());
	addOutputPort(frameT, "gyro");
	addPermission("android.permission.WAKE_LOCK");
	setThreadingOption(ThreadingOption.ANDROID);

	addResource("components.sensors.android.MotionSensor");
    }
}
