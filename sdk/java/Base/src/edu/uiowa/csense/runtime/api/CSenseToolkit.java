package edu.uiowa.csense.runtime.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import edu.uiowa.csense.runtime.api.concurrent.IEventManager;
import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;
import edu.uiowa.csense.runtime.api.concurrent.ITaskManager;
import edu.uiowa.csense.runtime.types.TypeInfo;

/**
 * This is the interface to the generic factory that each csense implementation
 * will have to provide. An implementation must provide the following functionality:
 * 
 * - a component 
 * - a source 
 * - a message pool 
 * - a scheduler that includes
 * 	- a wakelock
 * 	- a timer queue
 * 	- a task queue
 * 
 *  Note that each of these classes are assumed to have specific constructors for the 
 *  class to be instatiated via reflection.
 * 
 * @author ochipara
 * 
 */
public class CSenseToolkit {
    public static Map<String, IComponent> components = new HashMap<String, IComponent>();
    public static Map<String, IScheduler> schedulers = new HashMap<String, IScheduler>();
    protected static CSenseToolkit csense = null;
    protected static int id = 0;

    protected Class<?> componentClass = null;
    protected Class<?> sourceClass = null;
    protected Class<?> messagePoolClasss = null;    
    protected Class<?> schedulerClass = null;    
    protected Class<?> taskQueueClass = null;
    protected Class<?> eventQueueClass = null;
    protected Class<?> idleLockClass = null;

    public IComponent newComponent() throws CSenseException {
	Constructor<?> c;
	try {
	    c = componentClass.getConstructor();
	    IComponent component = (IComponent) c.newInstance();
	    return component;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(CSenseError.FACTORY_ERROR, e.getMessage());
	} 	
    }
    
    
    public <T extends Frame> ISource newSource(TypeInfo<T> typeInfo) throws CSenseException {
	Constructor<?> c;
	try {
	    c = sourceClass.getConstructor();
	    ISource component = (ISource) c.newInstance();
	    return component;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(CSenseError.FACTORY_ERROR, e.getMessage());
	} 
    }

    public <T extends Frame> FramePool newFramePool(TypeInfo<T> type, int capacity) throws CSenseException {
	try {
	    Constructor<?> constructor = messagePoolClasss.getConstructor(TypeInfo.class, Integer.TYPE);
	    FramePool pool = (FramePool) constructor.newInstance(type, capacity);
	    return pool;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}	
    }
        
    public IScheduler newScheduler(String threadName) throws CSenseException {
	IScheduler scheduler;
	try {
	    // create the new task queue
	    Constructor<?> idleLockConstructor = idleLockClass.getConstructor();
	    IIdleLock idleLock = (IIdleLock) idleLockConstructor.newInstance();
	    
	    // create the new task queue
	    Constructor<?> taskQueueConstructor = taskQueueClass.getConstructor(Integer.TYPE);
	    ITaskManager pending = (ITaskManager) taskQueueConstructor.newInstance(30);

	    Constructor<?> timerConstructor = eventQueueClass.getConstructor(Integer.TYPE);
	    IEventManager events = (IEventManager) timerConstructor.newInstance(30);

	    //public CSenseScheduler(String threadName, IIdleLock idleLock, ITaskManager pending, IEventManager eventQueue) throws CSenseException {
	    Constructor<?> schedulerConstructor = schedulerClass.getConstructor(String.class, idleLockClass, taskQueueClass, eventQueueClass);
	    scheduler = (IScheduler) schedulerConstructor.newInstance(threadName, idleLock, pending, events);	
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}

	schedulers.put(threadName, scheduler);
	return scheduler;    
    }
    
    
    /**
     * 
     */
    public CSenseToolkit() {
	csense  = this;
    }

    public static IComponent getComponent(String name) {
	return components.get(name);
    }

    public static CSenseToolkit getImplementation() {
	return csense;
    }

    public static void registerComponent(IComponent component) {
	components.put(component.getName(), component);	
	component.setId(id++);
    }

    public IScheduler getScheduler(String threadName) {
	return schedulers.get(threadName);
    }
    

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }


    public void setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }


    public void setMessagePoolClass(Class<?> messagePoolClasss) {
        this.messagePoolClasss = messagePoolClasss;
    }


    public void setSchedulerClass(Class<?> schedulerClass) {
        this.schedulerClass = schedulerClass;
    }


    public void setTaskQueueClass(Class<?> taskQueueClass) {
        this.taskQueueClass = taskQueueClass;
    }


    public void setEventQueueClass(Class<?> timerQueueClass) {
        this.eventQueueClass = timerQueueClass;
    }


    public void setIdleLockClass(Class<?> idleLockClass) {
        this.idleLockClass = idleLockClass;
    }
}
