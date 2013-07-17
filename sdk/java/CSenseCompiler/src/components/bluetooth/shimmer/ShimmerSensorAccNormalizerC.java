package components.bluetooth.shimmer;

import java.util.List;

import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.types.FrameTypeC;
import compiler.types.JavaTypeC;
import compiler.types.TypeInfoC;
import compiler.types.constraints.Constraint;

public class ShimmerSensorAccNormalizerC extends CSenseSourceC {        
    public ShimmerSensorAccNormalizerC(FrameTypeC frameT) throws CompilerException {
	super("components.bluetooth.shimmer.ShimmerSensorAccNormalizer", frameT);
	List<Constraint> constraints = frameT.getConstraints();
	JavaTypeC rawT = TypeInfoC.newJavaMessage(ShimmerSensorData.class);
	//for(Constraint c: constraints) rawT.addConstraint(c);
	addIOPort(rawT, "raw");
	addOutputPort(frameT, "dataOut");
    }
}
