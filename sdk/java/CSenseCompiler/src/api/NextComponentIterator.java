package api;

import java.util.Iterator;

import compiler.model.InputPortC;
import compiler.model.OutputPortC;


public class NextComponentIterator implements Iterable<IComponentC>,
	Iterator<IComponentC> {
    protected final IComponentC _component;
    protected Iterator<OutputPortC> _port;
    protected Iterator<InputPortC> _inputPortIterator = null;

    public NextComponentIterator(IComponentC component) {
	_component = component;
	_port = component.getOutputPorts().iterator();
    }

    @Override
    public boolean hasNext() {
	return _port.hasNext();
    }

    @Override
    public IComponentC next() {
	if (_inputPortIterator == null) {
	    OutputPortC port = _port.next();
	    _inputPortIterator = port.outLinks().iterator();
	    return _inputPortIterator.next().getComponent();
	}

	if (_inputPortIterator.hasNext()) {
	    return _inputPortIterator.next().getComponent();
	} else {
	    OutputPortC port = _port.next();
	    _inputPortIterator = port.outLinks().iterator();
	    return _inputPortIterator.next().getComponent();
	}
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
