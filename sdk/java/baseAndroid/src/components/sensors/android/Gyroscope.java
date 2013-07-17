package components.sensors.android;

import messages.TypeInfo;
import messages.fixed.FloatMatrix;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import api.CSenseException;

public class Gyroscope extends MotionSensor {

    public Gyroscope(TypeInfo<FloatMatrix> type, Context context) throws CSenseException {
	super(type, context, "gyro", Sensor.TYPE_GYROSCOPE, SensorManager.SENSOR_DELAY_FASTEST);
    }

}
