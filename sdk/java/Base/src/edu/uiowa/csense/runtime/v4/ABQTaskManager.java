package edu.uiowa.csense.runtime.v4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.concurrent.ITaskManager;

public class ABQTaskManager implements ITaskManager {
    private BlockingQueue<IComponent> _tasks;

    public ABQTaskManager(int numTasks) {
	_tasks = new ArrayBlockingQueue<IComponent>(numTasks);
    }

    @Override
    public IComponent nextTask() {
	return _tasks.poll();
    }

    @Override
    public void scheduleTask(IComponent component) throws CSenseException {
	if (_tasks.contains(component))
	    return;

	if (_tasks.offer(component) == false) {
	    throw new CSenseException(CSenseError.ERROR, "Out of space in the ABQManger?");
	}

    }

    @Override
    public void clear() {
	_tasks.clear();
    }

    @Override
    public boolean hasTask() {
	return _tasks.size() > 0;
    }
}
