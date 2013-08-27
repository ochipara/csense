package edu.uiowa.csense.compiler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uiowa.csense.compiler.model.api.IComponentC;

public class DomainManager {
    static int _idGenerator = 0;
    Map<IComponentC, Domain> _domainsMap = new HashMap<IComponentC, Domain>();
    List<Domain> _domains = new LinkedList<Domain>();
    private static DomainManager _manager = new DomainManager();

    private DomainManager() {
    }

    public Domain newDomain() {
	Domain d = new Domain(_idGenerator);
	_idGenerator = _idGenerator + 1;
	_domains.add(d);

	return d;
    }

    public Collection<Domain> domains() {
	return _domains;
    }

    public void update(IComponentC comp, Domain domain) {
	if (_domainsMap.containsKey(comp)) {
	    Domain prevDomain = _domainsMap.get(comp);
	    prevDomain.removeComponent(comp);
	}

	_domainsMap.put(comp, domain);
	domain.addComponent(comp);
	comp.setDomain(domain);
    }

    public static DomainManager domainManager() {
	return _manager;
    }

}
