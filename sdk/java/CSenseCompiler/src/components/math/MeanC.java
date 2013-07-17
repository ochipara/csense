package components.math;


import compiler.CompilerException;
import compiler.matlab.MatlabArgument;
import compiler.matlab.MatlabComponentC;
import compiler.matlab.MatlabParameter;
import compiler.types.BaseTypeC;

public class MeanC extends MatlabComponentC {

    public MeanC(BaseTypeC frameT, BaseTypeC meanT) throws CompilerException {
	super("MeanM");

	// create the ports for the component
	addInputPort(frameT, "in");
	addInputPort(meanT, "meanIn");

	addOutputPort(frameT, "out");
	addOutputPort(meanT, "meanOut");

	// map the ports to the matlab function
	addMatlabInput(new MatlabParameter("y", MatlabArgument.INPUT,
		getInputPort("in"), getOutputPort("out")));
	addMatlabOutput(new MatlabParameter("m", MatlabArgument.OUTPUT,
		getInputPort("meanIn"), getOutputPort("meanOut")));
    }

}
