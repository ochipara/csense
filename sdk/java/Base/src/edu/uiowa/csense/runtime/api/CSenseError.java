package edu.uiowa.csense.runtime.api;

public class CSenseError {
    protected int _error = 0;
    public static final CSenseError SUCCESS = new CSenseError(0);

    /**
     * Throw when the underlying configuration graph has initialization issue
     * e.g., ports cannot be found
     * 
     */
    public static final CSenseError CONFIGURATION_ERROR = new CSenseError(1);

    /**
     * Throw when the component makes an illegal transition
     */
    public static final CSenseError ILLEGAL_TRANSITION = new CSenseError(2);

    /**
     * Throw when an unsupported operation is performed e.g., a poll on a
     * component that does not support it
     */
    public static final CSenseError UNSUPPORTED_OPERATION = new CSenseError(3);

    /**
     * Thrown when an a packet is dropped
     */
    public static final CSenseError DROP_PACKET = new CSenseError(4);

    /**
     * Thrown when the queue is full
     * 
     */
    public static final CSenseError QUEUE_FULL = new CSenseError(5);

    /**
     * Component is busy
     */
    public static final CSenseError BUSY = new CSenseError(6);

    /**
     * Component suffered a synchronization error
     */
    public static final CSenseError SYNCHRONIZATION_ERROR = new CSenseError(7);

    /**
     * An operation was interrupted. This wraps the interrupted exception
     */
    public static final CSenseError INTERRUPTED_OPERATION = new CSenseError(8);

    /**
     * A component received another message before completing processing the current one
     */    
    public static final CSenseError MISSING_QUEUE = new CSenseError(9);
    
    /**
     * The toolkit implementatio is improperly configured
     */
    public static final CSenseError FACTORY_ERROR = new CSenseError(10);
    
    /**
     * Generic error condition
     */
    public static final CSenseError ERROR = new CSenseError(255);
    
    

    public CSenseError(int error) {
	_error = error;
    }

    public int error() {
	return _error;
    }

    @Override
    public String toString() {
	if (_error == CSenseError.SUCCESS.error()) {
	    return "Success";
	} else if (_error == CSenseError.CONFIGURATION_ERROR.error()) {
	    return "Configuration error";
	} else if (_error == CSenseError.ERROR.error()) {
	    return "Generic error";
	} else if (_error == CSenseError.QUEUE_FULL.error()) {
	    return "Queue is full";
	} else if (_error == CSenseError.ILLEGAL_TRANSITION.error()) {
	    return "Illegal transition on component";
	} else if (_error == CSenseError.UNSUPPORTED_OPERATION.error()) {
	    return "Unsupported operation";
	} else if (_error == CSenseError.DROP_PACKET.error()) {
	    return "Dropped packet";
	} else if (_error == CSenseError.BUSY.error()) {
	    return "Component is busy";
	} else if (_error == CSenseError.SYNCHRONIZATION_ERROR.error()) {
	    return "Synchronization error";
	} else if (_error == CSenseError.INTERRUPTED_OPERATION.error()) {
	    return "Interrupted operation";
	} else if (_error == CSenseError.MISSING_QUEUE.error()) {
	    return "A component received a frame before completing processing the previous one. You may want to add queue.";
	} else if (_error == CSenseError.FACTORY_ERROR.error()) {
	    return "The toolkit is improperly configured. Check csense.xml";
	}

	return "code (" + _error + ")";
    }
}
