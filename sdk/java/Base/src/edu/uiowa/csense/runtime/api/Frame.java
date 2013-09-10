package edu.uiowa.csense.runtime.api;


import edu.uiowa.csense.runtime.api.profile.IRoute;

/**
 * The IFrame is the interface for the basic types exchanged in CSense
 * It provides mechanisms for memory management
 * 
 * @author ochipara
 *
 */
public interface Frame {
    public void drop();
    
    /**
     * calls necessary for figuring out the end of a stream
     */
    public void eof();
    public boolean isEof();
    
    /**
     * increments the reference to the message
     */
    public void incrementReference();
    public void incrementReference(int count);

    /**
     * - decrements the references to message 
     * - will automatically call free on the message when it is done
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
    
    
    // view management
    public Frame[] window(int splits);    
    public Frame[] window(int splits, int increment);
    public Frame slice(int start, int end);
    public Frame getParent();
        
    

    // view management    
   //public List<Frame<T>> split(CSenseComponent component, int numFrames) throws CSenseException; 
   // public Frame<T> getParent();    
}
