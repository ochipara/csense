package components.matlab;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabConstant;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.matlab.types.MLIntegerMatrix;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.PrimitiveType;

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
