package api;

public class CSenseErrors {
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
     * Generic error condition
     */
    public static final CSenseError ERROR = new CSenseError(255);

}
