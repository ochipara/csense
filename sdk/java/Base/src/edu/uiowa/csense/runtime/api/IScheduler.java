package edu.uiowa.csense.runtime.api;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;

import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

/**
 * No need to consider if a component needs CPU cycles or not, just call
 * Scheduler.addComponent() to add the component. Every component can register
 * its I/O channels for the scheduler to select to run. A component can be
 * scheduled to run in the next round without loss of other scheduled timer
 * tasks or registered I/O events. A component can have different timer tasks
 * and each timer task can be scheduled with different delay. A timer task can
 * be cancelled by the scheduler for sure. Currently, EgoScheduler is the only
 * implementation.
 * 
 * @author Austin Laugesen, Farley Lai
 */

public interface IScheduler {
    /**
     * Returns the status of the scheduler.
     * 
     * @return true is the scheduler is running, false otherwise
     */
    public boolean isActive();

    /**
     * Starts the scheduler which will initialize, activate all the added
     * components and run in an event loop.
     * 
     * @return true on success, false on failure because the previous scheduler
     *         thread has not exited yet.
     */
    public boolean start();

    /**
     * Causes the scheduler to exit the event loop, deactivate and deinitialize
     * all the added components.
     */
    public void stop();

    /**
     * Waits until the scheduler stops.
     */
    public void join();

    /**
     * Adds a EgoComponent to an inactive scheduler whether it is executed by
     * the scheduler or in another thread. In any case, a component has to be
     * initialized and activated by the scheduler.
     * 
     * @param component the EgoComponent to add
     * @return true on success, false on failure
     */
    public boolean addComponent(IComponent component);

    /**
     * Registers a nio selectable channel with the scheduler so that the
     * component can be notified to process its interested I/O events.
     * 
     * @param channel
     *            the SelectableChannel to register
     * @param ops
     *            the interested operations such as ACCEPT, CONNECT, READ and
     *            WRITE
     * @param cmpt
     *            the component that owns the channel
     * @return a key that associates the channel with the interested operations.
     */
    public SelectionKey registerChannel(SelectableChannel channel, int ops,
	    CSenseComponent owner);

    /**
     * Schedules a task to run sometime later.
     * 
     * @param owner
     *            a component which owns the task.
     * @return true on success, false on failure.
     */
    public boolean schedule(IComponent owner, Task _task);

    /**
     * Cancels a previously scheduled task. If a task is scheduled several
     * times, all the schedules will be cancelled.
     * 
     * @param task
     *            the task to cancel
     * @return true on success, false on failure
     */
    public boolean cancel(Task task);

   /**
    * Schedules a component to run after delay milliseconds
    * The schedule method using TimeUnit as an argument will be preferred in future reture releases
    * @deprecated
    * @param component
    * @param task
    * @param delay
    * @return
    */
    @Deprecated
    public boolean schedule(IComponent component, TimerEvent task, long delay);
    
    /**
     * Schedules a component to run after delay. The time unit is specified as TimeUnit
     * @param component
     * @param task
     * @param delay
     * @param unit
     * @return
     */
    public boolean schedule(IComponent component, TimerEvent task, long delay, TimeUnit unit);
    
    
    /**
     * Schedules a component to run at the nanotime specified by the delay
     * @param component
     * @param task
     * @param delay
     * @return
     */
    public boolean scheduleAt(IComponent component, TimerEvent task, long delay);


    /**
     * Cancels a previously scheduled timer task. If a timer task is scheduled
     * with different delays, all the schedules will be cancelled.
     * 
     * @param task the timer task to cancel
     * @return true on success, false on failure
     */
    public boolean cancel(TimerEvent task);

    public void handleException(CSenseException e);

    public long getTotalRunningTime();

    /**
     * Setter for instance variable.
     * 
     * @param path
     *            - Must be a system path which a .log file will be written to.
     */
    public void setLogPath(String path);

    /**
     * Returns the scheduler's thread
     * 
     * @return
     */
    public Thread getThread();
    
    
    public int registerStateListener(ICommandHandler handler);

    public IIdleLock getIdleLock();
}
