package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.concurrent.IEventManager;
import edu.uiowa.csense.runtime.concurrent.CSenseBlockingQueue;

public class CBQTaskManager implements IEventManager {
    private CSenseBlockingQueue<Event> _tasks;

    public CBQTaskManager(int numTasks) {
	_tasks = new CSenseBlockingQueue<Event>(numTasks);
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
