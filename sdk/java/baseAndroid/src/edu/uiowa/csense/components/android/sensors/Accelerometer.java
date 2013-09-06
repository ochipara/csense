package edu.uiowa.csense.components.android.sensors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Context;


/**
 * Collects acceleration values and the related timestamp and pushes them on to the queue for further processing by the gait component and storage.
 * @author hasanshabih, austin
 * */
public class Accelerometer extends MotionSensor {	
 
    public Accelerometer (TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "acc", Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
