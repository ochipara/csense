package components.math;


import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabParameter;
import compiler.types.BaseTypeC;

public class PowerSpectrumDensityC extends MatlabComponentC {

    public PowerSpectrumDensityC(BaseTypeC messageType)
	    throws CompilerException {
	super("PSDM");

	addIOPort(messageType, "data");

	MatlabParameter arg1 = new MatlabParameter("arg1",
		MatlabArgument.INPUT_OUTPUT, getInputPort("dataIn"),
		getOutputPort("dataOut"));
	addMatlabInput(arg1);
	addMatlabOutput(arg1);
    }

}
