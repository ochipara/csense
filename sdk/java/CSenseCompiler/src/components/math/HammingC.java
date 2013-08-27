package components.math;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.types.BaseTypeC;

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
