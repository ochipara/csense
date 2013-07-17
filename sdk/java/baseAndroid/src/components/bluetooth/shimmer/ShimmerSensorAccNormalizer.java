package components.bluetooth.shimmer;

import messages.TypeInfo;
import messages.fixed.FloatMatrix;
import api.CSenseException;
import api.CSenseRuntimeException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;

public class ShimmerSensorAccNormalizer extends CSenseSource<FloatMatrix>{
    public static final int ACC_IDEA_G_MIN = 935;//1676;
    public static final int ACC_IDEA_G_MAX = 3107;//2420;
    public static final int ACC_IDEA_G_MID = (int)((ACC_IDEA_G_MIN + ACC_IDEA_G_MAX + 0.5) / 2); 
    public static final int ACC_SAMPLE_SIZE = ShimmerSensorComponent.getSensorSampleSizeInBytes(ShimmerSensorComponent.SENSOR_ACCEL);
    public final IInPort<ShimmerSensorData> rawIn = newInputPort(this, "rawIn");
    public final IOutPort<ShimmerSensorData> rawOut = newOutputPort(this, "rawOut");
    public final IOutPort<FloatMatrix> dataOut = newOutputPort(this, "dataOut");
    private FloatMatrix frame;
    
    public ShimmerSensorAccNormalizer(TypeInfo<FloatMatrix> frameT) throws CSenseException {
	super(frameT);
	if(frameT.getRows() < 4) throw new CSenseRuntimeException("frame size has to be at least 16");
    }

    public static float normalize(short value) {
	int offset = value - ACC_IDEA_G_MID;
	return 9.81f * offset / (ACC_IDEA_G_MAX - ACC_IDEA_G_MID);
    }

    public static short denormalize(float value) {
	return (short)(ACC_IDEA_G_MID + value * (ACC_IDEA_G_MAX - ACC_IDEA_G_MID) / 9.81f + 0.5f);
    }

    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();
    }
    
    @Override
    public void doInput() throws CSenseException {
	// call JNI to normalize an incoming short raw frame
//	ShimmerSensorData raw = rawIn.getMessage();
//	FloatMatrix frame = getNextMessageToWriteInto();
//	ShimmerSensorAccNormalizerMatlab.normalize(raw.getBuffer(), frame.getBuffer(), raw.capacity());
//	rawOut.push(raw);
//	dataOut.push(frame);
	
	if(frame == null) {
	    frame = getNextMessageToWriteInto();
//	    debug("frame capacity:", frame.remaining(), "floats");
	}
	
	ShimmerSensorData raw = rawIn.getMessage();
	int samples = raw.remaining() / ACC_SAMPLE_SIZE;
	for(int i = 0; i < samples; i++) {
	    float timestamp = raw.getShort() & 0xFFFF;
	    short rawX = raw.getShort();
	    short rawY = raw.getShort();
	    short rawZ = raw.getShort();
	    float x = normalize(rawX);
	    float y = normalize(rawY);
	    float z = normalize(rawZ);
	    frame.put(timestamp);
	    frame.put(x);
	    frame.put(y);
	    frame.put(z);
//	    debug(String.format("normalze(%d, %d, %d)->(%f, %f, %f) at %.0f", rawX, rawY, rawZ, x, y, z, timestamp));	    
	    if(frame.remaining() < ACC_SAMPLE_SIZE / 2) {
		frame.flip();
//		debug(String.format("push a normalized frame of %d float samples", frame.remaining()));
		dataOut.push(frame);
		frame = getNextMessageToWriteInto();
	    }
	}
	rawOut.push(raw);
    }
}
