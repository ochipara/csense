package api;

import java.nio.channels.SelectionKey;

import api.concurrent.IComponentState;

/**
 * API v 1.1 -- added STATE_READY -- now you call directly the logging commands
 * from your component and getName()
 * 
 * API v 1.3 -- changed the push to return error. the throwing of exceptions
 * leed to code that was difficult to write and understand.
 * 
 * @author ochipara
 * 
 */
public interface IComponent extends ICommandHandler{
    public final static int STATE_INIT = 0;
    public final static int STATE_CREATED = 1;
    public final static int STATE_READY = 2;
    public final static int STATE_RUNNING = 3;
    public final static int STATE_STOPPED = 4;

    public final static int PUSH_SUCCESS = 0;
    public final static int PUSH_FAIL = 1;
    public static final int PUSH_DROP = 2;

    // getter/setter for id assignment by CSense
    int getId();
    void setId(int id);
    
    // get the name of the component
    public String getName();

    public void setName(String name);

    // accessing ports
    public <T extends Message> IOutPort<T> newOutputPort(CSenseComponent owner, String name) throws CSenseException;
    public <T extends Message> IInPort<T> newInputPort(CSenseComponent owner, String name) throws CSenseException;

    public IInPort<? extends Message> getInputPort(String name)
	    throws CSenseException;

    public IOutPort<? extends Message> getOutputPort(String name)
	    throws CSenseException;

    // links components together
    public <T extends Message> void link(IInPort<T> in, IOutPort<T> out)
	    throws CSenseException;

    // access the scheduler
    public void setScheduler(IScheduler cSenseScheduler);
    public IScheduler getScheduler();

    // component life-cycle events
    public void onCreate() throws CSenseException;
    public void onStart() throws CSenseException;
    public void onStop() throws CSenseException;

    // state management for the component
    public void transitionTo(int newState) throws CSenseException;
    public boolean transition(int expectedState, int newState);
    public IComponentState getState();

    // the following methods are necessary for handling the push
    public <T extends Message> IResult processInput(IInPort<T> input, T m) throws CSenseException;

    // this method is invoked when a channel has data to process
    public void processInput(SelectionKey key);

    /**
     * doInput will be called from within the scheduler's thread the only
     * component that takes exceptions from this rule are the queue components.
     * @return 
     * 
     * @throws CSenseException
     */
    public void doInput() throws CSenseException; // TODO: rename to onInput
    public IResult onError(IResult result) throws CSenseException;
    
   
    /**
     * Callend with a scheduled event
     * @param t
     * @throws CSenseException
     */
    public void doEvent(Task t) throws CSenseException;
    public Task asTask();
    
    public void accumulateResult(IResult result);
    public IResult getResult();

    
    /**
     * Called when the component receives a poll request
     * WARNING: do not call push from onPoll!!!
     * 
     * @param the output port on which the poll was requested
     * @return the data item requrested. May be null.
     * @throws CSenseException
     *             when the request cannot be fulfilled
     */
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException;

    // logging options
    public void error(Object... args);

    public void warn(Object... args);

    public void info(Object... args);

    public void debug(Object... args);

    public void verbose(Object... args);
}