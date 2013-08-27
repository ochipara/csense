package edu.uiowa.csense.compiler.transformations.partition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import components.basic.CopyRefC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.CSenseComponentC.ThreadingOption;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.Path;
import edu.uiowa.csense.compiler.model.PathComparator;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.transformations.PartitionApplication2;

public class QuickPartition {
    protected static Logger logger = Logger.getLogger("partition");
    protected final boolean debug = false;

    private static void constructPaths(OutputPortC out, Path currentPath,
	    List<Path> paths) throws CompilerException {
	IComponentC component = out.getComponent();
	currentPath.push(component);

	InputPortC in = out.getSingleOutgoing();
	IComponentC next = in.getComponent();
	OutputPortC internalOut = in.getInternalOutput();
	boolean shouldPop = true;

	if (internalOut != null) {
	    constructPaths(internalOut, currentPath, paths);
	} else {
	    if (next.getOutputPorts().size() == 0) {
		currentPath.push(next);
		paths.add((Path) currentPath.clone());
	    } else {
		logger.warn("Something went wrong. Probably you did not transition to IOPorts in "
			+ next);
		for (OutputPortC inOut : next.getOutputPorts()) {
		    constructPaths(inOut, currentPath, paths);
		    currentPath.pop();
		    shouldPop = false;
		}
	    }
	}
	if (shouldPop)
	    currentPath.pop();
    }

    public static void partitionApplication(CSenseGroupC main)
	    throws CompilerException {
	logger.setLevel(Level.DEBUG);

	ComponentGraph graph = main.getComponentGraph();
	List<IComponentC> components = new ArrayList<IComponentC>(
		graph.components());

	List<Path> allPaths = new LinkedList<Path>();
	for (IComponentC c : graph.components()) {
	    if ((c.getInputPorts().size() < c.getOutputPorts().size())
		    && (c instanceof CopyRefC == false)) {
		int cIndex = components.indexOf(c);
		// this is a source, find a path from it

		List<Path> paths = new LinkedList<Path>();

		for (OutputPortC out : c.getOutputPorts()) {
		    if (out.getInternalInput() == null) {
			if (out.outLinks().size() > 0) {
			    Path currentPath = new Path();

			    constructPaths(out, currentPath, paths);
			    allPaths.addAll(paths);
			} else {
			    if (out.isOptional() == false) {
				throw new CompilerException(
					"Expected this to be an optional port");
			    }
			}

		    }
		}
	    }
	}

	Assignments assignments = assignDomains(components, allPaths);
	DomainManager manager = DomainManager.domainManager();

	int maxDomains = assignments.numDomains();
	if (maxDomains == -1)
	    maxDomains = 1;
	for (int d = 0; d <= maxDomains; d++) {
	    Domain cdomain = manager.newDomain();

	    for (IComponentC component : components) {
		Assignment a = assignments.get(component);
		if (a.getDomain() == d) {
		    cdomain.addComponent(component);
		}
	    }
	}

	PartitionApplication2.fixTransitions(graph, manager);
	PartitionApplication2.fixAndroidSources(graph, manager);
    }

    private static Assignments assignDomains(List<IComponentC> components,
	    List<Path> paths) throws CompilerException {
	for (Path path : paths) {
	    logger.debug("long " + path + " " + path.numThreadedComponents());
	}

	List<Path> newPaths = new LinkedList<Path>();
	for (Path path : paths) {
	    if (path.numThreadedComponents() > 1) {
		Path newPath = new Path();
		int numThreads = 0;
		for (int i = 0; i < path.size(); i++) {
		    IComponentC c = path.get(i);

		    if (c.getThreadType() != ThreadingOption.NONE)
			numThreads += 1;

		    if (numThreads <= 1) {
			newPath.push(c);
		    } else {
			newPaths.add(newPath);
			newPath = new Path();
			newPath.push(c);
			numThreads = 0;
		    }
		}
		if (newPath.size() > 0)
		    newPaths.add(newPath);
	    } else {
		newPaths.add(path);
	    }
	}

	paths = newPaths;
	for (Path path : paths) {
	    logger.debug("short " + path + " " + path.numThreadedComponents());
	}

	PriorityQueue<Path> queue = new PriorityQueue<Path>(components.size(),
		new PathComparator());
	queue.addAll(paths);
	Assignments assignments = new Assignments();

	for (Path path : queue) {
	    if (path.numThreadedComponents() > 1) {
		path.numThreadedComponents();
		throw new CompilerException("Fix me!");
	    } else if (path.numThreadedComponents() == 1) {
		int color = getPathColor(path, assignments);
		if (color == -1) {
		    color = assignments.newDomain();
		    logger.debug("new domain" + color);
		}
		colorPath(path, color, assignments);
	    }
	}

	for (Path path : queue) {
	    if (path.numThreadedComponents() == 0) {
		int color = getPathColor(path, assignments);
		if (color == -1)
		    color = 0;
		// if (color != -1) {
		// color = getPathColor(path, assignments);
		// }
		// assert (color != -1);
		colorPath(path, color, assignments);
	    }
	}

	return assignments;
    }

    private static void colorPath(Path path, int color, Assignments assignments) {
	boolean first = true;
	for (IComponentC component : path) {
	    boolean skip = first && (component.getInputPorts().size() > 0);
	    first = false;

	    if (skip == false) {
		if (assignments.containsKey(component)) {
		    int dcolor = assignments.get(component).getDomain();
		    assert (color == dcolor);
		} else {
		    assignments.put(component, new Assignment(color));
		    logger.debug("color " + component.getVariableName() + " "
			    + color);
		}
	    } else {
		logger.debug("skip " + component.getVariableName());
	    }
	}
    }

    private static int getPathColor(Path path, Assignments assignments)
	    throws CompilerException {
	int color = -1;
	boolean first = true;
	for (IComponentC component : path) {
	    if (assignments.containsKey(component)) {
		int dcolor = assignments.get(component).getDomain();
		boolean skip = first && (component.getInputPorts().size() > 0);
		first = false;

		if (skip == false) {
		    if (color == -1) {
			color = dcolor;
		    } else if (color != dcolor) {
			System.out.println("hmmm");
			throw new CompilerException("This should not happen");
		    }
		} else {
		    logger.debug("skip " + component.getVariableName());
		}
	    }
	}
	return color;
    }

}
