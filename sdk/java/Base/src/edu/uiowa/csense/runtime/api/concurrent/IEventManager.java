package edu.uiowa.csense.runtime.api.concurrent;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Event;

public interface IEventManager {
    public Event nextEvent();
    public void scheduleEvent(Event event) throws CSenseException;    
    public void clear();
    public boolean hasEvent();
}
