package edu.uiowa.csense.compiler.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import components.basic.CopyRefC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class PortPath implements Iterable<PortPair> {
    protected Stack<PortPair> _path;

    public PortPath() {
	_path = new Stack<PortPair>();
    }

    @SuppressWarnings("unchecked")
    public PortPath(PortPath path) {
	_path = (Stack<PortPair>) path._path.clone();
    }

    public void push(PortPair pair) {
	_path.push(pair);
    }

    public PortPair pop() {
	return _path.pop();
    }

    public static List<PortPath> pathFrom(InputPortC input) throws CompilerException {
	List<PortPath> paths = new ArrayList<PortPath>();
	PortPath path = new PortPath();

	pathsFrom(input, path, paths, null);
	return paths;
    }

    public static List<PortPath> pathFrom(OutputPortC output) throws CompilerException {
	List<PortPath> paths = new ArrayList<PortPath>();
	PortPath path = new PortPath();

	pathsFrom(output, path, paths, null);
	return paths;
    }

    
    public static List<PortPath> pathFrom(InputPortC input, Collection<OutputPortC> stopPorts) throws CompilerException {
	List<PortPath> paths = new ArrayList<PortPath>();
	PortPath path = new PortPath();

	pathsFrom(input, path, paths, stopPorts);
	return paths;
    }

    public static void pathsFrom(OutputPortC output, PortPath path, List<PortPath> paths, Collection<OutputPortC> stopPorts) throws CompilerException {
	InputPortC input = null;
	path.push(new PortPair(input, output));

	if ((stopPorts != null) && (stopPorts.contains(output))) {
	    paths.add(new PortPath(path));
	} else {
	    InputPortC nextInput = output.getSingleOutgoing();
	    if (nextInput != null) {
		pathsFrom(nextInput, path, paths, stopPorts);
	    } else {
		paths.add(new PortPath(path));
	    }
	}

	path.pop();
    }
    
    public static void pathsFrom(InputPortC input, PortPath path, List<PortPath> paths, Collection<OutputPortC> stopPorts) throws CompilerException {
	List<OutputPortC> outputs = new ArrayList<OutputPortC>();

	OutputPortC internalOutput = input.getInternalOutput();
	if (input.getComponent().getOutputPorts().size() > 0) {
	    if (internalOutput == null) {
		/**
		 * Check if any of the paths finishes with this component. If they do, then we have a path that ends here. Add this as a path.
		 * 
		 */
		boolean terminal = false;
		for (OutputPortC output : input.getComponent().getOutputPorts()) {
		   if (output.isSource() == false) outputs.add(output);
		   else terminal = true;
		}
		
		if (terminal) {
		    path.push(new PortPair(input, null));
		    paths.add(new PortPath(path));
		    path.pop();
		}
		
//		if ((input.getComponent() instanceof CopyRefC == false) && (input.getComponent() instanceof MergeC == false)) {
//		    throw new CompilerException("Internal link not set "  + input);
//		} else if (input.getComponent() instanceof MergeC == false) {
//		    // merge 
//		    outputs.addAll(input.getComponent().getOutputPorts());
//		} else {
//		    outputs.addAll(input.getComponent().getOutputPorts());
//		} 
//		path.push(new PortPair(input, null));
//		paths.add(new PortPath(path));
//		path.pop();
	    } else {
		outputs.add(internalOutput);
	    }
	} else {
	    path.push(new PortPair(input, null));
	    paths.add(new PortPath(path));
	    path.pop();
	}

	for (OutputPortC output : outputs) {
	    path.push(new PortPair(input, output));

	    if ((stopPorts != null) && (stopPorts.contains(output))) {
		paths.add(new PortPath(path));
	    } else {
		InputPortC nextInput = output.getSingleOutgoing();
		if (nextInput != null) {
		    pathsFrom(nextInput, path, paths, stopPorts);
		} else {
		    paths.add(new PortPath(path));
		}
	    }

	    path.pop();
	}
    }

    public static List<PortPath> pathFromWithin(InputPortC input, Collection<IComponentC> components) throws CompilerException {
	List<PortPath> paths = new ArrayList<PortPath>();
	PortPath path = new PortPath();

	pathFromWithin(input, path, paths, components);
	return paths;
    }

    public static void pathFromWithin(InputPortC input, PortPath path, List<PortPath> paths, Collection<IComponentC> components)
	    throws CompilerException {
	List<OutputPortC> outputs = new ArrayList<OutputPortC>();

	OutputPortC internalOutput = input.getInternalOutput();
	if (input.getComponent().getOutputPorts().size() > 0) {
	    if (internalOutput == null) {
		if (input.getComponent() instanceof CopyRefC == false) {
		    throw new CompilerException("Internal link not set " + input);
		}
		outputs.addAll(input.getComponent().getOutputPorts());
	    } else {
		outputs.add(internalOutput);
	    }
	} else {	    
	    paths.add(new PortPath(path));
	}

	for (OutputPortC output : outputs) {
	    path.push(new PortPair(input, output));
	    IComponentC component = output.getComponent();
	    IComponentC next = output.getSingleOutgoing().getComponent();

	    if (components.contains(next) == false) {
		paths.add(new PortPath(path));
	    } else {
		InputPortC nextInput = output.getSingleOutgoing();
		if (nextInput != null) {
		    pathFromWithin(nextInput, path, paths, components);
		} else {		    
		    paths.add(new PortPath(path));
		}
	    }

	    path.pop();
	}
    }

    @Override
    public Iterator<PortPair> iterator() {
	return _path.iterator();
    }

    public PortPair lastElement() {
	return _path.lastElement();
    }

    public int size() {
	return _path.size();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (PortPair portPair : _path) {
	    sb.append(portPair + " ");
	}

	return sb.toString();
    }

    public PortPair elementAt(int i) {
	return _path.elementAt(i);
    }

    public PortPair firstElement() {
	return _path.firstElement();
    }

}
