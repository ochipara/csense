package base.v2;

import base.Debug;
import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IComponent;
import api.IInPort;
import api.IOutPort;
import api.IResult;
import api.Message;

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
public class OutPortImpl<T extends Message> extends PortImpl implements IOutPort<T> {
    private final String TAG;
    IInPort<T> _in = null;
    boolean _supportPull = false;
    private int _multiplier;
    private int _index = 0;

    public OutPortImpl(CSenseComponent owner, String name) {
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
    public void link(IInPort<? extends Message> in) throws CSenseException {
	if (in == null)
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR,
		    "in cannot be null");
	_in = (IInPort<T>) in;
	_in.link(this);
    }

    @Override
    public IResult push(T m) throws CSenseException {
	if (_owner.isEof()) {
	    m.eof();
	}

	if (_index > 0) {
	    _index = _index - 1;
	    return IResult.PUSH_SUCCESS;
	} else {
	    IResult result;
	    T pushMessage;
	    if (_multiplier == 1) {
		pushMessage = m;		
	    } else {
		pushMessage = (T) m.getParent();
	    }
	    int mid = pushMessage.getId();
	    result = _in.onPush(pushMessage); 
	    Debug.logMessagePushReturn(_owner, mid);
	    _owner.accumulateResult(result);
	    _index = _multiplier - 1;
	    //Log.d(TAG, "push " + pushMessage.hashCode());
	    return result;
	}
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

    @Override
    public void setMultiplier(int multiplier) {
	this._multiplier = multiplier;
	this._index = multiplier - 1;
    }
}
