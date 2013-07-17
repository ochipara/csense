package base.concurrent;

import api.CSenseErrors;
import api.CSenseException;
import api.IComponent;
import api.concurrent.ITaskManager;

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
