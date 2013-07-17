package components.math;


import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabParameter;
import compiler.types.BaseTypeC;

/**
 * Creates the HammingC component
 * 
 * @author ochipara
 * 
 */
public class HammingC extends MatlabComponentC {

    public HammingC(BaseTypeC messageType) throws CompilerException {
	super("HammingM");

	addIOPort(messageType, "data");
	MatlabParameter arg1 = new MatlabParameter("arg1",
		MatlabArgument.INPUT_OUTPUT, getInputPort("dataIn"),
		getOutputPort("dataOut"));
	addMatlabInput(arg1);
	addMatlabOutput(arg1);
    }
}
