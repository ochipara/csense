package components.bluetooth.shimmer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import components.bluetooth.BluetoothCommand;
import components.bluetooth.BluetoothCommand.Type;
import components.bluetooth.android.BluetoothClientComponent;
import components.bluetooth.android.BluetoothClientService;
import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;


/**
 * A Bluetooth client component implementing the protocol to access Shimmer sensor devices.
 * @author Farley Lai
 *
 */
public class ShimmerSensorComponent extends BluetoothClientComponent<RawFrame, ShimmerSensorData> {	
    // packet type definitions
    static private final byte DATA_PACKET 		= 0x00;
    static private final byte SET_SAMPLING_RATE_COMMAND = 0x05;
    static private final byte START_STREAMING_COMMAND   = 0x07;
    static private final byte SET_SENSORS_COMMAND 	= 0x08;
    static private final byte SET_ACCEL_RANGE_COMMAND   = 0x09;
    static private final byte STOP_STREAMING_COMMAND    = 0x20;
    static private final byte ACK_COMMAND_PROCESSED     = (byte) 0xFF;
    
    // sensor type definitions
    static public final Byte SENSOR_ACCEL   = (byte) 0x80;
    static public final Byte SENSOR_GYRO    = 0x40;
    static public final Byte SENSOR_MAG     = 0x20;
    static public final Byte SENSOR_ECG     = 0x10;
    static public final Byte SENSOR_EMG     = 0x08;
    static public final Byte SENSOR_GSR     = 0x04;
    static public final Byte SENSOR_ANEX_A7 = 0x02;
    static public final Byte SENSOR_ANEX_A0 = 0x01;

    // sampling rate definitions
    static public final byte SAMPLING_50HZ  = 0x14; // 51.20Hz
    static public final byte SAMPLING_10HZ  = 0x64; // 10.24Hz

    // accelerometer range definitions
    static public final byte RANGE_1_5G  = 0;
    static public final byte RANGE_2_0G  = 1;  // 7260 only
    static public final byte RANGE_4_0G  = 2;  // 7260 only
    static public final byte RANGE_6_0G  = 3;

    // thread-specific sensor data message storage
    private class SensorData extends ThreadLocal<ShimmerSensorData> {
	@Override
	protected ShimmerSensorData initialValue() {
	    return getNextMessageToWriteInto();
	}
    }

    private List<Map<Byte, SensorData>> _shimmerStorage;
    private byte _sensors;

    /**
     * Constructs a Shimmer sensor component with necessary information.
     * @param context the application context to access Android Bluetooth radio
     * @param prefix the prefix of remote Bluetooth devices
     * @param max the maximum number of Bluetooth devices to connect
     * @param sensors types of sensor data to collect
     * @throws CSenseException if IOutPort.push() fails
     */
    public ShimmerSensorComponent(TypeInfo<ShimmerSensorData> type, Context context, String prefix, int max, int sensors, int frameSize) throws CSenseException {
	super(ShimmerSensorData.getTypeInfo(frameSize), context, prefix, max, 64);
//	super(ShimmerSensorData.getTypeInfo(50 * getSensorSampleSizeInBytes(SENSOR_ACCEL)), context, prefix, max, 64);
//	super(type, context, prefix, max, 64);
	_sensors = (byte)(sensors & 0xFF);
	_shimmerStorage = new ArrayList<Map<Byte, SensorData>>();
	for(int i = 0; i < max; i++) {
	    Map<Byte, SensorData> storage = new TreeMap<Byte, SensorData>();
	    if(hasSensor(SENSOR_ACCEL)) storage.put(SENSOR_ACCEL, new SensorData());
	    if(hasSensor(SENSOR_GYRO)) storage.put(SENSOR_GYRO, new SensorData());
	    if(hasSensor(SENSOR_MAG)) storage.put(SENSOR_MAG, new SensorData());
	    _shimmerStorage.add(storage);
	}
    }

    public static String getSensorName(byte sensor) {
	if(sensor == SENSOR_ACCEL) return "accel";
	if(sensor == SENSOR_GYRO) return "gyro";
	if(sensor == SENSOR_MAG) return "mag";
	return "sensor";
    }

    private boolean hasSensor(byte sensor) {
	return (_sensors & sensor) == sensor;
    }

    private int getSensorCount() {
	int count = 0;
	if(hasSensor(SENSOR_ACCEL)) count ++;
	if(hasSensor(SENSOR_GYRO)) count ++;
	if(hasSensor(SENSOR_MAG)) count ++;
	return count;
    }
    
    public static int getSensorSampleSizeInBytes(Byte sensor) {
	// short timestamp + 3 * short samples for each axis
	return 2 + 3 * 2;
    }

    private int getDataPacketSize() {
	// type + timestamp + channels * short * sensors
	return 1 + 2 + 3 * 2 * getSensorCount();
    }
    
    @Override
    public synchronized void onClientConnected(BluetoothClientService service) {
	if(getConnections() > getMaxConnections()) {
	    warn("exceeding max connections, drop one");
	    service.stop();
	    return;
	} else
	    registerChannel(service);

	info("send SET_SENSORS_COMMAND");
	BluetoothCommand cmd = service.getCommand();
	cmd.setType(Type.RAW);
	cmd.putByte(SET_SENSORS_COMMAND);
	cmd.putByte(_sensors);
	cmd.putByte((byte) 0x00);
	cmd.flip();
	cmd.bytesToRead(1);
	service.send(cmd);
    }

    @Override
    public void onDisconnect(BluetoothClientService service) {
	info("send STOP_STREAMING_COMMAND");
	BluetoothCommand cmd = service.getCommand();
	cmd.setType(Type.RAW);
	cmd.putByte(STOP_STREAMING_COMMAND);
	cmd.flip();
	cmd.bytesToRead(1);
	service.send(cmd);
    }

    @Override
    public void onResponse(BluetoothClientService service, BluetoothCommand prev, ByteBuffer response) {
	BluetoothCommand cmd = service.getCommand();
	byte pkt = response.get();
	if(pkt == ACK_COMMAND_PROCESSED) {
	    info("recv ACK_COMMAND_PROCESSED");	
	    int type = prev.getByte();
	    switch(type) {
	    case SET_SENSORS_COMMAND:
		info("send SET_ACCEL_RANGE_COMMAND");
		cmd.setType(Type.RAW);
		cmd.putByte(SET_ACCEL_RANGE_COMMAND);
		cmd.putByte(RANGE_1_5G);
		cmd.flip();
		cmd.bytesToRead(1);
		service.send(cmd);
		break;
	    case SET_ACCEL_RANGE_COMMAND:
		info("send SET_SAMPLING_RATE_COMMAND");
		cmd.setType(Type.RAW);
		cmd.putByte(SET_SAMPLING_RATE_COMMAND);
		cmd.putByte(SAMPLING_50HZ);
		cmd.flip();
		cmd.bytesToRead(1);
		service.send(cmd);
		break;
	    case SET_SAMPLING_RATE_COMMAND:
		info("send START_STREAMING_COMMAND");
		cmd.setType(Type.RAW);
		cmd.putByte(START_STREAMING_COMMAND);
		cmd.flip();
		cmd.bytesToRead(1);
		service.send(cmd);
		break;
	    case START_STREAMING_COMMAND:
		cmd.setType(Type.READ);
		cmd.bytesToRead(getDataPacketSize());
		service.send(cmd);
		Debug.logComponentReady(this);
		break;
	    case STOP_STREAMING_COMMAND:
		info("streaming stopped");
		break;
	    }
	} else if(pkt == DATA_PACKET) {
//	    debug("recv a data packet");
	    int channel = getChannel(service);
	    long timestamp = System.currentTimeMillis();
	    response.mark();
	    short timestampShort = response.getShort();
	    int timestampInt = timestampShort & 0xFFFF;
	    Map<Byte, SensorData> storage = _shimmerStorage.get(channel);
	    if(hasSensor(SENSOR_ACCEL)) {
		SensorData data = storage.get(SENSOR_ACCEL);
		ShimmerSensorData accel = data.get();
		if(accel == null) warn("failed to get a fresh message to save acc data, message pool too small?");
		else {
		    if(accel.position() == 0) {
			accel.setMoteName(service.getDevice().getName());
			accel.setSensorName(getSensorName(SENSOR_ACCEL));
			accel.setTimeStamp(timestamp);					
		    }

		    short ax = response.getShort();
		    short ay = response.getShort();
		    short az = response.getShort();
		    accel.put(timestampShort);
		    accel.put(ax);
		    accel.put(ay);
		    accel.put(az);
		    //		debug("read accl data (", ax, ay, az, ") at", timestampInt, "from", service.getDevice().getName());
		    if(accel.remaining() < getSensorSampleSizeInBytes(SENSOR_ACCEL)) {						
			accel.flip();				
			try {
			    _outs.get(channel).push(accel);
			} catch (CSenseException e) {
			    throw new CSenseRuntimeException(e);
			}
			data.remove();
		    } else data.set(accel);
		}
	    }

	    if(hasSensor(SENSOR_GYRO)) {
		SensorData data = storage.get(SENSOR_GYRO);
		ShimmerSensorData gyro = data.get();
		if(gyro == null) warn("failed to get a fresh message to save gyro data, message pool too small?");
		else {
		    if(gyro.position() == 0) {
			gyro.setMoteName(service.getDevice().getName());
			gyro.setSensorName(getSensorName(SENSOR_GYRO));
			gyro.setTimeStamp(timestamp);
		    }

		    short gx = response.getShort();
		    short gy = response.getShort();
		    short gz = response.getShort();
		    if(gx < 5 || gy < 5 || gz < 5) {
			error(String.format("detected weird Gyro data from %s with timestamp %d and GYRO(%d, %d, %d)", service.getDevice().getName(), timestampInt, gx, gy, gz));			
			fatal(true);
		    } else {
			gyro.putShort(timestampShort);
			gyro.putShort(gx);
			gyro.putShort(gy);
			gyro.putShort(gz);		
			//		    debug("read gyro data (", gx, gy, gz, ") at", timestampInt, "from", service.getDevice().getName());
			if(gyro.remaining() < getSensorSampleSizeInBytes(SENSOR_GYRO)) {
			    gyro.flip();
			    try {
				_outs.get(channel).push(gyro);
			    } catch (CSenseException e) {
				throw new CSenseRuntimeException(e);
			    }
			    data.remove();
			} else data.set(gyro);
		    }
		}
	    }
	    
	    if(hasSensor(SENSOR_MAG)) {
		SensorData data = storage.get(SENSOR_MAG);
		ShimmerSensorData mag = data.get();
		if(mag == null) warn("failed to get a fresh message to save mag data, message pool too small?");
		else {
		    if(mag.position() == 0) {
			mag.setMoteName(service.getDevice().getName());
			mag.setSensorName(getSensorName(SENSOR_MAG));
			mag.setTimeStamp(timestamp);
		    }

		    short mx = response.getShort();
		    short my = response.getShort();
		    short mz = response.getShort();
		    mag.putShort(timestampShort);
		    mag.putShort(mx);
		    mag.putShort(my);
		    mag.putShort(mz);		
		    debug("read mag data (", mx, my, mz, ") at", timestampInt, "from", service.getDevice().getName());
		    if(mag.remaining() < getSensorSampleSizeInBytes(SENSOR_MAG)) {
			mag.flip();
			try {
			    _outs.get(channel).push(mag);
			} catch (CSenseException e) {
			    throw new CSenseRuntimeException(e);
			}
			data.remove();
		    } else data.set(mag);
		}
	    }
	    
	    response.reset();
	    cmd.setType(Type.READ);
	    cmd.bytesToRead(getDataPacketSize());
	    service.send(cmd);
	} else 
	    warn(String.format("unknown packet type 0x%02X", pkt));		
    }
}
