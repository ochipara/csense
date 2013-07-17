package compiler.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import components.basic.CopyRefC;

import api.IComponentC;

public class ExpandFanOut {
    protected static Logger logger = Logger.getLogger(ExpandFanOut.class);
    static int refs = 0;

    public static void expandFanOut(CSenseGroupC main) throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();
	List<IComponentC> toadd = new ArrayList<IComponentC>();

	for (IComponentC component : graph.components()) {
	    expand_fanout(graph, toadd, component);
	}
	expand_fanout_internal(graph, toadd, main);

	for (IComponentC component : toadd) {
	    graph.addComponent("ref" + refs, component);
	    refs += 1;
	}

	// graph.display();
    }

    private static void expand_fanout(ComponentGraph graph, List<IComponentC> toadd, IComponentC component) throws CompilerException {
	for (OutputPortC outPort : component.getOutputPorts()) {
	    int fanout = outPort.outLinks().size();
	    if (fanout > 1) {
		logger.debug("Expanding outgoing link for " + component.getVariableName() + "::" + outPort.getName());

		// generate a reference component to replace the fan-out
		CopyRefC ref = new CopyRefC(outPort.getType(), fanout);
		toadd.add(ref);

		// the outputs go to each one of the destination components
		int index = 0;
		for (InputPortC input : outPort.outLinks()) {
		    input.removeIncoming(outPort);
		    graph.link(ref.getOutputPort(index), input);		    
		    index += 1;
		}

		// the input port of the ref is the outPort of the component
		//outPort.outLinks().clear();
		outPort.removeOutgoing();
		graph.link(outPort, ref.getInputPort(0));
	    }
	}
    }

    private static void expand_fanout_internal(ComponentGraph graph, List<IComponentC> toadd, CSenseGroupC component) throws CompilerException {
	for (OutputPortC outPort : component.internalOutputPorts()) {
	    int fanout = outPort.outLinks().size();
	    if (fanout > 1) {
		logger.debug("Expanding outgoing link for "
			+ component.getName() + "::" + outPort.getName());

		// generate a reference component to replace the fan-out
		CopyRefC ref = new CopyRefC(outPort.getType(), fanout);
		toadd.add(ref);

		// the outputs go to each one of the destination components
		int index = 0;
		for (InputPortC input : outPort.outLinks()) {
		    graph.link(ref.getOutputPort(index), input);
		    index += 1;
		}

		// the input port of the ref is the outPort of the component
		outPort.removeOutgoing();
		graph.link(outPort, ref.getInputPort(0));
	    }
	}
    }
}
