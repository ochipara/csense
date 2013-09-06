package edu.uiowa.csense.runtime.v4;

import java.lang.reflect.Constructor;

import android.content.Context;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseToolkit;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.api.concurrent.IEventManager;
import edu.uiowa.csense.runtime.api.concurrent.ITimerEventManager;
import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;

public class CSenseAndroidToolkit extends CSenseToolkit {
    protected final Context context;
    
    public CSenseAndroidToolkit(Context context) {
	CSenseToolkit.csense = this;
	this.context = context;    
    }
    
    @Override
    public IScheduler newScheduler(String threadName) throws CSenseException {
  	IScheduler scheduler;
  	try {
  	    // create the new task queue
  	    Constructor<?> idleLockConstructor = idleLockClass.getConstructor(Context.class, String.class);
  	    IIdleLock idleLock = (IIdleLock) idleLockConstructor.newInstance(context, threadName);
  	    
  	    // create the new task queue
  	    Constructor<?> taskQueueConstructor = taskQueueClass.getConstructor(Integer.TYPE);
  	    IEventManager events = (IEventManager) taskQueueConstructor.newInstance(30);

  	    Constructor<?> timerConstructor = eventQueueClass.getConstructor(Integer.TYPE);
  	    ITimerEventManager timerEvents = (ITimerEventManager) timerConstructor.newInstance(30);

  	    //public CSenseScheduler(String threadName, IIdleLock idleLock, ITaskManager pending, IEventManager eventQueue) throws CSenseException {
  	    Constructor<?> schedulerConstructor = schedulerClass.getConstructor(String.class, IIdleLock.class, IEventManager.class, ITimerEventManager.class);
  	    scheduler = (IScheduler) schedulerConstructor.newInstance(threadName, idleLock, events, timerEvents);	
  	} catch (Exception e) {
  	    e.printStackTrace();
  	    throw new CSenseException(e);
  	}

  	schedulers.put(threadName, scheduler);
  	return scheduler;    
      }
}
