package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.concurrent.IState;

public class SynchronizedStateManager implements IState {
    private int _state = IState.STATE_INIT;
    private boolean _hasInput = false;
    private boolean _hasEvent = false;

    @Override
    public synchronized void transitionTo(int newState) throws CSenseException {
	if (newState == IState.STATE_CREATED) {
	    if ((_state != IState.STATE_INIT) && (_state != IState.STATE_STOPPED)) {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IState.STATE_READY) {
	    if ((_state != IState.STATE_CREATED)
		    && (_state != IState.STATE_RUNNING)
		    && (_state != IState.STATE_READY)) {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IState.STATE_RUNNING) {
	    if (_state == IState.STATE_RUNNING) {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	    if (_state != IState.STATE_READY) {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IState.STATE_STOPPED) {
	    if ((_state != IState.STATE_RUNNING)
		    && (_state != IState.STATE_READY)) {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	} else {
	    throw new CSenseException(CSenseError.ILLEGAL_TRANSITION,
		    "unknown state");
	}

	_state = newState;
    }

    @Override
    public synchronized boolean transitionTo(int expectedState, int newState) {
	if (_state == expectedState) {
	    _state = newState;
	    return true;
	}

	return false;
    }

    @Override
    public synchronized int getState() {
	return _state;
    }

    @Override
    public synchronized void assertState(int state) throws CSenseException {
	if (_state != state) {
	    throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	}
    }

    @Override
    public synchronized void assertState(int state1, int state2)
	    throws CSenseException {
	if ((_state != state1) && (_state != state2)) {
	    throw new CSenseException(CSenseError.ILLEGAL_TRANSITION,
		    "onStop() called during an invalid state");
	}
    }

    @Override
    public synchronized String toString() {
	switch (_state) {
	case IState.STATE_INIT:
	    return "init";

	case IState.STATE_CREATED:
	    return "started";

	case IState.STATE_RUNNING:
	    return "running";

	case IState.STATE_STOPPED:
	    return "stopped";

	case IState.STATE_READY:
	    return "ready";

	default:
	    return "unknown state (should not happen)";
	}
    }

    @Override
    public synchronized void setHasInput(boolean state) throws CSenseException {
	if (_hasInput == false) {
	    if (state == true)
		_hasInput = true;
	    else {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	} else {
	    if (state == false)
		_hasInput = false;
	    else {
		throw new CSenseException(CSenseError.ILLEGAL_TRANSITION);
	    }
	}
    }

    @Override
    public synchronized boolean hasInput() {
	return _hasInput;
    }

    @Override
    public synchronized void setHasEvent(boolean state) throws CSenseException {
	_hasEvent = state;
    }

    @Override
    public synchronized boolean hasEvent() {
	return _hasEvent;
    }

}
