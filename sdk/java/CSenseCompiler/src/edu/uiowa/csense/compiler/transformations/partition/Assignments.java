package edu.uiowa.csense.compiler.transformations.partition;

import java.util.HashMap;

import edu.uiowa.csense.compiler.model.api.IComponentC;

public class Assignments {
    protected HashMap<IComponentC, Assignment> _assignments = new HashMap<IComponentC, Assignment>();
    protected int domains = -1;

    public boolean containsKey(IComponentC c) {
	return _assignments.containsKey(c);
    }

    public Assignment get(IComponentC c) {
	return _assignments.get(c);
    }

    public int newDomain() {
	domains = domains + 1;
	return domains;
    }

    public void put(IComponentC c, Assignment assignment) {
	_assignments.put(c, assignment);
    }

    public int numDomains() {
	return domains;
    }

}
