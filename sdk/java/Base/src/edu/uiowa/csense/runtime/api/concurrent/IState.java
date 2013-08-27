package api.concurrent;

import api.CSenseException;

/**
 * This API is used by the scheduler to manage its concurrency. The components
 * that implement this API must ensure be thread safe. This will be instantiated
 * as part of CSenseComponets
 * 
 * @author ochipara
 */
public interface IComponentState {

    /**
     * Updates the state of the component to the new state.
     * 
     * @param state
     * 
     */
    public void transitionTo(int newState) throws CSenseException;

    public boolean transitionTo(int expectedState, int newState);

    /**
     * 
     * @return the current state
     */
    public int getState();

    /**
     * Asserts that the component is in a specific state. If the state does not
     * match, a CSenseException with error code ILLEGAL_TRANSITION will be
     * thrown
     * 
     * @param state
     * @throws CSenseException
     */
    public void assertState(int state) throws CSenseException;

    /**
     * Asserts that the scheduler is in either state1 or state2.
     * 
     * @param state1
     * @param state2
     * @throws CSenseException
     */
    public void assertState(int state1, int state2) throws CSenseException;

    public void setHasInput(boolean state) throws CSenseException;

    public boolean hasInput();

    public void setHasEvent(boolean state) throws CSenseException;

    public boolean hasEvent();

    @Override
    public String toString();
}
