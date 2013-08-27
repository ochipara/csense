package components.sensors.android;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.FrameTypeC;

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