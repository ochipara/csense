package components.bluetooth.shimmer;

import java.util.List;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.components.bluetooth.shimmer.ShimmerSensorData;

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
