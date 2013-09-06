package edu.uiowa.csense.runtime.v4;

import java.lang.reflect.Constructor;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.ILog;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.ISource;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.concurrent.CSenseBlockingQueue;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class MessagePoolAtomic implements FramePool {
    static public final int MAX_CAPACITY = 8;
    private int _numBytes;
    private int _capacity;
    private boolean _direct;
    private Constructor _constructor;
    private TypeInfo _type = null;
    private ISource _source = null;

    // must be synchronized
    private CSenseBlockingQueue<Frame> _pool;
    //private ArrayList<T> _checkout;
    private final static int level = ILog.VERBOSE;

    public MessagePoolAtomic(TypeInfo type, int capacity) throws CSenseException {
	_numBytes = type.getNumBytes();
	_direct = type.isDirect();
	_capacity = capacity < 0 ? 0 : capacity > MAX_CAPACITY ? MAX_CAPACITY : capacity;
	_pool = new CSenseBlockingQueue<Frame>(MAX_CAPACITY); 
	_type = type;

	// this is more complicated because we need to instantiate an instance
	// of type T
	try {
	    _constructor = type.getJavaType().getDeclaredConstructor(FramePool.class, TypeInfo.class);
	} catch (SecurityException e) {
	    e.printStackTrace();
	    throw new CSenseException("Failed to instatiate frame", e);
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	    throw new CSenseException("Failed to instatiate frame", e);
	}

	for (int i = 0; i < _capacity; i++) {
	    _pool.offer(allocate());
	}
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
    protected Frame allocate() {
	try {
	    if (_type == null) {
		return (Frame) _constructor.newInstance(this, _numBytes, _direct);
	    } else {
		return (Frame) _constructor.newInstance(this, _type);
	    }
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
    public int size() {
	return _pool.size();
    }

    /**
     * Returns the total number of allocated messages so far.
     * 
     * @return the total number of messages allocated
     */
    public int capacity() {
	return _capacity;
    }

    /**
     * Indicates if the pool is empty or not.
     * 
     * @return true if the pool is empty, false otherwise
     */
    public boolean isEmpty() {
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
    public Frame get() {
	Frame m = _pool.poll();
	if (m != null) {	    
	    m.initialize();

	    if (level >= ILog.DEBUG) {
		Log.d(_source.getName(), "get", used());
	    }

	    //_checkout.add(m);
	}

	return m;
    }

    @Override
    public Frame getAndBlock() {
	Frame m;
	try {
	    m = _pool.take();
	    m.initialize();

	    if (level >= ILog.DEBUG) {
		Log.d(_source.getName(), "get", used());
	    }

	    //_checkout.add(m);

	    return m;
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    return null;
	}	
    }

    /**
     * Puts a message in the pool.
     * 
     * @param message the message to put
     * @return true if the pool is not full, false otherwise
     * @throws InterruptedException 
     */
    @SuppressWarnings("unused")
    @Override
    public void put(Frame msg) {
	if(_source != null) Debug.logMessageReturn(_source, msg);	
	try {
	    _pool.put(msg);
	} catch (InterruptedException e) {	 
	    e.printStackTrace();
	}
	//_checkout.remove(msg);
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
    public int getAvailable() {
	return _pool.size();
    }
}
