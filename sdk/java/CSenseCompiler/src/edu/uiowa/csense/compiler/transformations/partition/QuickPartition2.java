package edu.uiowa.csense.compiler.transformations.partition;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.CSenseComponentC.ThreadingOption;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.transformations.PartitionApplication2;

/**
 * For a description of the algorithm see:
 * 
 * http://code.google.com/p/egosense/wiki/QuickPartition?ts=1359510731&updated=
 * QuickPartition
 * 
 */
public class QuickPartition2 {
    protected static Logger logger = Logger.getLogger("partition");

    private void makeAssignment(IComponentC component, int domain, Hashtable<IComponentC, Integer> assignments) {
	if (assignments.containsKey(component)) {
	    if (assignments.get(component) != domain) {
		throw new IllegalStateException("This should not happen");
	    }
	} else {
	    assignments.put(component, domain);
	    logger.debug("assigning component=" + component.getVariableName() + " domain=" + domain);
	}

    }

    public void partitionApplication(CSenseGroupC main) throws CompilerException {
	logger.setLevel(Level.DEBUG);

	ComponentGraph graph = main.getComponentGraph();
	Hashtable<IComponentC, Integer> assignments = new Hashtable<IComponentC, Integer>();
	int domain = -1;

	for (IComponentC component : graph.components()) {
	    if (component.getThreadType() != ThreadingOption.NONE) {
		domain = domain + 1;

		makeAssignment(component, domain, assignments);
	    }
	}

	// start from sources
	for (IComponentC component : graph.components()) {
	    if (component.isSource()) {
		if (component.getThreadType() != ThreadingOption.NONE) {		    
		    domain = assignments.get(component);
		    assignDomains(component, domain, assignments);
		}
	    }
	}

	// start from sources
	for (IComponentC component : graph.components()) {
	    if (component.isSource() == false) {
		if (component.getThreadType() != ThreadingOption.NONE) {		    
		    int cdomain = assignments.get(component);
		    assignDomains(component, cdomain, assignments);
		}
	    }
	}

	// fill in the missing components
	for (IComponentC component : graph.components()) {
	    if ((assignments.containsKey(component) == false)
		    && (component.getThreadType() != ThreadingOption.NONE)) {
		domain = domain + 1;

		makeAssignment(component, domain, assignments);
		assignDomains(component, domain, assignments);
	    }
	}

	if (domain < 0) domain = 0;

	DomainManager manager = DomainManager.domainManager();
	for (int d = 0; d <= domain; d++) {
	    Domain cdomain = manager.newDomain();

	    for (IComponentC component : assignments.keySet()) {
		Integer assignment = assignments.get(component);
		if (assignment == d) {
		    cdomain.addComponent(component);
		}
	    }
	}

	PartitionApplication2.fixTransitions(graph, manager);
	PartitionApplication2.fixAndroidSources(graph, manager);
    }

    private void assignDomains(IComponentC component, int domain, Hashtable<IComponentC, Integer> assignments) {
	//logger.debug("considering component=" + component + " domain=" + domain);
	assert (assignments.containsKey(component));
	assert (assignments.get(component) >= 0);

	// check if the assignment is safe
	List<IComponentC> toallocate = new LinkedList<IComponentC>();

	for (InputPortC input : component.getInputPorts()) {
	    List<IComponentC> ancestors = getAncestors(input, component);

	    // check if the assignment is safe
	    boolean safe = true;
	    for (IComponentC ancestor : ancestors) {
		safe = checkAncestors(ancestor, domain, assignments);
		if (safe == false)
		    break;
	    }

	    // if safe, assign the ancestors to this domain
	    if (safe) {
		for (IComponentC ancestor : ancestors) {
		    makeAssignment(ancestor, domain, assignments);

		    // necessary if you walking backwards you encounter an element that has multiple inputs
		    for (IComponentC next : ancestor.nextComponents()) {

			if ((assignments.containsKey(next) == false) && (component != next)) {
			    boolean r = checkAssignment(next, domain, assignments);
			    if (r) {
				toallocate.add(next);
			    }			
			}
		    }
		}
	    }
	}

	// we will try to assign the next components to this thread
	for (IComponentC next : component.nextComponents()) {
	    boolean r = checkAssignment(next, domain, assignments);
	    if (r) {
		toallocate.add(next);
	    }
	}

	for (IComponentC next : toallocate) {
	    assignDomains(next, domain, assignments);
	}
    }

//    private void assignDomains(IComponentC component, int domain, Hashtable<IComponentC, Integer> assignments) {
//	//logger.debug("considering component=" + component + " domain=" + domain);
//	assert (assignments.containsKey(component));
//	assert (assignments.get(component) >= 0);
//
//	// check if the assignment is safe
//	List<IComponentC> toallocate = new LinkedList<IComponentC>();
//
//	for (InputPortC input : component.getInputPorts()) {
//	    List<IComponentC> ancestors = getAncestors(input, component);
//
//	    // check if the assignment is safe
//	    boolean safe = true;
//	    for (IComponentC ancestor : ancestors) {
//		safe = checkAncestors(ancestor, domain, assignments);
//		if (safe == false)
//		    break;
//	    }
//
//	    // if safe, assign the ancestors to this domain
//	    if (safe) {
//		for (IComponentC ancestor : ancestors) {
//		    makeAssignment(ancestor, domain, assignments);
//
//		    // necessary if you walking backwards you encounter an element that has multiple inputs
//		    for (IComponentC next : ancestor.nextComponents()) {
//
//			if ((assignments.containsKey(next) == false) && (component != next)) {
//			    boolean r = checkAssignment(next, domain, assignments);
//			    if (r) {
//				toallocate.add(next);
//			    }			
//			}
//		    }
//		}
//	    }
//	}
//
//	// we will try to assign the next components to this thread
//	for (IComponentC next : component.nextComponents()) {
//	    boolean r = checkAssignment(next, domain, assignments);
//	    if (r) {
//		toallocate.add(next);
//	    }
//	}
//
//	for (IComponentC next : toallocate) {
//	    assignDomains(next, domain, assignments);
//	}
//    }

    private List<IComponentC> getAncestors(InputPortC input, IComponentC component) {
	LinkedList<IComponentC> ancestors = new LinkedList<IComponentC>();
	IComponentC parent = input.getIncoming().getComponent();
	ancestors.add(parent);
	getAncestors(parent, ancestors);
	return ancestors;
    }

    private void getAncestors(IComponentC component, List<IComponentC> ancestors) {	
	for (IComponentC prev : component.prevComponents()) {
	    ancestors.add(prev);
	    getAncestors(prev, ancestors);
	}
    }

    /**
     * Checks if the ancestors of the component are okay to be assigned to the
     * specified domain
     * 
     * @param component
     * @param domain
     * @param assignments
     * @return
     */
    private boolean checkAncestors(IComponentC component, int domain, Hashtable<IComponentC, Integer> assignments) {
	Integer assignment = assignments.get(component);
	if (assignment != null) {
	    // we have an assignment, check if it is the same domain
	    return (assignment == domain);
	} else {
	    for (IComponentC prev : component.prevComponents()) {
		boolean safe = checkAncestors(prev, domain, assignments);
		if (safe == false) {
		    return false;
		}
	    }

	    return true;
	}
    }

    private boolean checkAssignment(IComponentC component, int domain, Hashtable<IComponentC, Integer> assignments) {
	if (component.getThreadType() == ThreadingOption.CSENSE)
	    return false;

	if (assignments.containsKey(component) == false) {
	    makeAssignment(component, domain, assignments);
	    return true;
	} else {
	    Integer assignment = assignments.get(component);

	    return assignment == domain;
	}

    }
}
