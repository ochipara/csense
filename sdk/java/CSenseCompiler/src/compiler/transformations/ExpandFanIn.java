package compiler.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import components.basic.MergeC;
import api.IComponentC;

public class ExpandFanIn {
    protected static Logger logger = Logger.getLogger(ExpandFanIn.class);

    public static void expandFanIn(CSenseGroupC main) throws CompilerException {
  	ComponentGraph graph = main.getComponentGraph();
  	List<IComponentC> toadd = new ArrayList<IComponentC>();

  	for (IComponentC component : graph.components()) {
  	  expandFanIn(graph, toadd, component);
  	}
  	

  	int refs = 0;
  	for (IComponentC component : toadd) {
  	    graph.addComponent("merge" + refs, component);
  	    refs += 1;
  	}

  	// graph.display();
      }

    private static void expandFanIn(ComponentGraph graph, List<IComponentC> toadd, IComponentC component) throws CompilerException {
	for (InputPortC input : component.getInputPorts()) {
	    int fanIn = input.getAllIncoming().size(); 
	    if (fanIn > 1) {
		logger.debug("Expanding fanin " + input);
		
		MergeC merge = new MergeC(input.getType(), fanIn);		
		List<OutputPortC> outs = input.getAllIncoming();
		
		int index = 0;
		for (OutputPortC out : outs) {
		    IComponentC prev = out.getComponent();
		    out.removeOutgoing();
		    merge.getInputPort(index).addIncoming(out);		    		    
		    out.addOutgoing(merge.getInputPort(index));
		    index = index + 1;
 		}
		
		input.removeIncoming();
		input.addIncoming(merge.getOutputPort(0));
		merge.getOutputPort(0).addOutgoing(input);
		toadd.add(merge);		
	    }
	}
    }
    

}
