package edu.uiowa.csense.runtime.api.concurrent;

import edu.uiowa.csense.runtime.api.TimerEvent;

public interface ITimerEventManager {
    boolean isEmpty();
    TimerEvent peek();
    TimerEvent poll();
    void clear();
    boolean add(TimerEvent task);
    boolean remove(TimerEvent task);
}
