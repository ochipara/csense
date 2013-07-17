package compiler.transformations;

import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.CSenseComponentC.ThreadingOption;
import compiler.model.CSenseGroupC;
import compiler.model.ComponentGraph;
import compiler.model.Domain;
import compiler.model.DomainManager;
import compiler.model.Utils;
import components.basic.SyncQueue;

import api.IComponentC;

/**
 * A basic algorithm for producing partitions. It is safe and REALY STUPID. We
 * will need something better here.
 * 
 * @author ochipara
 * 
 */
public class PartitionApplication {
    protected static Logger logger = Logger
	    .getLogger(PartitionApplication.class);
    protected final boolean debug = true;

    public static void partitionApplication(CSenseGroupC main)
	    throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();
	DomainManager manager = graph.getDomainManager();

	// System.out.println("PARTION====>");
	// graph.display();
	// System.out.println("PARTION====>");

	// initialize the domains with the seeds
	boolean anyThread = false;
	for (IComponentC component : graph.components()) {
	    if (component.getThreadType() == ThreadingOption.CSENSE) {
		Domain domain = manager.newDomain();
		manager.update(component, domain);
		anyThread = true;
	    } else if (component.getThreadType() == ThreadingOption.ANDROID) {
		Domain domain = manager.newDomain();
		manager.update(component, domain);

		IComponentC next = component.nextComponents().iterator().next();
		if (next.getComponent() != SyncQueue.class) {
		    throw new CompilerException("Expected to find a queue");
		}

		// IComponentC next2 = next.nextComponents().iterator().next();
		domain = manager.newDomain();
		// manager.update(next2, domain);
		manager.update(next, domain);

		anyThread = true;
	    }
	}

	if (anyThread == false) {
	    Domain domain = manager.newDomain();
	    for (IComponentC component : graph.components()) {
		manager.update(component, domain);
	    }
	} else {
	    for (IComponentC component : graph.components()) {
		if (component.getDomain() != null) {
		    Domain domain = component.getDomain();
		    expandDomain(domain, component, manager);
		}
	    }
	}

	System.out.println("Domain partition ======> ");
	for (Domain domain : manager.domains()) {
	    System.out.println(domain);
	    for (IComponentC component : domain.components()) {
		System.out.println("\t" + component);
	    }
	}
	System.out.println("Domain partition <====== ");

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

    private static void expandDomain(Domain domain, IComponentC component,
	    DomainManager manager) throws CompilerException {
	// TODO: check for the presence of SyncQueues
	// if (component.getThreadType() == ThreadingOption.CSENSE) {
	// // make sure we have a syncQueue
	// for (IComponentC prev : component.prevComponents()) {
	// // if (SyncQueue.class != prev.getComponent()) {
	// // throw new
	// CompilerException("Domains should be separated by synchronous queues");
	// // } else {
	// manager.update(prev, component.getDomain());
	// // }
	// }
	// }

	// update the next components
	for (IComponentC next : component.nextComponents()) {
	    if ((next.getThreadType() == ThreadingOption.NONE)
		    && (next.getDomain() == null)) {
		manager.update(next, domain);
		expandDomain(domain, next, manager);
	    }
	}

	// update the previous components
	// for (IComponentC prev : component.prevComponents()) {
	// if ((prev.getThreadType() == ThreadingOption.NONE) &&
	// (prev.getDomain() == null)) {
	// manager.update(prev, domain);
	// expandDomain(domain, prev, manager);
	// }
	// }
    }
}
