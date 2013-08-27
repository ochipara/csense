package edu.uiowa.csense.runtime.v4;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.Constants;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.Options;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.api.Task;
import edu.uiowa.csense.runtime.api.concurrent.IState;
import edu.uiowa.csense.runtime.compatibility.Log;

public class CSenseComponent implements IComponent {
    protected final Map<String, InputPort<? extends Frame>> _inPorts = new HashMap<String, InputPort<? extends Frame>>();
    private final List<InputPort<? extends Frame>> _inPortList = new ArrayList<InputPort<? extends Frame>>();

    protected final Map<String, OutPortImpl<? extends Frame>> _outPorts = new HashMap<String, OutPortImpl<? extends Frame>>();

    protected IScheduler _scheduler = null;
    protected final IState _state;

    protected String _name = getClass().getName();
    protected int _inPortSize;
    protected boolean eof = false;
    protected int _id = -1;

    private Task _task = new Task();
    private int multiplier = 1;

    public CSenseComponent() {
	_state = new SynchronizedStateManager();
    }

    @Override
    public <T extends Frame> OutputPort<T> newOutputPort(IComponent owner, String name) throws CSenseException {
	OutPortImpl<T> port = new OutPortImpl<T>(owner, name);
	if (_outPorts.containsKey(name))
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "Cannot have multiple output ports with the same name");
	_outPorts.put(name, port);

	return port;
    }

    @Override
    public <T extends Frame> InputPort<T> newInputPort(IComponent owner, String name) throws CSenseException {
	InPortImpl<T> port = new InPortImpl<T>(owner, name);
	if (_inPorts.containsKey(name))
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "Cannot have multiple input ports with the same name");
	_inPorts.put(name, port);
	_inPortList.add(port);

	_inPortSize = _inPortList.size();
	return port;
    }

    @Override
    public InputPort<? extends Frame> getInputPort(String name)
	    throws CSenseException {
	if (_inPorts.containsKey(name) == false)
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "Cannot find port " + name + " in component "
			    + getName());
	return _inPorts.get(name);
    }

    @Override
    public OutputPort<? extends Frame> getOutputPort(String name)
	    throws CSenseException {
	if (_outPorts.containsKey(name) == false) {
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "Cannot find port " + name + " in component "
			    + getName());
	}
	return _outPorts.get(name);
    }

    @Override
    public <T extends Frame> void link(InputPort<T> in, OutputPort<T> out) throws CSenseException {
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
    public IState getState() {
	return _state;
    }

    @Override
    public void transitionTo(int newState) throws CSenseException {
	_state.transitionTo(newState);
    }

    @Override
    public void onCreate() throws CSenseException {	
	transitionTo(IState.STATE_CREATED);	
    }

    @Override
    public void onStart() throws CSenseException {
	transitionTo(IState.STATE_READY);
    }

    @Override
    public void onStop() throws CSenseException {
	transitionTo(IState.STATE_STOPPED);	
    }

    /**
     * The following methods implement the push behavior
     * 
     * @throws CSenseException
     */
    @Override    
    public <T extends Frame> int onPush(InputPort<T> self, Frame frame) throws CSenseException {
	if (Options.CHECK_CURRENT_THREAD) {
	    if (Thread.currentThread() != getScheduler().getThread()) {
		throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR);
	    }
	}
	
	// check if all the puts have data to process		
	for (int i = 0; i < _inPortSize; i++) {
	    InputPort<? extends Frame> port = _inPortList.get(i);	   	    
	    if (port.hasFrame() == false) {
		if (port.getSupportsPoll()) {
		    int r = port.poll();
		    if (r == Constants.POLL_FAILED) return Constants.PUSH_COMPONENT_NOT_READY;
		} else {
		    return Constants.PUSH_COMPONENT_NOT_READY;
		}
	    } 
	}
	
	if (transition(IState.STATE_READY, IState.STATE_RUNNING) == true) {
	    // call the doInput method    	    
	    for (int i = 0; i < multiplier; i++) {
		onInput();
	    }
	    
	    // clear the input ports
	    for (int i = 0; i < _inPortSize; i++) {
		InputPort<? extends Frame> port = _inPortList.get(i);
		port.clear();
		transitionTo(IState.STATE_READY);
	    }
	    
	    return Constants.PUSH_SUCCESS;
	} else {
	    throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR, "This should not happen");
	}
    }

    @Override
    public void onInput() throws CSenseException {
	return;
    }

    @Override
    public void processInput(SelectionKey key) {
	throw new IllegalStateException();
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
    public String getName() {
	return _name;
    }

    @Override
    public void setName(String name) {
	_name = name;	
    }

    @Override
    public int getId() {
	return _id;
    }
    
    @Override
    public void setId(int id) {
	_id = id;
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
    public boolean transition(int expectedState, int newState) {
	return _state.transitionTo(expectedState, newState);
    }

    @Override
    public Frame onPoll(OutputPort<? extends Frame> port) throws CSenseException {
	throw new CSenseException(CSenseError.UNSUPPORTED_OPERATION);
    }

    @Override
    public void setMultiplier(int m) {
	this.multiplier = m;
    }  
}
