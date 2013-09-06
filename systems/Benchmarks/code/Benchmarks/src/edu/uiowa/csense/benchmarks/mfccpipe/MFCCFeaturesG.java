package edu.uiowa.csense.benchmarks.mfccpipe;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;
import components.basic.MemorySourceC;
import components.basic.SliceC;
import components.basic.TapComponentC;
import components.math.HammingC;
import components.math.PowerSpectrumDensityC;
import components.matlab.MFCCComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.matlab.types.MLFloatMatrix;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.compiler.types.constraints.Equal;
import edu.uiowa.csense.compiler.utils.MatlabCommand;


public class MFCCFeaturesG extends CSenseGroupC {
    private FrameTypeC frameType;
    private PipelineConfig config;
    private final FrameTypeC mfccType;
    private final FrameTypeC fftType;

    public MFCCFeaturesG(FrameTypeC frameType, PipelineConfig config) throws CompilerException {
	this(frameType, config, false);
    }

    public MFCCFeaturesG(FrameTypeC frameType, PipelineConfig config, boolean useFloats) throws CompilerException {
	super("FeaturesG");
	this.frameType = frameType;
	this.config = config;

	if (useFloats == false) {
	    mfccType = TypeInfoC.newDoubleVector(config.melComponents() - 1);
	    mfccType.setConstraint(new Equal(config.melComponents() - 1));
	    mfccType.mapToJNI(new MLDoubleMatrix(1, config.melComponents() - 1));

	    fftType = TypeInfoC.newDoubleVector(config.getFftLength());
	    fftType.setConstraint(new Equal(config.getFftLength()));
	    fftType.mapToJNI(new MLDoubleMatrix(1, config.getFftLength()));
	} else {

	    mfccType = TypeInfoC.newFloatVector(config.melComponents() - 1);
	    mfccType.setConstraint(new Equal(config.melComponents() - 1));
	    mfccType.mapToJNI(new MLFloatMatrix(1, config.melComponents() - 1));

	    fftType = TypeInfoC.newFloatVector(config.getFftLength());
	    fftType.setConstraint(new Equal(config.getFftLength()));
	    fftType.mapToJNI(new MLFloatMatrix(1, config.getFftLength()));
	}

	addInputPort(frameType, "audio");
	addOutputPort(mfccType, "mfcc");
    }

    @Override
    public void instantiate() throws CompilerException {
	try {
	    /**
	     * Precompute the melbank filters
	     */
	    MatlabCommand cmd = new MatlabCommand();	    
	    cmd.command("[mb_filter, mb_a, mb_b] = melbankm(" +
		    config.getNumFilters() + "," +
		    config.getFftLength() + "," + 
		    config.getFrequency() + ");");
	    cmd.command("mb_filter = full(mb_filter);");

	    MatlabTypeConverter processor = new MatlabTypeConverter(cmd.getProxy());

	    double a = processor.getNumericArray("mb_a").getRealValue(0);
	    double b = processor.getNumericArray("mb_b").getRealValue(0);

	    MatlabNumericArray filter = processor.getNumericArray("mb_filter");
	    MLDoubleMatrix mb_filter = MLDoubleMatrix.fromDoubles(filter.getLengths(), filter.getRealArray2D());

	    cmd.command("clear");
	    cmd.disconnect();

	    /**
	     * Connect the rest of the components
	     */
	    addComponent("applyHamming", new HammingC(frameType));
	    addComponent("psd", new PowerSpectrumDensityC(frameType));
	    //addComponent("slicer", new SliceC(frameType, getFftType(), 0, config.getFftLength()));
	    addComponent("slicer", new SliceC(frameType, frameType, 0, config.getFftLength()));
	    addComponent("mfcc", new MFCCComponentC(frameType, mfccType, config.melComponents(), mb_filter, (int) a, (int) b));
	    addComponent("mfccSource", new MemorySourceC(mfccType));
	    addComponent("frameTap", new TapComponentC(frameType));

	    link("::audio", "applyHamming");
	    link("applyHamming", "psd");
	    link("psd", "slicer");
	    link("slicer", "mfcc::psdIn");

	    link("mfccSource", "mfcc::mfccIn");        
	    link("mfcc::psdOut", "frameTap");
	    link("mfcc::mfccOut", "::mfcc");
	} catch (MatlabInvocationException e) {
	    e.printStackTrace();
	}

    }

    public FrameTypeC mfccType() {
	return mfccType;
    }

    public FrameTypeC getFftType() {
	return fftType;
    }

    public FrameTypeC getMfccType() {
	return mfccType;
    }
}
