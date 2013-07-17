package base.v2;

import java.lang.reflect.Constructor;

import android.content.Context;
import api.CSenseException;
import api.IEventManager;
import api.IScheduler;
import api.concurrent.IIdleLock;
import api.concurrent.ITaskManager;
import base.concurrent.ABQTaskManager;
import base.concurrent.MonitorIdleLock;

public class CSenseToolkitAndroidImpl extends CSenseToolkitImpl{
    protected Context context = null;
    
    
    public void setContext(Context context) {
	this.context = context;
    }
    
    
    @Override
    public IScheduler newScheduler(String threadName) throws CSenseException {
	IIdleLock idleLock = new AndroidIdleLock(context, threadName);
	IScheduler scheduler;
	
	try {
	    // create the new task queue
	    Constructor taskQueueConstructor = taskQueueClass.getConstructor(Integer.TYPE);
	    ITaskManager pending = (ITaskManager) taskQueueConstructor.newInstance(30);
	    
	    Constructor timerConstructor = timerQueueClass.getConstructor(Integer.TYPE);
	    IEventManager events = (IEventManager) timerConstructor.newInstance(30);
	    
	    scheduler = new CSenseScheduler(threadName, idleLock, pending, events);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}
	
	schedulers.put(threadName, scheduler);
	return scheduler;
    }
}
