package edu.uiowa.csense.runtime.api;

/**
 * This is the base class that each implementation of CSense will have to
 * subclass from Provides basic functionalities for input ports
 * 
 * API v1.1: -- add previousComponent
 * 
 * 
 * API v 1.3 --- changed push to return status indicating the success of the
 * operation
 * 
 * @author farley, ochipara
 * 
 * @param <T>
 */
public interface InputPort<T extends Frame> extends Port {

    /**
     * Links to the next component
     * 
     * @param port
     * @throws CSenseException
     */
    public void link(OutputPort<? extends Frame> port) throws CSenseException;

    /**
     * Receives an incoming push from the previous component
     * 
     * @param m
     * @return returns a code indicating success of the operation.
     * @throws CSenseException
     */
    public int onPush(Frame m) throws CSenseException;

    /**
     * Clears the pending message
     */
    public void clear() throws CSenseException;

    /**
     * Determines if the input port can accept an additional message
     * 
     * @return
     */
    public boolean hasFrame();

    /**
     * Returns the message
     * 
     * @return
     */
    public T getFrame();
    
    /**
     * Request a poll from the previous component
     * 
     * @return error or success
     * @throws CSenseException
     */
    public int poll() throws CSenseException;

    /**
     * 
     * @return
     */
    public boolean getSupportsPoll();
}
