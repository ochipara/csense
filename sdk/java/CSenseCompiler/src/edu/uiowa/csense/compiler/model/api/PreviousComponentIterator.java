package edu.uiowa.csense.compiler.model.api;

import java.util.Iterator;

import edu.uiowa.csense.compiler.model.InputPortC;


public class PreviousComponentIterator implements Iterable<IComponentC>,
	Iterator<IComponentC> {
    protected final IComponentC _component;
    protected Iterator<InputPortC> _port;

    public PreviousComponentIterator(IComponentC component) {
	_component = component;
	_port = component.getInputPorts().iterator();
    }

    @Override
    public boolean hasNext() {
	return _port.hasNext();
    }

    @Override
    public IComponentC next() {
	InputPortC input = _port.next();
	return input.getIncoming().getComponent();
    }

    @Override
    public void remove() {
	throw new IllegalStateException();
    }

    @Override
    public Iterator<IComponentC> iterator() {
	return this;
    }
}
