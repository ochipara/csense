package components.matlab;

import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabConstant;
import compiler.matlab.MatlabParameter;
import compiler.matlab.MatlabPersistent;
import compiler.matlab.types.MatlabStruct;
import compiler.types.BaseTypeC;

public class EstimateNoiseC extends MatlabComponentC {

    public EstimateNoiseC(BaseTypeC messageType, String configuration)
	    throws CompilerException {
	super("EstimateNoiseM");

	// define the ports
	addIOPort(messageType, "noise");
	addIOPort(messageType, "spectrum");

	// matlab specific configuration
	addMatlabInput(new MatlabParameter("yf", MatlabArgument.INPUT,
		getInputPort("spectrumIn"), getOutputPort("spectrumOut")));

	MatlabStruct local = MatlabStruct
		.loadFromMat(configuration, "en_local");
	MatlabPersistent localPersistent = new MatlabPersistent("local", local);
	addMatlabInput(localPersistent);

	// matlab configuration
	MatlabStruct param_estimate_noise = MatlabStruct.loadFromMat(
		configuration, "en_params");
	addMatlabInput(new MatlabConstant("param_estimate_noise",
		param_estimate_noise));

	// output
	addMatlabOutput(new MatlabParameter("x", MatlabArgument.OUTPUT,
		getInputPort("noiseIn"), getOutputPort("noiseOut")));
	addMatlabOutput(localPersistent);
    }
}
