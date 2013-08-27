package base.v2;

import java.util.List;

import messages.RawMessage;

import base.Debug;
import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.IResult;
import api.Message;

/**
 * The class implements the input port of a component.
 * One of the key responsibilities of this class is to handle superframes. 
 * The class will automatically split a superframe into frames and present them to the underlying component implementation.
 * This works in tandem with the output port, which will ignore aggregate the frames computed by the component into a superframe.
 * The next component will not be invoked until all the elements in the superframe have been computed!
 * 
 * Implementation details:
 * One of the reasons why this class looks so nasty is because it must handle both PUSH and POLL. Moreover, the getMessage method
 * must return the proper frame within the superframe. 
 * 
 * The PUSHING is handled in the onPush method.
 * The POLLING is handled in two places: the poll procedure the clear procedure. 
 * The poll procedure will request a superframe and will all further poll invokations will result in returning frames of the obtained superframe.
 * The clear procedure will advance the pollIndex to point to the next frame in the superframe.
 * 
 * 
 * @author ochipara
 *
 * @param <T>
 */
public class InPortImpl<T extends Message> extends PortImpl implements IInPort<T> {
    private final String TAG;
    protected OutPortImpl<T> _out = null;
    protected T _m = null;
    protected int _multiplier = 1;
    protected int _pollIndex = -1;
    protected int _pushIndex = -1;
    protected List<Message> views = null;

    public InPortImpl(CSenseComponent owner, String name) {
	super(owner, name);
	String cName = owner.getName().substring(owner.getName().lastIndexOf('.') + 1, owner.getName().length());
	TAG =cName + "::" + name;
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
    public void link(IOutPort<T> out) throws CSenseException {
	if (out == null) {
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "out port cannot be null");
	}
	_out = (OutPortImpl<T>) out;
    }

    @Override
    public IResult onPush(T m) throws CSenseException {
	if (_multiplier == 1) {
	    if (setMessage(m, true) != IResult.PUSH_SUCCESS) {
		return IResult.PUSH_DROP;
	    }
	    return _owner.processInput(this, m);
	} else {
	    IResult result = IResult.PUSH_SUCCESS;
	    T parent = m;
	    setMessage(m, true);
	    for (_pushIndex = 0; _pushIndex < _multiplier; _pushIndex++) {		
		_m = (T) views.get(_pushIndex);
		//Log.d(TAG, "push " + parent.hashCode() + " index: " + pushIndex);
		result = _owner.processInput(this, _m); 
		if (result  != IResult.PUSH_SUCCESS) break;	
	    }
	    _pushIndex = -1;
	    return result;
	}
    }
    
    public IResult setMessage(Message m, boolean push) throws CSenseException {
	if (_m != null) {
	    m.drop();
	    return IResult.PUSH_DROP; 
	}

	Debug.logMessagePush(_owner, m);
	m.setOwner(_owner);
	if ((_multiplier > 1) && (m.getParent() == null)) {
	    views = ((RawMessage) m).split(_owner, _multiplier);
	    if (push == false) {
		_pollIndex = 0;
		_m = (T) views.get(_pollIndex);
	    }
	} else { 	
	    _m = (T) m;
	}	
	return IResult.PUSH_SUCCESS;
    }

    @Override
    public T poll() throws CSenseException {	
	if (_pollIndex < 0)  {
	    if (_m != null) {
		throw new CSenseException(CSenseErrors.ERROR);
	    }
	    T m = _out.onPoll();
	    if (m != null) {
		setMessage(m, false);
	    }
	    return m;
	} else {
	    _m = (T) views.get(_pollIndex);
	    //Log.d(TAG, " poll " + _m.getParent().hashCode() + " index: " + _pollIndex);
	    return _m;
	}
    }

    @Override
    public void clear() {
	if (_m != null) {	    
	    if (_pollIndex >= 0) {
		// we are in poll mode 
		_pollIndex = _pollIndex + 1;
		if (_pollIndex == _multiplier) {
		    //Log.d(TAG, " poll-clear " + _m.getParent().hashCode() + " index: " + _pollIndex);
		    _pollIndex = -1;
		}
	    }
	}
	_m = null;
    }

    @Override
    public boolean hasMessage() {
	return _m != null;
    }

    @Override
    public T getMessage() {
	if (_multiplier == 1) {
	    if (_m != null) Debug.logMessageInput(_owner, _m);
	} else if (_pushIndex == 0 || _pollIndex == 0) {
	    if(_m != null) Debug.logMessageInput(_owner, _m.getParent());
	}
	
	return _m;
    }

 
    @Override
    public boolean getSupportsPoll() {
	return _out.getSupportsPoll();
    }

    @Override
    public void setMultiplier(int multiplier) {
	this._multiplier = multiplier;	
    }

    @Override
    public int geMultiplier() {
	return this._multiplier;
    }

    @Override
    public String toString() {
	if  (_m == null) return _name + " " + " M:" + _multiplier + " [NO MESSAGE]";
	else return _name + " " + " M:" + _multiplier + " [MESSAGE]";
    }

}
