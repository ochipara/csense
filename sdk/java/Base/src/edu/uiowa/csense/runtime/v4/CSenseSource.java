package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseToolkit;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.ISource;
import edu.uiowa.csense.runtime.api.Options;
import edu.uiowa.csense.runtime.api.concurrent.IState;
import edu.uiowa.csense.runtime.types.TypeInfo;


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
public class CSenseSource<T extends Frame> extends CSenseComponent implements ISource<T> {
    protected TypeInfo<T> _type;
    protected FramePool _pool = null;

    private volatile boolean _logging;

    public CSenseSource(TypeInfo<T> type) throws CSenseException {
	super();	
	_type = type;
	_pool = CSenseToolkit.getImplementation().newFramePool(type, Options.INIT_MSG_POOL_CAPACITY);
	_pool.setSource(this);	

    }
    
    @Override
    public void onCreate() throws CSenseException {	
	super.onCreate();
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
	T m =  (T) _pool.get();
	if (m == null) {
	    error("message pool is empty");
	    return null;
	}	
	Debug.logMessageSource(this, m);
	return m;
    }
    
    @Override
    public T getNextMessageToWriteIntoAndBlock() throws InterruptedException {
	T m = (T) _pool.getAndBlock();
	if (m == null) {
	    return null;
	}
	Debug.logMessageSource(this, m);
	return m;
    }

    @Override
    public void onStop() throws CSenseException {
	_pool = null;
	transitionTo(IState.STATE_STOPPED);
    }
   
    @Override
    public int getAvailableMessages() {
	return _pool.getAvailable();
    }
}
