package edu.uiowa.csense.runtime.v4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.concurrent.IEventManager;

public class ABQTaskManager implements IEventManager {
    private BlockingQueue<Event> _tasks;

    public ABQTaskManager(int numTasks) {
	_tasks = new ArrayBlockingQueue<Event>(numTasks);
    }

    @Override
    public Event nextEvent() {
	return _tasks.poll();
    }

    @Override
    public void scheduleEvent(Event component) throws CSenseException {
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
    public boolean hasEvent() {
	return _tasks.size() > 0;
    }
}
