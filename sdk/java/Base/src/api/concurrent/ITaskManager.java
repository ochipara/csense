package api.concurrent;

import api.CSenseException;
import api.IComponent;

/**
 * Manages how the tasks in the future will be executed by the scheduler.
 * 
 * @author ochipara
 * 
 */
public interface ITaskManager {
    /**
     * 
     * @return the next pending task
     */
    public IComponent nextTask();

    /**
     * Adds a task that will be executed in the future by the scheduler.
     */
    public void scheduleTask(IComponent component) throws CSenseException;
    
    /**
     * Clears 
     */
    public void clear();
    
    /**
     * @return if there is a task pending
     */
    public boolean hasTask();
}
