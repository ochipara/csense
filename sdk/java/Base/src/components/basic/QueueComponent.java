package components.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.IResult;
import api.Message;
import api.Task;
import base.Utility;

/**
 * This component acts as a queue to store messages pushed from upstream and allows downstream components to poll. 
 * I/O: multiple input ports and one single output port of the same message type (MISO).
 * 
 * Use Cases
 * 1. upstream components push while downstream components never poll
 * 	  The Queue component tries to push whenever it has messages queued.
 * 2. upstream components push while downstream components poll
 *    The Queue component adapts to producer and consumer rate changes over time.
 *    
 * Limitation: upstream components must push messages. 
 * 
 * Implementation
 * This component operates in two states: PushState and PullState, following the State pattern.
 * The state transition from push to pull happens when push() fails.
 * The state transition from pull to push happens when polling can not be satisfied because the queue is empty.
 * (see http://en.wikipedia.org/wiki/State_pattern)
 * 
 * @author Farley Lai
 */
public class QueueComponent<T extends Message> extends CSenseComponent {	
    private interface State {
	void doInput();
	public Message onPoll(IOutPort<? extends Message> port) throws CSenseException;
    }

    private State PushState = new State() {
	@Override
	@SuppressWarnings("unchecked")
	public void doInput() {
	    try {
		if (_out.nextComponent().getState().getState() == STATE_READY) {
		    T m = _queue.poll();
		    _out.push(m);
		    _pulled = false;
		    //debug("succeeded to push a message");
		} else {
		    if (_out.getSupportsPoll()) {
			_state = PollState;
		    }
		}
	    } catch (CSenseException e) {
		if(_out.getSupportsPoll()) {
		    // producer is faster than consumer
		    //warn(QueueComponent.this, "failed to push a message downstream, switches to the pull state");
		    _state = PollState;
		} else {
		    //warn("failed to push a message downstream, reschedule");
		    getScheduler().schedule(QueueComponent.this, asTask());
		}
	    }				
	}

	@Override
	public Message onPoll(IOutPort<? extends Message> port) throws CSenseException { 
	    return null;
	}
    };

    private State PollState = new State() {
	@Override
	public void doInput() {
	    if(_pulled) {
		debug("push a message to satisfy a previous pull request");
		PushState.doInput();
	    }
	}

	@Override
	public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	    if(_queue.isEmpty()) {
		// consumer is faster than producer
		_state = PushState;			
		debug("failed to satisfy a pull request immediately, switches to the push state");				
		return null;
	    }

	    //FIXME
	    //push() called in onPoll() will cause infinite looping.
	    //One solution is to schedule to execute push() in the next round.
	    //PushState.doInput();
	    getScheduler().schedule(QueueComponent.this, asTask());
	    return null;
	}
    };

    private List<IInPort<T>> _in; 
    private IOutPort<T> _out;
    private BlockingQueue<T> _queue;
    private State _state;
    private boolean _pulled;

    /**
     * Constructs an empty push queue with only one input port.
     * @param capacity number of items that the queue can hold
     * @throws CSenseException fails to create new ports
     */
    public QueueComponent(int capacity) throws CSenseException {
	this(capacity, 1);
    }

    /**
     * Constructor that creates an empty queue where messages will be stored.
     * @param capacity number of items that the queue can hold
     * @param ports number of input ports the queue can link
     * @throws CSenseException fails to create new ports
     */
    public QueueComponent(int capacity, int ports) throws CSenseException {
	this(capacity, ports, false);
    }

    /**
     * Constructor that creates an empty queue where messages will be stored.
     * @param capacity number of items that the queue can hold
     * @param ports number of input ports the queue can accepts
     * @param pullable indicates if the queue can be polled
     * @throws CSenseException fails to create new ports
     */
    public QueueComponent(int capacity, int ports, boolean pollable) throws CSenseException {
	if(capacity <= 0 || ports <= 0) throw new IllegalArgumentException("capacity and ports should be positive");
	_in = new ArrayList<IInPort<T>>(ports);
	for(int i = 0; i < ports; i++) _in.add(this.<T>newInputPort(this, Utility.toString("in", i)));
	_out = newOutputPort(this, "out");
	_out.setSupportPull(pollable);
	_state = PushState;
	_queue = new ArrayBlockingQueue<T>(capacity);
    }

    public int size() {
	return _queue.size();
    }

    @Override
    public <T2 extends Message> IResult processInput(IInPort<T2> port, T2 m) throws CSenseException {
	port.clear();
	boolean ret = _queue.offer((T) m);
	if(ret)	doInput();
	else throw new CSenseException(CSenseErrors.QUEUE_FULL, "queue is full");
	return IResult.PUSH_SUCCESS;
    }

    @Override
    public void doInput() throws CSenseException {
	_state.doInput();
    }

    @Override
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	_pulled = true;
	return _state.onPoll(port);
    }

    @Override
    public void doEvent(Task t) {
	if(t == asTask()) _state.doInput();
    }
}
