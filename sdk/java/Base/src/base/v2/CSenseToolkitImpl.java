package base.v2;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import base.concurrent.MonitorIdleLock;
import messages.TypeInfo;
import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.CSenseRuntimeException;
import api.CSenseSource;
import api.CSenseToolkit;
import api.IComponentInternal;
import api.IEventManager;
import api.IMessage;
import api.IMessagePool;
import api.IScheduler;
import api.ISource;
import api.Message;
import api.concurrent.IIdleLock;
import api.concurrent.ITaskManager;

public class CSenseToolkitImpl implements CSenseToolkit {
    protected HashMap<String, IScheduler> schedulers = new HashMap<String, IScheduler>();
    protected Class<?> messagePoolClasss = null;
    protected Class<?> taskQueueClass = null;
    protected Class<?> timerQueueClass = null;

    @Override
    public IComponentInternal newComponent() {
	//	IComponentState stateManager = new SynchronizedStateManager();
	//IComponentInternal c = new CSenseComponent(false, stateManager);
	IComponentInternal c = new CSenseComponent();
	return c;
    }

    @Override
    public IComponentInternal newThreadSafeComponent() {
	throw new CSenseRuntimeException("anyone using me?");
	//	
	//	IComponentState stateManager = new SynchronizedStateManager();
	//	IComponentInternal c = new CSenseComponent(true, stateManager);
	//	return c;
    }

    @Override
    public IScheduler getScheduler(String schedulerId) {
	return schedulers.get(schedulerId);
    }

    @Override
    public <T extends Message> ISource<T> newSource(TypeInfo<T> typeInfo) throws CSenseException {
	///IComponentState stateManager = new SynchronizedStateManager();
	ISource<T> source = new CSenseSource(typeInfo);
	return source;
    }

    @Override
    public IScheduler newScheduler(String threadName) throws CSenseException {
	IIdleLock idleLock = new MonitorIdleLock();
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

    @Override
    public void setApi(String api) throws CSenseException {
	if ("v2".equals(api) == false)
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "Incorrect version");
    }

    @Override
    public <T extends Message> IMessagePool<T> newMessagePool(TypeInfo<T> type, int capacity) throws CSenseException {
	try {
	    Constructor constructor = messagePoolClasss.getConstructor(TypeInfo.class, Integer.TYPE);
	    IMessagePool<T> pool = (IMessagePool<T>) constructor.newInstance(type, capacity);
	    return pool;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}	
    }

    @Override
    public IMessage newMessage(IMessagePool<? extends Message> pool, TypeInfo<? extends Message> type) throws CSenseException {
	IMessage m = new Message(pool, type);
	return m;
    }

    @Override
    public void setMemoryPool(Class memoryPool) throws CSenseException {
	this.messagePoolClasss = memoryPool;
    }

    @Override
    public void setTaskQueue(Class taskQueueClass) {
	this.taskQueueClass = taskQueueClass;
    }
    
    @Override
    public void setTimerQueue(Class timerQueueClass) {
	this.timerQueueClass = timerQueueClass;
    }
}
