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

    public class ComponentAssignment {
	int domain;
	boolean preliminary;

	public ComponentAssignment(int domain) {
	    this.domain = domain;
	    this.preliminary = true;
	}

	@Override
	public String toString() {
	    if (preliminary)
		return "d=" + domain + "[final]";
	    return "d=" + domain + "[preliminary";
	}

	public int getDomain() {
	    return domain;
	}

	public boolean isPreliminary() {
	    return preliminary;
	}
    }

    public void partitionApplication(CSenseGroupC main)
	    throws CompilerException {
	logger.setLevel(Level.DEBUG);

	ComponentGraph graph = main.getComponentGraph();
	Hashtable<IComponentC, ComponentAssignment> assignments = new Hashtable<IComponentC, ComponentAssignment>();
	int domain = -1;

	// start from sources
	for (IComponentC component : graph.components()) {
	    if (component.isSource()) {
		if (component.getThreadType() != ThreadingOption.NONE) {
		    domain = domain + 1;

		    ComponentAssignment a = new ComponentAssignment(domain);
		    assignments.put(component, a);
		    assignDomains(component, domain, assignments);
		}
	    }
	}

	// fill in the missing components
	for (IComponentC component : graph.components()) {
	    if ((assignments.containsKey(component) == false)
		    && (component.getThreadType() != ThreadingOption.NONE)) {
		domain = domain + 1;

		ComponentAssignment a = new ComponentAssignment(domain);
		assignments.put(component, a);
		assignDomains(component, domain, assignments);
	    }
	}

	if (domain < 0) domain = 0;

	DomainManager manager = DomainManager.domainManager();
	for (int d = 0; d <= domain; d++) {
	    Domain cdomain = manager.newDomain();

	    for (IComponentC component : assignments.keySet()) {
		ComponentAssignment assignment = assignments.get(component);
		if (assignment.getDomain() == d) {
		    cdomain.addComponent(component);
		}
	    }
	}

	PartitionApplication2.fixTransitions(graph, manager);
	PartitionApplication2.fixAndroidSources(graph, manager);
    }

    private void assignDomains(IComponentC component, int domain, Hashtable<IComponentC, ComponentAssignment> assignments) {
	logger.debug("considering component=" + component + " domain=" + domain);
	assert (assignments.containsKey(component));
	assert (assignments.get(component).getDomain() >= 0);

	// check if the assignment is safe
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
		    assignments.put(ancestor, new ComponentAssignment(domain));
		}
	    }
	}

	// we will try to assign the next components to this thread
	List<IComponentC> toallocate = new LinkedList<IComponentC>();
	for (IComponentC next : component.nextComponents()) {
	    boolean r = checkAssignment(next, domain, assignments);
	    if (r)
		toallocate.add(next);
	}

	for (IComponentC next : toallocate) {
	    assignDomains(next, domain, assignments);
	}
    }

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
    private boolean checkAncestors(IComponentC component, int domain,
	    Hashtable<IComponentC, ComponentAssignment> assignments) {
	ComponentAssignment assignment = assignments.get(component);
	if (assignment != null) {
	    // we have an assignment, check if it is the same domain
	    return (assignment.getDomain() == domain);
	} else {
	    for (IComponentC prev : component.prevComponents()) {
		boolean safe = checkAncestors(prev, domain, assignments);
		if (safe == false)
		    return false;
	    }

	    return true;
	}
    }

    private boolean checkAssignment(IComponentC component, int domain,
	    Hashtable<IComponentC, ComponentAssignment> assignments) {
	if (component.getThreadType() == ThreadingOption.CSENSE)
	    return false;

	if (assignments.contains(component) == false) {
	    ComponentAssignment assignment = new ComponentAssignment(domain);
	    assignments.put(component, assignment);
	    return true;
	} else {
	    ComponentAssignment assignment = assignments.get(component);

	    return assignment.getDomain() == domain;
	}

    }
}
