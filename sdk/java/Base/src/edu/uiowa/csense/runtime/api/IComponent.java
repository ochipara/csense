package edu.uiowa.csense.runtime.api;

import java.nio.channels.SelectionKey;

import edu.uiowa.csense.runtime.api.concurrent.IState;

/**
 * API v 1.1 -- added STATE_READY -- now you call directly the logging commands
 * from your component and getName()
 * 
 * API v 1.3 -- changed the push to return error. the throwing of exceptions
 * led to code that was difficult to write and understand.
 * 
 * API v 2.0 -- new version after RTSS paper submission.
 * 
 * 
 * 
 * @author ochipara
 * 
 */
public interface IComponent {
    public final static int PUSH_SUCCESS = 0;
    public final static int PUSH_FAIL = 1;
    public static final int PUSH_DROP = 2;

    // manages the identity of the component
    public int getId();
    public void setId(int id);
    public String getName();
    public void setName(String name);

    // accessing ports
    public <T extends Frame> OutputPort<T> newOutputPort(IComponent owner, String name) throws CSenseException;
    public <T extends Frame> InputPort<T> newInputPort(IComponent owner, String name) throws CSenseException;    
    public InputPort<? extends Frame> getInputPort(String name) throws CSenseException;
    public OutputPort<? extends Frame> getOutputPort(String name) throws CSenseException;

    // links components together
    public <T extends Frame> void link(InputPort<T> in, OutputPort<T> out) throws CSenseException;

    // access the scheduler
    public void setScheduler(IScheduler cSenseScheduler);
    public IScheduler getScheduler();

    // state management for the component    
    public void transitionTo(int newState) throws CSenseException;
    public boolean transition(int expectedState, int newState);
    public IState getState();
    
    // component life-cycle events
    public void onCreate() throws CSenseException;
    public void onStart() throws CSenseException;
    public void onStop() throws CSenseException;
    
    // this method is called after each push
    /**
     * Called to receive a  command.
     * It will check the preconditions to make sure that the component is okay.
     * If all the conditions check out, it will invoke onInput()
     * otherwise, it will return an error
     * 
     * @param self
     * @param frame
     * @return
     * @throws CSenseException
     */
    public <T extends Frame> int onPush(InputPort<T> self, Frame frame) throws CSenseException;
    public void onInput() throws CSenseException;
    
    
    /**
     * Called when the component receives a poll request
     * WARNING: do not call push from onPoll!!!
     * 
     * @param the output port on which the poll was requested
     * @return the data item requested. May be null.
     * @throws CSenseException
     *             when the request cannot be fulfilled
     */
    public Frame onPoll(OutputPort<? extends Frame> port) throws CSenseException;
    
    // this method is invoked when a channel has data to process
    public void processInput(SelectionKey key);
   
    /**
     * Called with a scheduled event
     * @param t
     * @throws CSenseException
     */
    public void doEvent(Task t) throws CSenseException;
    public Task asTask();
    
    
    public void setMultiplier(int m);
    
    // logging options
    public void error(Object... args);
    public void warn(Object... args);
    public void info(Object... args);
    public void debug(Object... args);
    public void verbose(Object... args);
}