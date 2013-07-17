package components.bluetooth.shimmer;

import messages.TypeInfo;
import messages.fixed.ShortMatrix;
import api.CSenseException;
import api.IMessagePool;

/**
 * Instead of encoding string names in the payload, ShimmerSensorData stores string names separately to facilitate access.
 * @author Farley Lai
 *
 */
public class ShimmerSensorData extends ShortMatrix {
    public static TypeInfo<ShimmerSensorData> getTypeInfo(int frameSize) {
	return new TypeInfo<ShimmerSensorData>(ShimmerSensorData.class, 2, 4, frameSize, true, false);
    }
    
    private String _moteName;
    private String _sensorName;
    private String _fullName;
    
    public ShimmerSensorData(IMessagePool<ShimmerSensorData> pool, TypeInfo<ShimmerSensorData> type) throws CSenseException {
	super(pool, type);
	_fullName = _moteName = _sensorName = "";
    }
    
    public String getMoteName() {
	return _moteName;
    }
    
    public String getSensorName() {
	return _sensorName;
    }

    private void setFullName(String delimiter) {
	_fullName = _moteName + delimiter + _sensorName;
    }

    public void setMoteName(String name) {
	if(name.equals(_moteName)) return;
	_moteName = name;
	setFullName("-");
    }
    
    public void setSensorName(String name) {
	if(name.equals(_sensorName)) return;
	_sensorName = name;
	setFullName("-");
    }
    
    @Override
    public String toString() {
	return _fullName;
    }
}