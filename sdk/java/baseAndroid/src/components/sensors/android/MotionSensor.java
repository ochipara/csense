package components.sensors.android;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.compatibility.CSenseHandlerThread;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MotionSensor extends CSenseSource<FloatMatrix> implements SensorEventListener {
    public final OutputPort<FloatMatrix> output;

    protected final SensorManager _sensorManager;
    protected final Context _context;
    protected final int _sensorDelay, _sensorType;    
    protected int _counter;
    protected FloatMatrix _frame = null;
    protected final PowerManager _powerManger; 
    protected final WakeLock _wakeLock;
    protected final String _wakeLockTag;
    protected boolean initialized = false;
    
    private HandlerThread _handler;

    public MotionSensor (TypeInfo<FloatMatrix> type, Context context, String portName, int sensorType, int sensorDelay) throws CSenseException {
	super(type);
	_context = context;
	_sensorType = sensorType;
	_sensorDelay = sensorDelay;	
	_sensorManager = (SensorManager)_context.getSystemService(android.content.Context.SENSOR_SERVICE);
	_powerManger = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	_wakeLockTag = MotionSensor.class.getName() + "[" + sensorType + "]";
	_wakeLock = _powerManger.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, _wakeLockTag);
	output = newOutputPort(this, portName);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// do nothing		
    }

    @Override
    /**
     * Getting values from the Accelerometer Sensor.
     * @author hasanshabih
     */
    public void onSensorChanged(SensorEvent event) {	
	if (!initialized) return;
	if (_frame == null) _frame = getNextMessageToWriteInto();
	_frame.put(event.timestamp);		
	_frame.put(event.values[0]);
	_frame.put(event.values[1]);
	_frame.put(event.values[2]);				
//	debug("sensor reading (", event.values[0], event.values[1], event.values[2], ")");
	if (_frame.remaining() == 0) {
	    try {
		_frame.flip();
		output.push(_frame);
		_frame = null;
	    } catch (CSenseException e) {
		e.printStackTrace();
	    }
	}

    }

    @Override
    public void onStart() throws CSenseException {	
	_handler = new CSenseHandlerThread(getName() + "-handler");
	_handler.start();
	_sensorManager.registerListener(this, _sensorManager.getDefaultSensor(_sensorType), _sensorDelay, new Handler(_handler.getLooper()));
	_wakeLock.acquire();	
	initialized = true;
    }

    @Override
    public void onStop() throws CSenseException {	
	_sensorManager.unregisterListener(this);
	_handler.quit();
	_handler = null;
	_wakeLock.release();	
	super.onStop();
    }

}
