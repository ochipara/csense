package components.bluetooth.shimmer;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.FrameTypeC;
import compiler.types.TypeInfoC;


// TODO: nicer way to handle this
// TODO: this works because we want the analysis algorithms to behave as if they saw a CopyRefC
//public class ShimmerSensorDataDemuxerC extends CSenseComponentC {
public class ShimmerSensorDataDemuxerC extends CSenseComponentC {    
    public ShimmerSensorDataDemuxerC(FrameTypeC rawT) throws CompilerException {
	super(ShimmerSensorDataDemuxer.class);
	addInputPort(TypeInfoC.newJavaMessage(ShimmerSensorData.class), "in");
	addOutputPort(rawT, "acc").setSource(true);
	addOutputPort(rawT, "gyro").setSource(true);
	addOutputPort(rawT, "mag").setSource(true);
    }
    
    
}
