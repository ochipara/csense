package base.v2;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import base.Debug;
import messages.TypeInfo;

import compatibility.Log;

import api.CSenseException;
import api.CSenseRuntimeException;
import api.ILog;
import api.IMessagePool;
import api.ISource;
import api.Message;

public class MessagePoolArray <T extends Message> implements IMessagePool<T> {
    static public final int MAX_CAPACITY = 8;
    private int _numBytes;
    private int _capacity;
    private boolean _direct;
    private Constructor<T> _constructor;
    private TypeInfo<T> _type = null;
    private ISource<T> _source = null;

    // must be synchronized
    private List<T> _pool;
    private ArrayList<T> _checkout;
    private final static int level = ILog.VERBOSE;

    public MessagePoolArray(TypeInfo<T> type, int capacity) {
	_numBytes = type.getNumBytes();
	_direct = type.isDirect();
	_capacity = capacity < 0 ? 0 : capacity > MAX_CAPACITY ? MAX_CAPACITY : capacity;
	_pool = new ArrayList<T>(MAX_CAPACITY); //new LinkedBlockingQueue<T>(MAX_CAPACITY); //new ArrayBlockingQueue<T>(MAX_CAPACITY);
	_checkout = new ArrayList<T>(MAX_CAPACITY);
	_type = type;

	// this is more complicated because we need to instantiate an instance
	// of type T
	try {
	    // Log.d(_owner, "message type: ", type.getJavaType().toString());
	    _constructor = type.getJavaType().getDeclaredConstructor(IMessagePool.class, TypeInfo.class);
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
    protected synchronized T allocate() {
	try {
	    if (_type == null)
		return _constructor.newInstance(this, _numBytes, _direct);
	    else
		return _constructor.newInstance(this, _type);
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
    public synchronized T get() {
	T m = null;	
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
    public synchronized void put(T msg) {
	if(_source != null) Debug.logMessageReturn(_source, msg);
	if (_pool.add(msg) == false) {
	    throw new CSenseRuntimeException("Failed to put back a message to the pool of size " + _pool.size() + ", remaining capacity ");
	}
	_checkout.remove(msg);
    }

    @Override
    public void setSource(ISource<T> source) {
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
    public T getAndBlock() throws InterruptedException {
	throw new UnsupportedOperationException();
    }

}
