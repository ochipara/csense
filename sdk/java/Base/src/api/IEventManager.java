package api;

public interface IEventManager {
    boolean isEmpty();
    TimerEvent peek();
    TimerEvent poll();
    void clear();
    boolean add(TimerEvent task);
    boolean remove(TimerEvent task);
}
