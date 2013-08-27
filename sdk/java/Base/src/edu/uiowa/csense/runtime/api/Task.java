package api;

public class Task {
    private IComponent _owner;

    public IComponent getOwner() {
	return _owner;
    }

    public void setOwner(IComponent component) {
	_owner = component;
    }
}