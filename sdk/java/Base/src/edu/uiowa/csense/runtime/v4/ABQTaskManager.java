package base.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import api.CSenseErrors;
import api.CSenseException;
import api.IComponent;
import api.concurrent.ITaskManager;

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
	    throw new CSenseException(CSenseErrors.ERROR, "Out of space in the ABQManger?");
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
