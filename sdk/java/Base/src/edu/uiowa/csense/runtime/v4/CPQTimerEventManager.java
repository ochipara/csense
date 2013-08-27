package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.api.concurrent.IEventManager;
import edu.uiowa.csense.runtime.concurrent.CSensePriorityQueue;

public class CPQTimerEventManager implements IEventManager {
    private final CSensePriorityQueue<TimerEvent> _timerTasks;

    public CPQTimerEventManager(int capacity) {
	_timerTasks = new CSensePriorityQueue<TimerEvent>(capacity);
    }
    
    @Override
    public boolean isEmpty() {
	return _timerTasks.isEmpty();
    }

    @Override
    public TimerEvent peek() {
	return _timerTasks.peek();
    }

    @Override
    public TimerEvent poll() {
	return _timerTasks.poll();
    }

    @Override
    public void clear() {
	_timerTasks.clear();	
    }

    @Override
    public boolean add(TimerEvent task) {
	_timerTasks.offer(task);
	return true;
    }

    @Override
    public boolean remove(TimerEvent task) {
	return _timerTasks.remove(task);
    }    
}
