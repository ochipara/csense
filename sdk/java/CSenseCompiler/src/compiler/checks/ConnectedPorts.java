package compiler.checks;

import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.transformations.ExpandFanOut;


import api.IComponentC;

public class ConnectedPorts {
    protected static Logger logger = Logger.getLogger(ExpandFanOut.class);

    public static void checkConnections(CSenseGroupC group)
	    throws CompilerException {
	ComponentGraph cgraph = group.getComponentGraph();
	boolean errors = false;
	for (IComponentC component : cgraph.components()) {
	    for (InputPortC port : component.getInputPorts()) {
		if (port.getIncoming() == null) {
		    if (port.isOptional() == false) {
			logger.error("Component " + component.getVariableName()
				+ " has port " + port + " not connected!");
			System.err.println("Component "
				+ component.getVariableName() + " has port "
				+ port + " not connected!");
			errors = true;
		    } else {
			logger.warn("Component " + component.getVariableName()
				+ " has optional port " + port
				+ " not connected!");
		    }
		}
	    }

	    for (OutputPortC port : component.getOutputPorts()) {
		if (port.getOutgoing().size() == 0) {
		    if (port.isOptional() == false) {
			logger.error("Component " + component.getVariableName()
				+ " has port " + port + " not connected!");
			System.err.println("Component "
				+ component.getVariableName() + " has port "
				+ port + " not connected!");
			errors = true;
		    } else {
			logger.warn("Component " + component.getVariableName()
				+ " has optional port " + port
				+ " not connected!");
		    }
		}
	    }
	}

	if (errors) {
	    final String err = "Application graph failed to pass connection test.";
	    System.err.println();
	    System.err.println("Error: " + err);
	    System.err.println();
	    throw new CompilerException(err);
	}
    }

}
