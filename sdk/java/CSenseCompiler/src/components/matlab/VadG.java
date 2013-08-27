package components.matlab;

import components.basic.MemorySourceC;
import components.basic.TapComponentC;
import components.math.DivideC;
import components.math.LimitInputC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class VadG extends CSenseGroupC {
    public static FrameTypeC _fftType;
    public static FrameTypeC activityType;
    public static String _location;

    static {
	activityType = TypeInfoC.newDoubleVector(1);

	// map the to JNI types
	activityType.mapToJNI(new MLDoubleMatrix(1, 1));
    }

    public VadG(FrameTypeC fftType, String location) throws CompilerException {
	super("VadG");
	// specify the types
	_fftType = fftType;
	_location = location;

	// add the ports you want to expose
	addIOPort(fftType, "spectrum");
	addOutputPort(activityType, "snr");
    }

    @Override
    public void instantiate() throws CompilerException {
	addComponent("noiseEstimator", new EstimateNoiseC(_fftType, _location));
	addComponent("noiseFrame", new MemorySourceC(_fftType));
	addComponent("divide", new DivideC(_fftType));
	addComponent("tapNoise", new TapComponentC(_fftType));
	addComponent("limit", new LimitInputC(_fftType, 1e-4, 1000));

	link("::spectrumIn", "noiseEstimator::spectrumIn");
	link("noiseFrame", "noiseEstimator::noiseIn");
	link("noiseEstimator::spectrumOut", "divide::xIn");
	link("noiseEstimator::noiseOut", "divide::yIn");
	link("noiseEstimator::noiseOut", "divide::zIn");

	link("divide::xOut", "::spectrumOut");
	link("divide::yOut", "tapNoise");
	link("divide::zOut", "limit");
	link("limit", "::snr");
    }
}
