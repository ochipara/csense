package api;

import java.util.List;

public interface IMessage {
    /**
     * increments the reference to the message
     */
    public void incrementReference();
    public void incrementReference(int count);

    /**
     * - decrements the references to message - will automatically call free on
     * the message when it is done
     */
    public void decrementReference();

    /**
     * 
     * @return the number of references
     */
    public int getReference();

    /**
     * frees the message when the number of references is zero
     */
    public void free();
    public void drop();

    /**
     * Called to initialize the message both upon creation and reuse
     */
    public void initialize();
    public IRoute getRoute();

    
    /**
     * The ids are used to simplify the implementation of pools. 
     * The ids are guaranteed not to change once they are setup.
     * @throws CSenseException 
     */
    public void setPoolId(int id) throws CSenseException;
    public int getPoolId();
    public int getId();
    
    /**
     * calls necessary for figuring out the end of a stream
     */
    public void eof();
    public boolean isEof();

    // view management    
    public List<Message> split(CSenseComponent component, int numFrames) throws CSenseException; 
}
