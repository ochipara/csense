package components.math;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.types.BaseTypeC;

public class DivideC extends MatlabComponentC {

    public DivideC(BaseTypeC type) throws CompilerException {
	super("DivideM");

	addIOPort(type, "x");
	addIOPort(type, "y");
	addIOPort(type, "z");

	addMatlabInput(new MatlabParameter("m_x_in", MatlabArgument.INPUT,
		getInputPort("xIn"), getOutputPort("xOut")));
	addMatlabInput(new MatlabParameter("m_y_in", MatlabArgument.INPUT,
		getInputPort("yIn"), getOutputPort("yOut")));
	addMatlabOutput(new MatlabParameter("m_z_out", MatlabArgument.OUTPUT,
		getInputPort("zIn"), getOutputPort("zOut")));
    }
}
