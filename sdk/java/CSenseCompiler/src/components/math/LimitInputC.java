package components.math;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabConstant;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.types.BaseTypeC;

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
