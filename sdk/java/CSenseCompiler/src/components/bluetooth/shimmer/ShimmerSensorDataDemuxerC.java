package components.bluetooth.shimmer;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;


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
