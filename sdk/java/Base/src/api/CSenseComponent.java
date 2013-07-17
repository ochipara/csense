package api;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.concurrent.SynchronizedStateManager;
import base.v2.InPortImpl;
import base.v2.OutPortImpl;

import compatibility.Log;

import api.CSenseErrors;
import api.CSenseException;
import api.Command;
import api.IComponent;
import api.IComponentInternal;
import api.IResult;
import api.Message;
import api.IScheduler;
import api.IInPort;
import api.IOutPort;
import api.Task;
import api.concurrent.IComponentState;

public class CSenseComponent implements IComponentInternal {
    protected final Map<String, IInPort<? extends Message>> _inPorts = new HashMap<String, IInPort<? extends Message>>();
    private final List<IInPort<? extends Message>> _inPortList = new ArrayList<IInPort<? extends Message>>();

    protected final Map<String, OutPortImpl<? extends Message>> _outPorts = new HashMap<String, OutPortImpl<? extends Message>>();

    protected IScheduler _scheduler = null;
    protected final IComponentState _state;

    protected String _name = getClass().getName();
    protected int _inPortSize;
    protected boolean eof = false;
    protected int _id = -1;

    private Task _task = new Task();
    protected IResult result = null;

    public CSenseComponent() {
	_state = new SynchronizedStateManager();
    }

    protected Collection<IInPort<? extends Message>> getInputPorts() {
	return _inPorts.values();
    }

    protected Collection<OutPortImpl<? extends Message>> getOutputPorts() {
	return _outPorts.values();
    }

    @Override
    public <T extends Message> IOutPort<T> newOutputPort(CSenseComponent owner, String name) throws CSenseException {
	OutPortImpl<T> port = new OutPortImpl<T>(owner, name);
	if (_outPorts.containsKey(name))
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR,
		    "Cannot have multiple output ports with the same name");
	_outPorts.put(name, port);

	return port;
    }

    @Override
    public <T extends Message> IInPort<T> newInputPort(CSenseComponent owner, String name) throws CSenseException {
	InPortImpl<T> port = new InPortImpl<T>(owner, name);
	if (_inPorts.containsKey(name))
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR,
		    "Cannot have multiple input ports with the same name");
	_inPorts.put(name, port);
	_inPortList.add(port);

	_inPortSize = _inPortList.size();
	return port;
    }

    @Override
    public IInPort<? extends Message> getInputPort(String name)
	    throws CSenseException {
	if (_inPorts.containsKey(name) == false)
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR,
		    "Cannot find port " + name + " in component "
			    + getName());
	return _inPorts.get(name);
    }

    @Override
    public IOutPort<? extends Message> getOutputPort(String name)
	    throws CSenseException {
	if (_outPorts.containsKey(name) == false) {
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR,
		    "Cannot find port " + name + " in component "
			    + getName());
	}
	return _outPorts.get(name);
    }

    @Override
    public <T extends Message> void link(IInPort<T> in, IOutPort<T> out)
	    throws CSenseException {
	in.link(out);
	out.link(in);
    }

    @Override
    public void setScheduler(IScheduler scheduler) {
	_scheduler = scheduler;
    }

    @Override
    public IScheduler getScheduler() {
	return _scheduler;
    }

    @Override
    public IComponentState getState() {
	return _state;
    }

    @Override
    public void transitionTo(int newState) throws CSenseException {
	_state.transitionTo(newState);
    }

    @Override
    public void onCreate() throws CSenseException {
	transitionTo(IComponent.STATE_CREATED);	
    }

    @Override
    public void onStart() throws CSenseException {
	//_state.assertState(IComponent.STATE_CREATED);
    }

    @Override
    public void onStop() throws CSenseException {
	//_state.assertState(IComponent.STATE_RUNNING, IComponent.STATE_READY);	
    }

    /**
     * The following methods implement the push behavior
     * 
     * @throws CSenseException
     */
    @Override
    public <T extends Message> IResult processInput(IInPort<T> input, T m) throws CSenseException {
	if (Thread.currentThread() != getScheduler().getThread()) {
	    throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR);
	}

	if (m.isEof()) eof = true;
	// check if all the puts have data to process
		
	for (int i = 0; i < _inPortSize; i++) {
	    IInPort<? extends Message> port = _inPortList.get(i);	   
	    
	    if (port.hasMessage() == false) {
		if (port.getSupportsPoll()) {
		    Message requestedMsg = port.poll();
		    if (requestedMsg == null) return IResult.PUSH_SUCCESS;
		} else {
		    return IResult.PUSH_SUCCESS;
		}

		// you have to return even after doing a poll since it will be
		// followed immediately by a push
		//return IResult.PUSH_SUCCESS;
	    } 
	}

	// all the messages are ready
//	for (int i = 0; i < _inPortSize; i++) {
//	    Message mlog = _inPortList.get(i).getMessage();
//	    if (mlog.getParent() != null) {
//		mlog.getParent().log(this, Debug.TRACE_LOC_INPUT);
//	    } else {
//		mlog.getParent();
//	    }
//	}

	if (transition(STATE_READY, STATE_RUNNING) == true) {
	    // call the doInput method
	    result = IResult.PUSH_SUCCESS;
	    	    
	    doInput();

	    // check the result
	    IResult finalResult = onError(result);
	    ready();
	    return finalResult;
	} else {
	    throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR, "This should not happen");
	}
    }

    @Override
    public IResult onError(IResult result) throws CSenseException {
	return result;
    }

    @Override
    public void accumulateResult(IResult result) {
	if (result == IResult.PUSH_SUCCESS) return;
	else {
	    this.result = result;
	}	
    }

    @Override
    public IResult getResult() {
	return this.result;
    }


    /**
     * The following methods implement the push behavior
     * @throws CSenseException 
     */
//    @Override
//    public <T extends Message> IResult processInput(IInPort<T> input, T m) throws CSenseException {
//	throw new UnsupportedOperationException();
//    }

    @Override
    public void doInput() throws CSenseException {
	return;
    }

    @Override
    public void processInput(SelectionKey key) {
    }

    @Override
    public IComponentInternal getUnderlyingComponent() {
	return this;
    }

    /**
     * The following is invoked when the timer fires
     * @throws CSenseException 
     */
    @Override
    public void doEvent(Task t) throws CSenseException {
    }

    @Override
    public Task asTask() {
	return _task;
    }

    @Override
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	throw new CSenseException(CSenseErrors.UNSUPPORTED_OPERATION, "Polling is not supported by this component");
    }

    @Override
    public String getName() {
	return _name;
    }

    @Override
    public void setName(String name) {
	_name = name;
	CSense.registerComponent(this);
    }

    @Override
    public void error(Object... args) {
	Log.e(_name, args);
    }

    @Override
    public void warn(Object... args) {
	Log.w(_name, args);
    }

    @Override
    public void info(Object... args) {
	Log.i(_name, args);
    }

    @Override
    public void debug(Object... args) {
	Log.d(_name, args);

    }

    @Override
    public void verbose(Object... args) {
	Log.v(_name, args);
    }

    @Override
    public String toString() {
	return _name + " state=" + _state.toString();
    }
    
    @Override
    public int getId() {
	return _id;
    }

    @Override
    public void ready() throws CSenseException {
	if (_state.hasInput() == true) {
	    _state.setHasInput(false);
	}
	transitionTo(STATE_READY);

	// the locks are on the ports
	// so we should do this last
	final int size = _inPortList.size();
	for (int i = 0; i < size; i++) {
	    IInPort<? extends Message> port = _inPortList.get(i);
	    port.clear();
	}
    }

    @Override
    public void running() throws CSenseException {
	transitionTo(STATE_RUNNING);
    }
    
    @Override
    public void setId(int id) {
	_id = id;
    }

    @Override
    public boolean transition(int expectedState, int newState) {
	return _state.transitionTo(expectedState, newState);
    }

    @Override
    public int command(Command cmd) {
	return -1;
    }

    @Override
    public boolean isEof() {
	return eof;
    }


}
