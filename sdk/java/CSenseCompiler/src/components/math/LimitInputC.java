package components.math;


import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabConstant;
import compiler.matlab.MatlabParameter;
import compiler.matlab.types.MLDoubleMatrix;
import compiler.types.BaseTypeC;

public class LimitInputC extends MatlabComponentC {

    public LimitInputC(BaseTypeC type, double lower, double upper)
	    throws CompilerException {
	super("LimitInputM");

	addIOPort(type, "data");

	MatlabParameter x = new MatlabParameter("x",
		MatlabArgument.INPUT_OUTPUT, getInputPort("dataIn"),
		getOutputPort("dataOut"));
	addMatlabInput(x);
	addMatlabOutput(x);
	addMatlabInput(new MatlabConstant("lower", new MLDoubleMatrix(lower)));
	addMatlabInput(new MatlabConstant("upper", new MLDoubleMatrix(upper)));
    }

}
