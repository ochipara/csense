package edu.uiowa.csense.runtime.api;


/**
 * This is the abstract class from which the OutPort implementation should
 * subclass This class provides basic functionality for implementing input ports
 * 
 * API v 1.1 --- added nextComponent
 * 
 * API v 1.3 --- changed push to return status indicating the success of the
 * operation
 * 
 * @author ochipara
 * 
 * @param <T>
 */

public interface OutputPort<T extends Frame> extends Port {

    /**
     * Links to the next component
     * 
     * @param port
     * @throws CSenseException
     */
    public void link(InputPort<? extends Frame> port) throws CSenseException;

    /**
     * 
     * @return the next component
     */
    public IComponent nextComponent();

    /**
     * Pushes message m to the next component. 
     * Failures will results in exceptions being thrown
     * 
     * @param m
     * @throws CSenseException
     */
    public int push(T m) throws CSenseException;

    /**
     * Called when a poll requested by the next component
     * 
     * @return data
     * @throws CSenseException
     *             when the request cannot be fulfilled
     */
    public T onPoll() throws CSenseException;

    /**
     * 
     * @return true if this component can support polling
     */
    public boolean getSupportsPoll();

    public void setSupportPull(boolean b);

    /**
     * return true if the port is connected
     * 
     * @return
     */
    public boolean isConnected();

    //public void setMultiplier(int multiplier);
}
