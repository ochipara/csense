package api;

import java.util.HashMap;
import java.util.Map;

import compatibility.Environment;

import messages.TypeInfo;

/**
 * This is the basic factory of the toolkit. The main components will be created
 * through here.
 * 
 * @author ochipara
 * 
 */

public class CSense implements CSenseToolkit {
    protected String api = "v0";
    public static CSenseToolkit csense = null;
    private static int id = 0;
    public static final Map<String, IComponent> components = new HashMap<String, IComponent>();
    public static final Map<String, IScheduler> schedulers = new HashMap<String, IScheduler>();

    public CSense(String api) {
	this.api = api;
	if ("v2".equals(api) || "v3".equals(api)) {
	    if (Environment.isAndroid()) {
		try {
		    csense = (CSenseToolkit) Class.forName("base.v2.CSenseToolkitAndroidImpl").newInstance();
		} catch (InstantiationException e) {
		    e.printStackTrace();
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    } else {
		try {
		    csense = (CSenseToolkit) Class.forName("base.v2.CSenseToolkitImpl").newInstance();
		} catch (InstantiationException e) {
		    e.printStackTrace();
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }

	}

	if (csense == null)
	    throw new CSenseRuntimeException(
		    "failed to find CSense implementation [ver=" + api + "]");
    }

    public static IComponent getComponent(String name) {
	return components.get(name);
    }

    public static CSenseToolkit getImplementation() {
	return csense;
    }

    @Override
    public IComponentInternal newComponent() {
	IComponentInternal component = csense.newComponent();
	return component;
    }

    public static void registerComponent(IComponent component) {
	components.put(component.getName(), component);	
	component.setId(id ++);
    }

    @Override
    public IComponentInternal newThreadSafeComponent() {
	return csense.newThreadSafeComponent();
    }

    @Override
    public IScheduler newScheduler(String threadName) throws CSenseException {
	return csense.newScheduler(threadName);
    }

    @Override
    public IScheduler getScheduler(String threadName) {
	return schedulers.get(threadName);
    }

    @Override
    public void setApi(String api) {
	this.api = api;
    }

    @Override
    public <T extends Message> IMessagePool<T> newMessagePool(TypeInfo<T> type,
	    int capacity) throws CSenseException {
	return csense.newMessagePool(type, capacity);
    }

    @Override
    public IMessage newMessage(IMessagePool<? extends Message> pool, TypeInfo<? extends Message> type) throws CSenseException {
	return csense.newMessage(pool, type);
    }

    @Override
    public <T extends Message> ISource<T> newSource(TypeInfo<T> typeInfo) throws CSenseException {
	return csense.newSource(typeInfo);
    }

    @Override
    public void setMemoryPool(Class memoryPool) throws CSenseException {
	csense.setMemoryPool(memoryPool);	
    }

    @Override
    public void setTaskQueue(Class taskQueueClass) {
	csense.setTaskQueue(taskQueueClass);
    }

    @Override
    public void setTimerQueue(Class timerQueueClass) {
	csense.setTimerQueue(timerQueueClass);
    }    
}
