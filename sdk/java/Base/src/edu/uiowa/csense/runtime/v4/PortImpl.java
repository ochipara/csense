package base.v2;

import api.CSenseComponent;
import api.IComponent;
import api.IPort;

public abstract class PortImpl implements IPort {
    protected final CSenseComponent _owner;
    protected final String _name;

    public PortImpl(CSenseComponent owner, String name) {
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
