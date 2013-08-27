package api;

/**
 * This is the base class for ports. Holds information regarding - the component
 * to which this port is attached (_owner) - the name of the port (_name) - the
 * ids of the port(_id)
 * 
 * @author farley, ochipara
 * 
 */
public interface IPort {
    /**
     * 
     * @return the associated component with this port
     */
    public IComponent getOwner();

    /**
     * 
     * @return the name of the port
     */
    public String getName();

    /**
     * 
     * @return true if it is input, false otherwise
     */
    public boolean isInput();

    /**
     * 
     * @return true if it output, false otherwise
     */
    public boolean isOutput();
}
