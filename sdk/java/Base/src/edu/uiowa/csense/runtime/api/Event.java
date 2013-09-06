package edu.uiowa.csense.runtime.api;

public class Event {
    private IComponent _owner;

    public IComponent getOwner() {
	return _owner;
    }

    public void setOwner(IComponent component) {
	_owner = component;
    }
}