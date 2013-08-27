package edu.uiowa.csense.compiler.transformations;

import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class ExpandGroup {
    protected static Logger logger = Logger.getLogger(ExpandGroup.class);

    public static void expandGroup(ComponentGraph graph) throws CompilerException {
	boolean hasGroups = true;

	while (hasGroups) {
	    hasGroups = false;
	    for (IComponentC group : graph.components()) {
		if (group instanceof CSenseGroupC) {
		    CSenseGroupC g = (CSenseGroupC) group;
		    expandGroup(graph, g);
		    graph.removeComponent(g);
		    hasGroups = true;

		    break;
		    // necessary due to iterator
		}
	    }

	}
    }

    private static void expandGroup(ComponentGraph graph, CSenseGroupC group) throws CompilerException {
	// add the components to the main graph
	for (IComponentC component : group.getComponents()) {
	    if (component instanceof CSenseGroupC) {
		expandGroup(graph, (CSenseGroupC) component);
	    } else if (component instanceof CSenseComponentC) {
		String name = component.getVariableName();
		int count = 0;
		while (graph.hasComponent(name)) {
		    name = name + count;
		    count = count + 1;
		}
		graph.addComponent(name, component);
	    } else
		throw new CompilerException("Hmm?");
	}

	for (InputPortC input : group.getInputPorts()) {
	    OutputPortC srcPort = input.getIncoming();
	    String inputName = input.getName();

	    OutputPortC internalOuput = group.internalOutputPort(inputName);
//	    InputPortC destPort = internalOuput.getOutgoing().get(0);
//	    if (internalOuput.getOutgoing().size() > 1) {
//		throw new IllegalStateException();
//	    }
//	    destPort.removeIncoming();
//	    srcPort.removeOutgoing();
//	    graph.link(srcPort, destPort);
//	    
	    srcPort.removeOutgoing();
	    for (InputPortC destPort : internalOuput.getOutgoing()) {
		destPort.removeIncoming();		
		graph.link(srcPort, destPort);
	    }
	    
	    System.out.println(srcPort);
	}

	for (OutputPortC output : group.getOutputPorts()) {
	    InputPortC dstPort = output.getOutgoing().get(0);
	    if (output.getOutgoing().size() > 1)
		throw new IllegalStateException();
	    String srcName = output.getName();

	    InputPortC internalInput = group.internalInputPort(srcName);
	    OutputPortC internalOutput = internalInput.getIncoming();

	    internalOutput.removeOutgoingLink(internalInput);
	    dstPort.removeIncoming();
	    graph.link(internalOutput, dstPort);
	}

	// remove the group component and its links to it
	graph.removeComponent(group);
	for (InputPortC input : group.getInputPorts()) {
	    OutputPortC incomingToGroup = input.getIncoming();
	    incomingToGroup.removeOutgoingLink(input);
	}
    }
}
