package edu.uiowa.csense.components.android.sensors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Gyroscope extends MotionSensor {

    public Gyroscope(TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "gyro", Sensor.TYPE_GYROSCOPE, SensorManager.SENSOR_DELAY_FASTEST);
    }

}
