package edu.uiowa.csense.compiler.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;







import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class GraphUtils {
    /**
     * Returns the set of paths from the component
     * 
     * @param component
     * @param graph
     * @return
     * @throws CompilerException
     */
    public static List<Path> pathsFrom(IComponentC component)
	    throws CompilerException {
	List<Path> paths = new ArrayList<Path>();

	pathsFrom(component, new Path(), paths, null);
	return paths;
    }

    /**
     * Returns the paths from the component stopping when the specified output
     * ports are reached
     * 
     * @param component
     * @param outputPorts
     * @return
     * @throws CompilerException
     */
    public static List<Path> pathsFrom(CSenseComponentC component,
	    Collection<OutputPortC> outputPorts) throws CompilerException {
	List<Path> paths = new ArrayList<Path>();

	pathsFrom(component, new Path(), paths, outputPorts);
	return paths;
    }

    public static void pathsFrom(IComponentC component, Path path,
	    List<Path> paths, Collection<OutputPortC> stopOutputs)
	    throws CompilerException {
	path.push(component);

	if (component.getOutputPorts().size() == 0) {
	    paths.add((Path) path.clone());
	} else {
	    for (OutputPortC out : component.getOutputPorts()) {
		if (stopOutputs == null) {
		    IComponentC next = out.getSingleOutgoing().getComponent();
		    pathsFrom(next, path, paths, stopOutputs);
		} else {
		    if (stopOutputs.contains(out) == false) {
			IComponentC next = out.getSingleOutgoing().getComponent();
			pathsFrom(next, path, paths, stopOutputs);
		    } else {
			paths.add((Path) path.clone());
		    }
		}
	    }
	}

	path.pop();
    }

}
