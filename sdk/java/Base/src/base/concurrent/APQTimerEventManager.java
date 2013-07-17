package base.concurrent;

import java.util.concurrent.PriorityBlockingQueue;

import api.IEventManager;
import api.TimerEvent;

public class APQTimerEventManager implements IEventManager {
    private final PriorityBlockingQueue<TimerEvent> _timerTasks;

    public APQTimerEventManager(int capacity) {
	_timerTasks = new PriorityBlockingQueue<TimerEvent>(capacity);
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
	return _timerTasks.add(task);
    }

    @Override
    public boolean remove(TimerEvent task) {
	return _timerTasks.remove(task);
    }    
}
