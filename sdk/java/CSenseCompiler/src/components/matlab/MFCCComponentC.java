package components.matlab;

import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabConstant;
import compiler.matlab.MatlabParameter;
import compiler.matlab.types.MLDoubleMatrix;
import compiler.matlab.types.MLIntegerMatrix;
import compiler.types.FrameTypeC;
import compiler.types.PrimitiveType;

public class MFCCComponentC extends MatlabComponentC {
    public MFCCComponentC(FrameTypeC frameType, FrameTypeC mfccType,
	    int melComponents, MLDoubleMatrix filter, int a, int b)
	    throws CompilerException {
	super("MFCCM");
	if (mfccType.getBaseType().getType() == PrimitiveType.PRIMITIVE_FLOAT) {
	    setComponent("MFCCM_single");
	}

	// define the ports
	addIOPort(frameType, "psd");
	addIOPort(mfccType, "mfcc");

	// matlab specific configuration
	MatlabParameter psd = new MatlabParameter("psd", MatlabArgument.INPUT,
		getInputPort("psdIn"), getOutputPort("psdOut"));
	addMatlabInput(psd);

	MatlabParameter mfcc = new MatlabParameter("mfcc",
		MatlabArgument.OUTPUT, getInputPort("mfccIn"),
		getOutputPort("mfccOut"));
	addMatlabOutput(mfcc);
	addMatlabInput(new MatlabConstant("ncomponents", new MLIntegerMatrix(melComponents)));
	addMatlabInput(new MatlabConstant("filter", filter));
	addMatlabInput(new MatlabConstant("a", new MLIntegerMatrix(a)));
	addMatlabInput(new MatlabConstant("b", new MLIntegerMatrix(b)));
    }

}
