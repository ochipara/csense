package components.bluetooth.shimmer;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.bluetooth.shimmer.ShimmerSensorData;

public class ShimmerSensorComponentC extends CSenseSourceC {
    static public final Byte SENSOR_ACCEL   = (byte) 0x80;
    static public final Byte SENSOR_GYRO    = 0x40;
    static public final Byte SENSOR_MAG     = 0x20;
    static public final Byte SENSOR_ECG     = 0x10;
    static public final Byte SENSOR_EMG     = 0x08;
    static public final Byte SENSOR_GSR     = 0x04;
    static public final Byte SENSOR_ANEX_A7 = 0x02;
    static public final Byte SENSOR_ANEX_A0 = 0x01;

    public static JavaTypeC type = TypeInfoC.newJavaMessage(ShimmerSensorData.class); 
    
    public ShimmerSensorComponentC(String prefix, int max, int sensors, int frameSize) throws CompilerException {
	super("components.bluetooth.shimmer.ShimmerSensorComponent", type);
	addArgument(ArgumentC.self());
	addArgument(new ArgumentC(prefix));
	addArgument(new ArgumentC(max));
	addArgument(new ArgumentC(sensors));
	addArgument(new ArgumentC(frameSize));
	for(int i = 0; i < max; i++) {
	    addOutputPort(type, "out" + i);	    
	}
	addPermission("android.permission.BLUETOOTH");
	addPermission("android.permission.BLUETOOTH_ADMIN");
	addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
	setThreadingOption(ThreadingOption.ANDROID);	
    }
    
}
