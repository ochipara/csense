package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;

/**
 * This class implements the output port.
 * The main functionality is to handle superframes.
 * This class ensures that a superframe is pushed to the next component only after the component
 * processed all frames within the superframe.
 * 
 * @author ochipara
 *
 * @param <T>
 */
public class OutPortImpl<T extends Frame> extends PortImpl implements OutputPort<T> {
    private final String TAG;
    InputPort<T> _in = null;
    boolean _supportPull = false;
   
    public OutPortImpl(IComponent owner, String name) {
	super(owner, name);
	String cName = owner.getName().substring(owner.getName().lastIndexOf('.') + 1, owner.getName().length());
	TAG = cName + "::" + name;
    }

    @Override
    public IComponent nextComponent() {
	return _in.getOwner();
    }

    @Override
    public boolean isInput() {
	return false;
    }

    @Override
    public boolean isOutput() {
	return true;
    }

    @Override
    public void link(InputPort<? extends Frame> in) throws CSenseException {
	if (in == null) {
	    throw new IllegalArgumentException("input port cannot be null when connecting to " + TAG);
	}
	
	_in = (InputPort<T>) in;
	_in.link(this);
    }

    @Override
    public int push(T m) throws CSenseException {	
	int r = _in.onPush(m);
	Debug.logMessagePushReturn(_owner, m);
	return r;
    }

    @Override
    public T onPoll() throws CSenseException {
	return (T) _owner.onPoll(this);
    }

    @Override
    public boolean getSupportsPoll() {
	return _supportPull;
    }

    @Override
    public void setSupportPull(boolean b) {
	_supportPull = b;
    }

    @Override
    public boolean isConnected() {
	return (_in != null);
    }
}
