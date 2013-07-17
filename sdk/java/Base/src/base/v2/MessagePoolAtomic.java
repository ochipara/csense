package base.v2;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import base.Debug;
import base.concurrent.CSenseBlockingQueue;

import messages.TypeInfo;

import compatibility.Log;

import api.CSenseException;
import api.ILog;
import api.IMessagePool;
import api.ISource;
import api.Message;

public class MessagePoolAtomic<T extends Message> implements IMessagePool<T> {
    static public final int MAX_CAPACITY = 8;
    private int _numBytes;
    private int _capacity;
    private boolean _direct;
    private Constructor<T> _constructor;
    private TypeInfo<T> _type = null;
    private ISource<T> _source = null;

    // must be synchronized
    private CSenseBlockingQueue<T> _pool;
    //private ArrayList<T> _checkout;
    private final static int level = ILog.VERBOSE;

    public MessagePoolAtomic(TypeInfo<T> type, int capacity) {
	_numBytes = type.getNumBytes();
	_direct = type.isDirect();
	_capacity = capacity < 0 ? 0 : capacity > MAX_CAPACITY ? MAX_CAPACITY : capacity;
	_pool = new CSenseBlockingQueue<T>(MAX_CAPACITY); ///new ArrayList<T>(MAX_CAPACITY); //new LinkedBlockingQueue<T>(MAX_CAPACITY); //new ArrayBlockingQueue<T>(MAX_CAPACITY);
	//_checkout = new ArrayList<T>(MAX_CAPACITY);
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
    protected T allocate() {
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
    public T get() {
	T m = _pool.poll();
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
    public T getAndBlock() {
	T m;
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
    public void put(T msg) {
	if(_source != null) Debug.logMessageReturn(_source, msg);	
	try {
	    _pool.put(msg);
	} catch (InterruptedException e) {	 
	    e.printStackTrace();
	}
	//_checkout.remove(msg);
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
    public int getAvailable() {
	return _pool.size();
    }
}
