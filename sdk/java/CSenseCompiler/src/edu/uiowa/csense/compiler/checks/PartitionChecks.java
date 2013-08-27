package edu.uiowa.csense.compiler.checks;

import java.util.LinkedList;
import java.util.List;

import components.basic.SyncQueueC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.CSenseComponentC.ThreadingOption;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.Utils;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class PartitionChecks {

    public static void checkPartitions(CSenseGroupC main)
	    throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();
	allComponentsAssigned(graph);
	// checkSourceAssignments(graph);
	// checkDomainTransitions(graph);
    }

    private static void checkDomainTransitions(ComponentGraph graph)
	    throws CompilerException {
	for (IComponentC component : graph.components()) {
	    Domain componentDomain = component.getDomain();
	    for (IComponentC next : component.nextComponents()) {
		Domain nextDomain = next.getDomain();

		if (componentDomain != nextDomain) {
		    if (next instanceof SyncQueueC == false) {
			throw new CompilerException("Expected component ["
				+ component + "] to be followed by a SyncQueue");
		    }
		}
	    }
	}
    }

    private static void allComponentsAssigned(ComponentGraph graph)
	    throws CompilerException {
	/**
	 * Checks to make sure that all components pertain to a partition
	 */
	for (IComponentC component : graph.components()) {
	    if (component.getDomain() == null) {
		final String err = "No domain set for component " + component;
		Utils.printError(err);
		throw new CompilerException(err);
	    } else {
		System.out.println(component);
	    }
	}
    }

    private static void checkSourceAssignments(ComponentGraph graph)
	    throws CompilerException {
	/**
	 * Checks if each source has a different domain Checks if ANDROID
	 * sources are followed by sync queues
	 */
	DomainManager manager = DomainManager.domainManager();
	List<Domain> domains = new LinkedList<Domain>(manager.domains());
	int count[] = new int[domains.size()];

	for (IComponentC component : graph.components()) {
	    if (component.getThreadType() != ThreadingOption.NONE) {
		int index = domains.indexOf(component.getDomain());
		count[index] += 1;
	    }

	    if (component.getThreadType() == ThreadingOption.ANDROID) {
		for (IComponentC next : component.nextComponents()) {
		    if (next instanceof SyncQueueC == false) {
			throw new CompilerException("Expected component ["
				+ component + "] to be followed by a SyncQueue");
		    }
		}
	    }
	}

	for (int i = 0; i < count.length; i++) {
	    if (count[i] > 1) {
		Domain domain = domains.get(i);
		throw new CompilerException("Domain [" + domain
			+ "] has too many threaded components (count="
			+ count[i] + ")");
	    }
	}
    }
}
