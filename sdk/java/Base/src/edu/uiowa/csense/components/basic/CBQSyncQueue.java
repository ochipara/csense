package edu.uiowa.csense.components.basic;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Constants;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.ILog;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.concurrent.IState;
import edu.uiowa.csense.runtime.concurrent.CSenseBlockingQueue;

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
public class CBQSyncQueue<T extends Frame> extends SyncQueue<T> {
    public final InputPort<T> in = newInputPort(this, "dataIn");
    public final OutputPort<T> out = newOutputPort(this, "dataOut");
    protected final CSenseBlockingQueue<Frame> _queue;
    protected IComponent _nextComponent;

    private static final int level = ILog.INFO;

    public CBQSyncQueue(int capacity) throws CSenseException {
  	super();
  	_queue = new CSenseBlockingQueue<Frame>(capacity);
      }
    
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

    /**
     * The function may be called from a different a different thread
     */
    @SuppressWarnings("unused")
    @Override
    public <T2 extends Frame> int onPush(InputPort<T2> self, Frame m) throws CSenseException {
	if (Thread.currentThread() == getScheduler().getThread()) {
	    if (transition(IState.STATE_READY, IState.STATE_RUNNING) == true) {
		// call the doInput method
		onInput();
		in.clear();
		return Constants.PUSH_SUCCESS;
	    } else {
		throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR, "This should not happen");
	    }
	} else {	    
	    if (_queue.offer(m)) {
		in.clear();
		getScheduler().schedule(this, asTask());		
		return Constants.PUSH_SUCCESS;
	    } else {
		m.drop();
		in.clear();
		getScheduler().schedule(this, asTask());
		if (level <= ILog.DEBUG) warn("Droppeed", m);
		return Constants.PUSH_DROP;
	    }
	}
    }

    @Override
    public void onInput() throws CSenseException {
	// FIXME only executed when no thread-switching 
	T msg = in.getFrame();
	try {
	    _queue.put(msg);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    if (getState().getState() == IState.STATE_STOPPED) {
		return;
	    }
	}
	getScheduler().schedule(this, asTask());
    }

    @SuppressWarnings("unused")
    @Override
    public void onEvent(Event t) throws CSenseException {
	T m = (T) _queue.poll();
	if (m != null) {
	    Debug.logMessageInput(this, m);
	    if (out.push(m) == Constants.PUSH_SUCCESS) {
		if (level <= ILog.VERBOSE) {
		    verbose("push", _queue.size());
		}
	    } else {
		error("push fail");
		throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR);
	    }
	}

	if (!_queue.isEmpty()) getScheduler().schedule(this, asTask());
    }
}
