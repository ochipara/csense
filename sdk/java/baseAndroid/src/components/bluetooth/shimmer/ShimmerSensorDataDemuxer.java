package components.bluetooth.shimmer;

import messages.fixed.ShortMatrix;
import api.CSenseComponent;
import api.CSenseException;
import api.CSenseRuntimeException;
import api.IInPort;
import api.IOutPort;

public class ShimmerSensorDataDemuxer extends CSenseComponent {
    public final IInPort<ShimmerSensorData> in;
    public final IOutPort<ShortMatrix> mag;
    public final IOutPort<ShortMatrix> acc;
    public final IOutPort<ShortMatrix> gyro;

    public ShimmerSensorDataDemuxer() throws CSenseException {
	in = newInputPort(this, "in");
	mag = newOutputPort(this, "mag");
	acc = newOutputPort(this, "acc");
	gyro = newOutputPort(this, "gyro");
    }
    
    @Override
    public void doInput() throws CSenseException {
	ShimmerSensorData m = in.getMessage();
	if(m.getSensorName() == ShimmerSensorComponent.getSensorName(ShimmerSensorComponent.SENSOR_ACCEL))
	    acc.push(m);
	else if(m.getSensorName() == ShimmerSensorComponent.getSensorName(ShimmerSensorComponent.SENSOR_GYRO))
	    gyro.push(m);
	else if(m.getSensorName() == ShimmerSensorComponent.getSensorName(ShimmerSensorComponent.SENSOR_MAG))
	    mag.push(m);
	else
	    throw new CSenseRuntimeException("unknown Shimmer sensor type");
    }
}
