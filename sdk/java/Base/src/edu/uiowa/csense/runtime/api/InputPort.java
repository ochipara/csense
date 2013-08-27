package api;

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
public interface IInPort<T extends Message> extends IPort {

    /**
     * Links to the next component
     * 
     * @param port
     * @throws CSenseException
     */
    public void link(IOutPort<T> port) throws CSenseException;

    /**
     * Receives an incoming push from the previous component
     * 
     * @param m
     * @return returns a code indicating sucess of the operation.
     * @throws CSenseException
     */
    public IResult onPush(T m) throws CSenseException;

    /**
     * Clears the pending message
     */
    public void clear() throws CSenseException;

    /**
     * Determines if the input port can accept an additional message
     * 
     * @return
     */
    public boolean hasMessage();

    /**
     * Returns the message
     * 
     * @return
     */
    public T getMessage();
    
    /**
     * Request a poll from the previous component
     * 
     * @return
     * @throws CSenseException
     */
    public T poll() throws CSenseException;

    /**
     * 
     * @return
     */
    public boolean getSupportsPoll();

    public void setMultiplier(int multiplier);

    public int geMultiplier();
}
