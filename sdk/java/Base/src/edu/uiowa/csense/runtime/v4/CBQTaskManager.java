package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.concurrent.ITaskManager;
import edu.uiowa.csense.runtime.concurrent.CSenseBlockingQueue;

public class CBQTaskManager implements ITaskManager {
    private CSenseBlockingQueue<IComponent> _tasks;

    public CBQTaskManager(int numTasks) {
	_tasks = new CSenseBlockingQueue<IComponent>(numTasks);
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
