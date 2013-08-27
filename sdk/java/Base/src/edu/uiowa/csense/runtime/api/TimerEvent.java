package edu.uiowa.csense.runtime.api;

public class TimerEvent extends Task implements Comparable<TimerEvent> {
    private long _time;
    private boolean _scheduled = false;

    public long scheduledExecutionTime(long time) {
	return _time = time;
    }

    public long scheduledExecutionTime() {
	return _time;
    }

    @Override
    public int compareTo(TimerEvent task) {
	if (_time < task.scheduledExecutionTime())
	    return -1;
	if (_time > task.scheduledExecutionTime())
	    return 1;
	return 0;	
    }
    
    public boolean isScheduled() {
	return _scheduled;		
    }
    
    public void setScheduled(boolean scheduled) {
	_scheduled = scheduled;
    }
}
