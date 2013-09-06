package edu.uiowa.csense.runtime.v4;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uiowa.csense.profiler.CSenseFormatter;
import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.profiler.Utility;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.api.concurrent.IEventManager;
import edu.uiowa.csense.runtime.api.concurrent.IState;
import edu.uiowa.csense.runtime.api.concurrent.ITimerEventManager;
import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.compatibility.ThreadCPUUsage;
/**
 * The default implementation of the Scheduler interface. The scheduler supports
 * to schedule timer tasks and notify components of their interested I/O events.
 * A component can request the scheduler to schedule itself to run in an
 * appropriate time without blocking scheduled timer tasks and I/O events.
 * 
 * @author Farley Lai
 */

public class CSenseScheduler implements IScheduler {
    private Thread _schedulerThread;
    private volatile boolean _active;
    private final List<IComponent> _components;
    //private final PriorityBlockingQueue<TimerEvent> _timerTasks;
    private final String _name;

    // concurrent state for the scheduler
    private final IIdleLock _idleLock;
    private final IEventManager _events;
    private final ITimerEventManager _timerTasks;

    // listeners watching for the state changes of the scheduler
    //private final List<ICommandHandler> _listners = new LinkedList<ICommandHandler>(); 

    // performance metrics
    protected long _eventCount, _timerEvents;
    protected long _startTime, _endTime;
    private   long _timerTasksTime;
    private   long _scheduledTasksTime;
    private   long _idleTime;

    // the system path which a .log file will be written to (if this variable is
    // assigned a value).
    private String _path = null;

    // the file handler for the logger
    private FileHandler _handler = null;    

    // Java API logger - helps us write data to a file
    private Logger _logger = null;

    class SchedulerThread extends Thread {
	private final String _name;
	public SchedulerThread(String name) {
	    super(name);
	    _name = name;
	}

	/**
	 * Initializes and activates all the added components before entering
	 * the event loop.
	 */
	protected void initialize() throws CSenseException {
	    // initialize the components
	    for (int i = 0; i < _components.size(); i++) {
		IComponent component = _components.get(i);		
		component.onCreate();				
		component.getState().assertState(IState.STATE_CREATED);
	    }

	    // start the components
	    for (int i = 0; i < _components.size(); i++) {
		IComponent component = _components.get(i);
		component.onStart();
		component.transitionTo(IState.STATE_READY);
	    }
	}

	/**
	 * Deactivates and deinitializes all the components before exiting the
	 * event loop.
	 * 
	 * @throws CSenseException
	 */
	public void deinitialize() throws CSenseException {
	    for (int i = 0; i < _components.size(); i++) {
		IComponent component = _components.get(i);  
		component.onStop();
		component.getState().assertState(IState.STATE_STOPPED);
	    }
	}

	@Override
	public void run() {
	    Debug.logThreadStart();
	    try {
		initialize();		
		eventLoop();
		deinitialize();		
	    } catch (CSenseException e) {
		handleException(e);
	    }
	    Debug.logThreadStop();
	}

	public long doTimerTasks() throws CSenseException {
	    long scheduledExecutionTime = 0;
	    long now = 0;
	    long diff = 0;	    

	    while (!_timerTasks.isEmpty()) {
		if (_active == false) return 0;
		scheduledExecutionTime = _timerTasks.peek().scheduledExecutionTime();
		now = System.nanoTime();
		diff = scheduledExecutionTime - now;
		if (diff <= 0) {
		    TimerEvent task = _timerTasks.poll();
		    task.setScheduled(false);
		    Debug.logScheduledTimerEventExec(task.getOwner());
		    task.getOwner().onEvent(task);
		    Debug.logScheduledTimerEventReturn(task.getOwner());		    
		    _timerEvents += 1;
		    _timerTasksTime += System.nanoTime() - now;
		} else {
		    // Log.i(getName(), "remaining " + diff / 1000000000);
		    break;
		}
	    }

	    long timeout = _timerTasks.isEmpty() ? 0 : diff;
	    if (timeout < 0)
		throw new CSenseRuntimeException(
			Utility.toString(
				"timeout < 0: %d timer tasks queued, scheduled execution time %d, now %d, diff %d",
				scheduledExecutionTime, now, diff));

	    return timeout;
	}

	/**
	 * Executes scheduled timer tasks and notifies components of their
	 * interested I/O events to process in a loop until the scheduler is
	 * stopped.
	 */
	protected void eventLoop() {
	    _startTime = System.nanoTime();
	    _eventCount = _timerEvents = 0;
	    ThreadCPUUsage usage = ThreadCPUUsage.getCPUUsage();
	    long threadTime = usage.getThreadTime();
	    long utime = usage.getThreadUserTime();
	    long stime = usage.getThreadSystemTime();			
	    try {
		while (_active) {		    
		    long time = System.nanoTime();
		    
		    // execute the pending components
		    Event pending = _events.nextEvent();		    
		    while (_active && pending != null) {
			IComponent owner = pending.getOwner();
			_eventCount = _eventCount + 1;
			Debug.logScheduledTaskExec(owner);
			owner.onEvent(pending);
			Debug.logScheduledTaskReturn(owner);
			pending = _events.nextEvent();
		    }
		    		    
		    _scheduledTasksTime += System.nanoTime() - time;
		    if (_active) {
			long timeout = doTimerTasks();

			/**
			 * A few things to note here:
			 * 
			 * - the scheduler should not go to sleep unless
			 * 	(1) the scheduler cannot go to sleep unless the timeout exceeds 0. if the timeout is zero,
			 *  	then it is impossible for the scheduler to know when to wake up. in the case when there are only
			 *  	time events, this case will lead to the scheduler sleeping forever. it is never a good idea to have an
			 *  	empty _timerTasks queue.
			 *  
			 *  	(2) it is safe to go to sleep if both the _timerTasks and the _pending are empty. this means that the 
			 *  	component will be waiting for an external event to wake it up.
			 * 
			 */
			if ((timeout > 0 && _events.hasEvent() == false) || (_events.hasEvent() == false && _timerTasks.isEmpty())) {			   
			    time = System.nanoTime();
			    Debug.logThreadSleep();
			    _idleLock.sleep(timeout);
			    Debug.logThreadWakeup();
			    _idleTime += System.nanoTime() - time;
			}
		    }
		}
	    } catch (InterruptedException e) {
	    } catch (CSenseException e) {				
		if(!(e.getCause() instanceof InterruptedException) || (e.error() == CSenseError.INTERRUPTED_OPERATION)) {
		    e.printStackTrace();
		    Log.e("quit,", e);
		}
	    }
	    
	    if(Thread.interrupted()) Log.i(_name, "interrupted to stop");
	    
	    // consume the timer events and set them to be processed
	    while (true) {
		TimerEvent event = _timerTasks.poll();
		if (event != null) {
		    event.setScheduled(false);
		} else break;		
	    }
	    _events.clear();
	    _endTime = System.nanoTime();
	    Log.i(getName(), "=============== scheduler statistics report ===============");
	    double durationInNs = _endTime - _startTime;
	    double durationInSecs = durationInNs / 1000000000.0;
	    double rate = (_eventCount + _timerEvents) / durationInSecs;
	    usage = ThreadCPUUsage.getCPUUsage();
	    threadTime = usage.getThreadTime() - threadTime;
	    utime = usage.getThreadUserTime() - utime;
	    stime = usage.getThreadSystemTime() - stime;
	    
	    Log.i(getName(), String.format("%.2f loops/s, %d/%d events  in %.2fs", rate, _eventCount, _timerEvents, durationInSecs));
	    Log.i(getName(), String.format("timer tasks time: %.2fms, %.2f%%", _timerTasksTime / 1000000.0, _timerTasksTime / durationInNs * 100)); 
	    Log.i(getName(), String.format("scheduled tasks time: %.2fms, %.2f%%", _scheduledTasksTime / 1000000.0, _scheduledTasksTime / durationInNs * 100));
	    Log.i(getName(), String.format("idle time: %.2fms, %.2f%%", _idleTime / 1000000.0, _idleTime / durationInNs * 100));
	    Log.i(getName(), String.format("total thread time: %dns", threadTime));
	    Log.i(getName(), String.format("total user time: %dus", utime));
	    Log.i(getName(), String.format("total system time: %dus", stime));
	}
    }

    public CSenseScheduler(String threadName, 
	    IIdleLock idleLock, 
	    IEventManager events,
	    ITimerEventManager timerTasks) throws CSenseException {
	_components = new ArrayList<IComponent>();
	_timerTasks = timerTasks;
	_name = threadName;
	_events = events;
	_idleLock = idleLock;
	setupLogger();
    }

    @Override
    public void setLogPath(String path) {
	_path = path;
	setupLogger();
    }

    private void setupLogger() {
	_logger = Logger.getLogger(CSenseScheduler.class.getName());
	_logger.setLevel(Level.ALL);

	if (null != _path) {
	    try {
		if (null != _handler) {
		    _handler.close();
		}
		_handler = new FileHandler(String.format("%s/profiler.log", _path));
		_handler.setFormatter(new CSenseFormatter());
	    } catch (SecurityException e) {
		e.printStackTrace();

	    } catch (IOException e) {
		e.printStackTrace();

	    }
	    _logger.addHandler(_handler);
	}
    }

    @Override
    public boolean isActive() {
	return _active;
    }

    @Override
    public boolean addComponent(IComponent component) {
	if (_active) {
	    throw new RuntimeException("Cannot add components while the domain scheduler is running.");
	}

	component.setScheduler(this);
	_components.add(component);
	return true;
    }

    @Override
    public boolean start() {
	_active = true;	
	_idleLock.start();
	if (_schedulerThread == null)
	    _schedulerThread = new SchedulerThread(_name);

	if (!_schedulerThread.isAlive()) {
	    // this must be the last thing done to prevent race condition
	    _schedulerThread.start();
	    return true;
	}

//	for (int i = 0; i < _listners.size(); i++) {
//	    ICommandHandler handler = _listners.get(i);
//	    handler.command(new Command(null, "scheduler::start"));
//	}
	return false;
    }

    @Override
    public void stop() {
	_active = false;
	if (!_schedulerThread.isAlive()) {	    
	    _schedulerThread = null;
	} else {
	    // _idleLock.wakeup(); ==> no need, the interrupted exception will wake up the thread	    
	    if (Thread.currentThread() != _schedulerThread) {
		try {
		    // FIXME Do we really need to interrupt the scheduler thread to stop it?
		    _schedulerThread.interrupt();
		    _schedulerThread.join();
		    _schedulerThread = null;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    _idleLock.stop();
	}
    }

    @Override
    public boolean schedule(IComponent owner, Event task) {
	try {
	    task.setOwner(owner);
	    _events.scheduleEvent(task);
	    owner.getState().setHasEvent(true);
	    _idleLock.wakeup();
	} catch (CSenseException e) {
	    e.printStackTrace();
	}
	return true;
    }


    @Override
    public void join() {
	if (Thread.currentThread() == _schedulerThread)
	    throw new CSenseRuntimeException("the scheduler cannot join itself");
	if (_schedulerThread != null) {
	    try {
		_schedulerThread.join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }


    @Override
    public boolean schedule(IComponent owner, TimerEvent task, long delay) {
	task.setOwner(owner);
	task.scheduledExecutionTime(System.nanoTime() + delay * 1000000);

	if (task.isScheduled()) {
	    throw new CSenseRuntimeException();
	}

	boolean ret = _timerTasks.add(task);
	task.setScheduled(true);
	_idleLock.wakeup();
	return ret;
    }

    @Override
    public boolean schedule(IComponent owner, TimerEvent task, long delay, TimeUnit unit) {
	task.setOwner(owner);
	if (unit == TimeUnit.NANOSECONDS) {
	} else if (unit == TimeUnit.MICROSECONDS) {
	    delay = delay * 1000;
	} else if (unit == TimeUnit.MILLISECONDS) {
	    delay = delay * 1000 * 1000;
	} else if (unit  == TimeUnit.SECONDS) {
	    delay = delay * 1000 * 1000 * 1000;
	} else throw new IllegalArgumentException("Unsupported conversion");

	task.scheduledExecutionTime(System.nanoTime() + delay);
	if (task.isScheduled()) {
	    throw new CSenseRuntimeException(CSenseError.TASK_ALREADY_SCHEDULED);
	}


	boolean ret = _timerTasks.add(task);
	task.setScheduled(true);
	_idleLock.wakeup();
	return ret;
    }

    @Override
    public boolean scheduleAt(IComponent owner, TimerEvent task, long absoluteTime) {
	task.scheduledExecutionTime(absoluteTime);
	task.setOwner(owner);
	if (task.isScheduled()) {
	    throw new CSenseRuntimeException();
	}

	boolean ret = _timerTasks.add(task);
	task.setScheduled(true);
	_idleLock.wakeup();
	return ret;
    }

    @Override
    public final boolean cancel(Event task) {
	throw new UnsupportedOperationException();
    }

    @Override
    public final boolean cancel(TimerEvent task) {
	return _timerTasks.remove(task);
    }

    @Override
    public void handleException(CSenseException e) {
	Log.e(_name, "unable to handle exceptions, quit");
	e.printStackTrace();
	stop();
    }

    @Override
    public SelectionKey registerChannel(SelectableChannel channel, int ops,
	    CSenseComponent owner) {
	throw new UnsupportedOperationException();
    }

    @Override
    public long getTotalRunningTime() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public Thread getThread() {
	return _schedulerThread;
    }

//    @Override
//    public int registerStateListener(ICommandHandler handler) {
//	_listners.add(handler);
//	return 0;
//    }

    @Override
    public IIdleLock getIdleLock() {
	return _idleLock;
    }
}
