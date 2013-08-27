package components.basic;

import base.Debug;
import base.concurrent.CSenseBlockingQueue;

import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IComponent;
import api.IInPort;
import api.ILog;
import api.IOutPort;
import api.IResult;
import api.Message;
import api.Task;

/**
 * This class implements the synchronized queue. It should be used between
 * sources that operate in other threads and the toolkit.
 * 
 * Limitations: currently supports just push
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class CBQSyncQueue<T extends Message> extends CSenseComponent {
    public final IInPort<T> in = newInputPort(this, "dataIn");
    public final IOutPort<T> out = newOutputPort(this, "dataOut");
    protected final CSenseBlockingQueue<T> _queue;
    protected IComponent _nextComponent;

    private long _count = 0;
    private static final int level = ILog.INFO;

    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	_nextComponent = out.nextComponent();
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	_queue.clear();
    }

    public CBQSyncQueue(int capacity) throws CSenseException {
	super();
	_queue = new CSenseBlockingQueue<T>(capacity);
    }

    protected void log() throws CSenseException {}

    /**
     * The function may be called from a different a different thread
     */
    @SuppressWarnings("unused")
    @Override
    public <T2 extends Message> IResult processInput(IInPort<T2> input, T2 m) throws CSenseException {
	if (Thread.currentThread() == getScheduler().getThread()) {
	    if (transition(STATE_READY, STATE_RUNNING) == true) {
		// call the doInput method
//		m.log(this, Debug.TRACE_LOC_INPUT);
		doInput();
		ready();
		log();			
		return IResult.PUSH_SUCCESS;
	    } else {
		throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR, "This should not happen");
	    }
	} else {
	    if (_queue.offer((T) m)) {
		log();
		in.clear();
		getScheduler().schedule(this, asTask());		
		return IResult.PUSH_SUCCESS;
	    } else {
		log();
		m.drop();
		in.clear();
		getScheduler().schedule(this, asTask());
		if (level <= ILog.DEBUG) debug("Droppeed", m);
		return IResult.PUSH_DROP;
	    }
	}
    }

    @Override
    public void doInput() throws CSenseException {
	// FIXME only executed when no thread-switching 
	T msg = in.getMessage();
	try {
	    _queue.put(msg);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    if (getState().getState() == STATE_STOPPED) {
		return;
	    }
	}
	getScheduler().schedule(this, asTask());
    }

    @SuppressWarnings("unused")
    @Override
    public void doEvent(Task t) throws CSenseException {
//	T m = _queue.peek();
//	if (m != null) {
//	    m.log(this, Debug.TRACE_LOC_INPUT);
//	    if (out.push(m) == IResult.PUSH_SUCCESS) {
//		_queue.remove();
//		if (level <= ILog.VERBOSE)
//		    verbose("push", _count, _queue.size());
//		_count += 1;
//	    } else {
//		error("push fail");
//		throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR);
//	    }
//	    log();
//	}
//
//	if (!_queue.isEmpty()) getScheduler().schedule(this, asTask());
	
	T m = _queue.poll();
	if (m != null) {
	    Debug.logMessageInput(this, m);
	    if (out.push(m) == IResult.PUSH_SUCCESS) {
		if (level <= ILog.VERBOSE) {
		    verbose("push", _count, _queue.size());
		}
		_count += 1;
	    } else {
		error("push fail");
		throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR);
	    }
	    log();
	}

	if (!_queue.isEmpty()) getScheduler().schedule(this, asTask());
    }
}
