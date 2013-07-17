package api;

public interface IMessagePool<T extends Message> {
    // public abstract void allocate(int capacity);
    // public abstract boolean isEmpty();
    // public abstract boolean isFull();
    public void put(T m);
    public T get();
    public T getAndBlock() throws InterruptedException;
    public int getAvailable();
    public void setSource(ISource<T> source);
}
