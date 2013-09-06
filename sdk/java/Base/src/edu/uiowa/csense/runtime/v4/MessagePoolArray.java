package edu.uiowa.csense.runtime.v4;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.ILog;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.ISource;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class MessagePoolArray implements FramePool {
    static public final int MAX_CAPACITY = 8;
    private int _numBytes;
    private int _capacity;
    private boolean _direct;
    private Constructor _constructor;
    private TypeInfo _type = null;
    private ISource _source = null;

    // must be synchronized
    private List<Frame> _pool;
    private ArrayList<Frame> _checkout;
    private final static int level = ILog.VERBOSE;

    public MessagePoolArray(TypeInfo type, int capacity) {
	_numBytes = type.getNumBytes();
	_direct = type.isDirect();
	_capacity = capacity < 0 ? 0 : capacity > MAX_CAPACITY ? MAX_CAPACITY : capacity;
	_pool = new ArrayList<Frame>(MAX_CAPACITY); //new LinkedBlockingQueue<T>(MAX_CAPACITY); //new ArrayBlockingQueue<T>(MAX_CAPACITY);
	_checkout = new ArrayList<Frame>(MAX_CAPACITY);
	_type = type;

	// this is more complicated because we need to instantiate an instance
	// of type T
	try {
	    // Log.d(_owner, "message type: ", type.getJavaType().toString());
	    _constructor = type.getJavaType().getDeclaredConstructor(FramePool.class, TypeInfo.class);
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	}

	for (int i = 0; i < _capacity; i++)
	    _pool.add(allocate());
    }

    /**
     * A generic method of creating message of the appropriate type as specified
     * by T
     * 
     * @param messagePool
     * @param capacity
     * @param direct
     * @return
     */
    protected synchronized Frame allocate() {
	try {
	    if (_type == null)
		return (Frame) _constructor.newInstance(this, _numBytes, _direct);
	    else
		return (Frame) _constructor.newInstance(this, _type);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Returns the number of allocated messages currently in the pool.
     * 
     * @return the number of messages allocated present in the pool
     */
    public synchronized int size() {
	return _pool.size();
    }

    /**
     * Returns the total number of allocated messages so far.
     * 
     * @return the total number of messages allocated
     */
    public synchronized int capacity() {
	return _capacity;
    }

    /**
     * Indicates if the pool is empty or not.
     * 
     * @return true if the pool is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
	return _pool.isEmpty();
    }

    /**
     * Returns a message in the pool. If the pool is empty, the operation will
     * return null if the maximum capacity is reached or a new message will be
     * allocated if not yet.
     * 
     * @return a message in the pool or a newly allocated message
     * @throws CSenseException
     */
    @SuppressWarnings("unused")
    @Override
    public synchronized Frame get() {
	Frame m = null;	
	if (_pool.size() > 0) {
	    m = _pool.get(0);
	    _pool.remove(0);
	    m.initialize();

	    if (level >= ILog.DEBUG) {
		Log.d(_source.getName(), "get", used());
	    }
	    
	    _checkout.add(m);
	}

	return m;
    }

    /**
     * Puts a message in the pool.
     * 
     * @param message
     *            the message to put
     * @return true if the pool is not full, false otherwise
     */
    @Override
    public synchronized void put(Frame msg) {
	if(_source != null) Debug.logMessageReturn(_source, msg);
	if (_pool.add(msg) == false) {
	    throw new CSenseRuntimeException("Failed to put back a message to the pool of size " + _pool.size() + ", remaining capacity ");
	}
	_checkout.remove(msg);
    }

    @Override
    public void setSource(ISource source) {
	_source = source;
    }

    public int used() {
	return _capacity - _pool.size();
    }

    @Override
    public String toString() {
	return "owner " + _source.getName() + " " + getAvailable();
    }

    @Override
    public synchronized int getAvailable() {
	return _pool.size();
    }

    @Override
    public Frame getAndBlock() throws InterruptedException {
	throw new UnsupportedOperationException();
    }

}
