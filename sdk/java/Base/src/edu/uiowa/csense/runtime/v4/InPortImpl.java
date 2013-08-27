package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.Constants;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;

/**
 * The class implements the input port of a component.
 * Ports work on superframes, not on frames.
 * 
 * 
 * @author ochipara
 *
 * @param <T>
 */
public class InPortImpl<T extends Frame> extends PortImpl implements InputPort<T> {
    private final String TAG;
    protected OutputPort<T> _out = null;   
    protected InputPort<T> self;
    protected Frame _m = null;        
   
    public InPortImpl(IComponent owner, String name) {
	super(owner, name);
	String cName = owner.getName().substring(owner.getName().lastIndexOf('.') + 1, owner.getName().length());
	TAG = cName + "::" + name;	
    }
    
    @Override
    public boolean isInput() {
	return true;
    }

    @Override
    public boolean isOutput() {
	return false;
    }

    @Override
    public void link(OutputPort<? extends Frame> out) throws CSenseException {
	if (out == null) {
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR, "out port cannot be null");
	}
	_out = (OutputPort<T>) out;
    }
    

    @Override
    public int onPush(Frame m) throws CSenseException {
	if (_m != null) {
	    return Constants.PUSH_FAIL;
	}
	
	
	_m = m;	
	if (Debug.isTracing()) {
	    Debug.logMessagePush(_owner, m);
	}	
	_owner.onPush(self, m);	
	return Constants.PUSH_SUCCESS;
    }
    
    @Override
    public int poll() throws CSenseException {
	if (_m != null)  throw new CSenseException(CSenseError.ERROR, "This should not happen!");
	
	_m = _out.onPoll();
	if (_m != null) {
	    return Constants.POLL_SUCCESS;
	} else {
	    return Constants.POLL_FAILED;
	}
    }
	
    @Override
    public void clear() throws CSenseException {
	if (_m == null)  {
	    throw new CSenseException(CSenseError.ERROR, "This should not happen!");
	}
	_m = null;
    }

    @Override
    public boolean hasFrame() {
	return _m != null;
    }

    @Override
    public T getFrame() {	
	return (T) _m;
    }

 
    @Override
    public boolean getSupportsPoll() {
	return _out.getSupportsPoll();
    }


    @Override
    public String toString() {
	if  (_m == null) return _name + " [NO MESSAGE]";
	else return _name + " [HAS MESSAGE]";
    }
}
