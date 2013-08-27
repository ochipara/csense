package components.math;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabArgument;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.matlab.MatlabParameter;
import edu.uiowa.csense.compiler.types.BaseTypeC;

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
