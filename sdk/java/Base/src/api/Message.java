package api;

import java.util.List;

import compatibility.Log;

import base.Route;

import messages.TypeInfo;
import api.IMessagePool;
import api.IRoute;
import api.Message;

/**
 * This is for objects sent between components. Messages carry information
 * between components. Every message is associated by a particular message pool.
 * No component is allowed to create a message.
 * 
 * @author Austin, Farley
 * 
 */
public class Message implements IMessage {
    private static final String TAG = "message";
    protected final IMessagePool<Message> _pool;
    protected final IMessage _parent;

    // fields whose access must be synchronized
    // these fields are shared across potentially multiple threads
    protected int _ref = 1;    
    protected Message _message;
    private Route _route = null;
    private boolean _eof = false;
    private int id = -1;
    
    protected ThreadLocal<CSenseComponent> _owner = new ThreadLocal<CSenseComponent>() {
	@Override
	public CSenseComponent initialValue() {
	    return null;
	}
    };

    public CSenseComponent getOwner() {
        return _owner.get();
    }

    public void setOwner(CSenseComponent owner) {
        this._owner.set(owner);
    }

    public Message(IMessagePool<? extends Message> pool, TypeInfo<? extends Message> type) {
	this(pool, type, null);
    }

    public Message(IMessagePool<? extends Message> pool, TypeInfo<? extends Message> type, IMessage parent) {	
	_pool = (IMessagePool<Message>) pool;
	_parent = parent;
	_ref = 1;
	_eof = false;
    }

    @Override
    public synchronized void initialize() {
	_ref = 1;
	_eof = false;
    }

    /**
     * The methods incrementReference/decrementReference/free must provide
     * concurrent access to the _ref The ref variable may be accessed from
     * multiple threads, when the stream is split
     * 
     */
    @Override
    public synchronized void incrementReference() {
	if (_parent == null) {
	    _ref++;
	} else {
	    _parent.incrementReference();	    
	}
    }

    @Override
    public synchronized void incrementReference(int count) {
	if (_parent == null) {
	    _ref += count;
	} else {
	    _parent.incrementReference(count);
	} 
    }

    @Override
    public synchronized void decrementReference() {
	if (_parent == null) {
	    _ref--;
	    if (_ref < 0) {
		throw new IllegalStateException();
	    }
	    if (_ref == 0) free();
	   
	} else {
	    _parent.decrementReference();
	}
    }

    @Override
    public synchronized void free() {
	if (_parent == null) {
	    _pool.put(this);
	}
    }

    @Override
    public synchronized void drop() {
	if(_route != null) {
	    _route.clear();
	}
	Log.w(TAG, "drop");
	decrementReference();
    }

    @Override
    public synchronized int getReference() {
	return _ref;
    }

    @Override
    public synchronized IRoute getRoute() {
	return _route;
    }

    @Override
    public synchronized void eof() {
	_eof = true;

    }

    @Override
    public synchronized boolean isEof() {
	return _eof;
    }

    @Override
    public synchronized List<Message> split(CSenseComponent component, int numFrames) throws CSenseException {
	throw new CSenseException(CSenseErrors.UNSUPPORTED_OPERATION);
    }
    
    @Override
    public String toString() {
	return "R: " + _ref + " owner: " + _owner.get();
    }

    public IMessage getParent() {
	return _parent;
    }

    @Override
    public void setPoolId(int id) throws CSenseException {
	if (this.id == -1) this.id = id;
	else throw new CSenseException("Pool id cannot be modified once they are set");
    }
    
    @Override
    public int getPoolId() {
	return this.id;

    }
    
    @Override
    public int getId() {
	return hashCode();
    }

}