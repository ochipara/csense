package edu.uiowa.csense.runtime.v4;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.ILog;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.ISource;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class NoMessagePool implements FramePool {
    static public final int MAX_CAPACITY = 8;
    private int _numBytes;
    private int _capacity;
    private boolean _direct;
    private Constructor _constructor;
    private TypeInfo _type = null;
    private ISource _source = null;
    
    
    public final AtomicInteger _size;

    private final static int level = ILog.VERBOSE;

    public NoMessagePool(TypeInfo type, int capacity) {
	_numBytes = type.getNumBytes();
	_direct = type.isDirect();
	_capacity = capacity < 0 ? 0 : capacity > MAX_CAPACITY ? MAX_CAPACITY : capacity;
	_type = type;
	_size = new AtomicInteger(_capacity);

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
    public int size() {
	return _size.intValue();
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
	return _size.intValue() > 0;
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
	while(true) {
	    int value = _size.decrementAndGet();
	    if (value > 0) break;
	    else _size.incrementAndGet();
	};
	
	Frame m = allocate();	
	return m;
    }

    /**
     * Puts a message in the pool.
     * 
     * @param message
     *            the message to put
     * @return true if the pool is not full, false otherwise
     */
    @SuppressWarnings("unused")
    @Override
    public void put(Frame msg) {
	_size.incrementAndGet();
	if(_source != null) Debug.logMessageReturn(_source, msg);	
    }

    @Override
    public Frame getAndBlock() {
	while(true) {
	    int value = _size.decrementAndGet();
	    if (value > 0) break;
	    else _size.incrementAndGet();
	};
	Frame m = allocate();

	return m;
    }
    
    @Override
    public void setSource(ISource source) {
	_source = source;
    }

    public int used() {
	return 1; 
    }

    @Override
    public String toString() {
	return "owner " + _source.getName() + " " + getAvailable();
    }

    @Override
    public int getAvailable() {
	return _capacity - _size.intValue();
    }

  

}

