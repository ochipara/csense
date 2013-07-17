package base.concurrent;

import api.CSenseErrors;
import api.CSenseException;
import api.IComponent;
import api.concurrent.IComponentState;

public class SynchronizedStateManager implements IComponentState {
    private int _state = IComponent.STATE_INIT;
    private boolean _hasInput = false;
    private boolean _hasEvent = false;

    @Override
    public synchronized void transitionTo(int newState) throws CSenseException {
	if (newState == IComponent.STATE_CREATED) {
	    if ((_state != IComponent.STATE_INIT) && (_state != IComponent.STATE_STOPPED)) {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IComponent.STATE_READY) {
	    if ((_state != IComponent.STATE_CREATED)
		    && (_state != IComponent.STATE_RUNNING)
		    && (_state != IComponent.STATE_READY)) {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IComponent.STATE_RUNNING) {
	    if (_state == IComponent.STATE_RUNNING) {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	    if (_state != IComponent.STATE_READY) {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	} else if (newState == IComponent.STATE_STOPPED) {
	    if ((_state != IComponent.STATE_RUNNING)
		    && (_state != IComponent.STATE_READY)) {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	} else {
	    throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION,
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
	if (_state != state)
	    throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
    }

    @Override
    public synchronized void assertState(int state1, int state2)
	    throws CSenseException {
	if ((_state != state1) && (_state != state2)) {
	    throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION,
		    "onStop() called during an invalid state");
	}
    }

    @Override
    public synchronized String toString() {
	switch (_state) {
	case IComponent.STATE_INIT:
	    return "init";

	case IComponent.STATE_CREATED:
	    return "started";

	case IComponent.STATE_RUNNING:
	    return "running";

	case IComponent.STATE_STOPPED:
	    return "stopped";

	case IComponent.STATE_READY:
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
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
	    }
	} else {
	    if (state == false)
		_hasInput = false;
	    else {
		throw new CSenseException(CSenseErrors.ILLEGAL_TRANSITION);
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
