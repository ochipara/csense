package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.Port;

public abstract class PortImpl implements Port {
    protected final IComponent _owner;
    protected final String _name;

    public PortImpl(IComponent owner, String name) {
	_owner = owner;
	_name = name;
    }

    @Override
    public IComponent getOwner() {
	return _owner;
    }

    @Override
    public String getName() {
	return _name;
    }

    @Override
    abstract public boolean isInput();

    @Override
    abstract public boolean isOutput();
}
