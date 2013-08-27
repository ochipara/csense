package api;

import messages.TypeInfo;

/**
 * This is the interface to the generic factory that each csense implementation
 * will have to provide.
 * 
 * @author ochipara
 * 
 */
public interface CSenseToolkit {
    public IComponentInternal newComponent();
    public IComponentInternal newThreadSafeComponent();
    public <T extends Message> ISource<T> newSource(TypeInfo<T> typeInfo) throws CSenseException;
    public IScheduler newScheduler(String threadName) throws CSenseException;    
    public IMessage newMessage(IMessagePool<? extends Message> pool, TypeInfo<? extends Message> type) throws CSenseException;
    public <T extends Message> IMessagePool<T> newMessagePool(TypeInfo<T> type, int capacity) throws CSenseException;
    public IScheduler getScheduler(String schedulerId);
    
    
    /**
     * Control the toolkit and its implementation
     */
    public void setApi(String api) throws CSenseException;
    public void setMemoryPool(Class memoryPool) throws CSenseException;
    public void setTaskQueue(Class taskQueueClass);
    public void setTimerQueue(Class timerQueueClass);
}
