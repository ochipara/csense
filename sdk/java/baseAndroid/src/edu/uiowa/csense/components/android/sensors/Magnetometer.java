package edu.uiowa.csense.components.android.sensors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Magnetometer extends MotionSensor {

    public Magnetometer(TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "mag", Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_FASTEST);	
    }

}
