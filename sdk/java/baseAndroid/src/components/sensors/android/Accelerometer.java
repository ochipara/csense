package components.sensors.android;

import messages.TypeInfo;
import messages.fixed.FloatMatrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Context;
import api.CSenseException;


/**
 * Collects acceleration values and the related timestamp and pushes them on to the queue for further processing by the gait component and storage.
 * @author hasanshabih, austin
 * */
public class Accelerometer extends MotionSensor {	
 
    public Accelerometer (TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "acc", Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
