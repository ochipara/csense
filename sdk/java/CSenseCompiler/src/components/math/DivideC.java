package components.math;


import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabParameter;
import compiler.types.BaseTypeC;

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
