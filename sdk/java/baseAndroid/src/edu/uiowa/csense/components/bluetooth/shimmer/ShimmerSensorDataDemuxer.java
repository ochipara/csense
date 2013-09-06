package edu.uiowa.csense.components.bluetooth.shimmer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

public class ShimmerSensorDataDemuxer extends CSenseComponent {
    public final InputPort<ShimmerSensorData> in;
    public final OutputPort<ShortMatrix> mag;
    public final OutputPort<ShortMatrix> acc;
    public final OutputPort<ShortMatrix> gyro;

    public ShimmerSensorDataDemuxer() throws CSenseException {
	in = newInputPort(this, "in");
	mag = newOutputPort(this, "mag");
	acc = newOutputPort(this, "acc");
	gyro = newOutputPort(this, "gyro");
    }
    
    @Override
    public void onInput() throws CSenseException {
	ShimmerSensorData m = in.getFrame();
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
