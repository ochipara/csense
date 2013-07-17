package compiler.model;

import java.util.LinkedList;
import java.util.List;

import api.IComponentC;

public class Domain {
    protected final List<IComponentC> _components = new LinkedList<IComponentC>();
    protected final int _id;

    public Domain(int id) {
	_id = id;
    }

    public void addComponent(IComponentC c) {
	c.setDomain(this);
	_components.add(c);
    }

    public List<IComponentC> components() {
	return _components;
    }

    @Override
    public String toString() {
	return "domain" + _id;
    }

    public void removeComponent(IComponentC prev) {
	_components.remove(prev);
    }

    public String schedulerName() {
	return "domain" + _id + "Scheduler";
    }
}
