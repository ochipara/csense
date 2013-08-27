package api;

import api.CSense;
import api.CSenseErrors;
import api.CSenseException;
import api.CSenseOptions;
import api.IMessagePool;
import api.ISource;
import api.Message;
import messages.TypeInfo;
import base.Debug;


/**
 * Every component that is a source, writes to memory, must inherit from this
 * class. This class allows each source to build a local message pool. These
 * message pools are inherently synchronized because the message pools are built
 * using Java's ConcurrentLinkedQueue data structure.
 * 
 * The message pool is the only way for a component to get free messages. A
 * message can used as a buffer to store data for long without being returned in
 * the same order it is taken out.
 * 
 * @author Austin, Farley
 * 
 */
public class CSenseSource<T extends Message> extends CSenseComponent implements ISource<T> {
    protected TypeInfo<T> _type;
    protected IMessagePool<T> _pool = null;

    private volatile boolean _logging;

    public CSenseSource(TypeInfo<T> type) throws CSenseException {
	super();
	_type = type;
    }

    public void setupMessagePoolFromTypeInfo(int capacity)
	    throws CSenseException {
	if (_pool != null) {
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "Component intialized twice");
	}

	_pool = CSense.csense.newMessagePool(_type, capacity);
	_pool.setSource(this);	
    }
    
    @Override
    public void onCreate() throws CSenseException {	
	super.onCreate();
	setupMessagePoolFromTypeInfo(CSenseOptions.INIT_MSG_POOL_CAPACITY);
    }
    
    /**
     * Retrieves a message from a message pool if available, otherwise it blocks
     * until a message is freed.
     * 
     * @return a fresh message to use.
     * @throws CSenseException
     */
    @Override
    public T getNextMessageToWriteInto() {
	T m = _pool.get();
	if (m == null) {
	    error("message pool is empty");
	    return null;
	}	
	Debug.logMessageSource(this, m);
	return m;
    }
    
    @Override
    public T getNextMessageToWriteIntoAndBlock() throws InterruptedException {
	T m = _pool.getAndBlock();
	if (m == null) {
	    return null;
	}
	Debug.logMessageSource(this, m);
	return m;
    }

    @Override
    public void onStop() throws CSenseException {
	_pool = null;
    }
   
    @Override
    public int getAvailableMessages() {
	return _pool.getAvailable();
    }
}
