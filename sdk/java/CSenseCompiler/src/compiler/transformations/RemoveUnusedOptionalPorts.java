package compiler.transformations;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import compiler.model.CSenseGroupC;
import compiler.model.OutputPortC;

import api.IComponentC;

public class RemoveUnusedOptionalPorts {
    protected static Logger logger = Logger
	    .getLogger(RemoveUnusedOptionalPorts.class);

    public static void removeUnsedOptionalPorts(CSenseGroupC main) {
	List<OutputPortC> toRemove = new LinkedList<OutputPortC>();
	for (IComponentC component : main.getComponents()) {
	    for (OutputPortC output : component.getOutputPorts()) {
		if (output.isOptional()) {
		    if (output.getOutgoing().size() == 0) {
			toRemove.add(output);
		    }
		}
	    }
	}

	for (OutputPortC output : toRemove) {
	    logger.info("removing " + output);

	    // remove the port
	    IComponentC component = output.getComponent();
	    component.removeOutput(output);
	}
    }
}
