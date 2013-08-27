package edu.uiowa.csense.runtime.api.concurrent;

/**
 * This API controls when the scheduler thread goes to sleep and wakes up. The
 * implementation of this API must be reentrant.
 * 
 * @author ochipara
 * 
 */
public interface IIdleLock {
    /**
     * Puts the scheduler thread to sleep
     */
    public void sleep() throws InterruptedException;
    public void sleep(long nano) throws InterruptedException;

    /**
     * Wakes up the scheduler thread form sleep
     */
    public void wakeup();
    
    /**
     * Called when the scheduler is stopped.
     * This is used when we are also doing power management on android.
     */
    public void start();
    public void stop();
}
