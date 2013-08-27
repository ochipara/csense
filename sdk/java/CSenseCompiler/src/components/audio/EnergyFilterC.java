package components.audio;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;

public class EnergyFilterC extends CSenseComponentC {

    public EnergyFilterC(BaseTypeC portType, double threshold,
	    int smoothingWindow) throws CompilerException {
	super(EnergyFilter.class);
	addInputPort(portType, "in");
	addOutputPort(portType, "above");
	addOutputPort(portType, "below");

	addArgument(new ArgumentC(threshold));
	addArgument(new ArgumentC(smoothingWindow));
    }
}
