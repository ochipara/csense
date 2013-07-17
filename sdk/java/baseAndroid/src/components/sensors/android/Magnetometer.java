package components.sensors.android;

import messages.TypeInfo;
import messages.fixed.FloatMatrix;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import api.CSenseException;

public class Magnetometer extends MotionSensor {

    public Magnetometer(TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "mag", Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_FASTEST);	
    }

}
