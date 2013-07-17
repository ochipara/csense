package components.basic;

import java.util.LinkedList;
import java.util.Queue;

import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.Message;
import api.Task;

public class SimpleQueue<T extends Message> extends CSenseComponent {
    public final IInPort<T> in = newInputPort(this, "in");
    public final IOutPort<T> out = newOutputPort(this, "out");
    protected final Queue<T> _queue;
    protected final int _capacity;

    public SimpleQueue(int capacity) throws CSenseException {
	_queue = new LinkedList<T>();
	_capacity = capacity;
	out.setSupportPull(true);
    }

    protected void pushNext() throws CSenseException {
	if (_queue.isEmpty() == false) {
	    if (out.nextComponent().getState().getState() == STATE_READY) {
		try {
		    T m = _queue.peek();
		    out.push(m);
		    _queue.remove();
		    transitionTo(STATE_READY);
		} catch (CSenseException e) {
		    throw e;
		}
	    }
	} else {
	    debug("queue is empty");
	}

	if (out.getSupportsPoll()) {
	    // supports poll so we should wait to be asked for next element
	} else {
	    getScheduler().schedule(this, asTask());
	}
    }

    @Override
    public void doInput() throws CSenseException {
	T m = in.getMessage();
	if (_queue.size() < _capacity) {
	    _queue.offer(m);

	    if (_queue.size() + 1 >= _capacity) {
		transitionTo(STATE_RUNNING);
	    }
	    pushNext();
	    info(" new element ", _queue.size());
	} else {
	    in.clear();
	    throw new CSenseException(CSenseErrors.QUEUE_FULL,
		    "queue size exceeded");
	}
    }

    @Override
    public void doEvent(Task t) throws CSenseException {
	pushNext();
    }

    @Override
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	getScheduler().schedule(this, asTask());
	return null;
    }

}
