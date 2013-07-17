package api;

public class CSenseError {
    protected int _error = 0;

    public CSenseError(int error) {
	_error = error;
    }

    public int error() {
	return _error;
    }

    @Override
    public String toString() {
	if (_error == CSenseErrors.SUCCESS.error()) {
	    return "Success";
	} else if (_error == CSenseErrors.CONFIGURATION_ERROR.error()) {
	    return "Configuration error";
	} else if (_error == CSenseErrors.ERROR.error()) {
	    return "Generic error";
	} else if (_error == CSenseErrors.QUEUE_FULL.error()) {
	    return "Queue is full";
	} else if (_error == CSenseErrors.ILLEGAL_TRANSITION.error()) {
	    return "Illegal transition on component";
	} else if (_error == CSenseErrors.UNSUPPORTED_OPERATION.error()) {
	    return "Unsupported operation";
	} else if (_error == CSenseErrors.DROP_PACKET.error()) {
	    return "Dropped packet";
	} else if (_error == CSenseErrors.BUSY.error()) {
	    return "Component is busy";
	} else if (_error == CSenseErrors.SYNCHRONIZATION_ERROR.error()) {
	    return "Synchronization error";
	} else if (_error == CSenseErrors.INTERRUPTED_OPERATION.error()) {
	    return "Interrupted operation";
	}

	return "code (" + _error + ")";
    }
}
