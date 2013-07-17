package api;

abstract public class TimerTask extends Task implements Comparable<TimerTask>, Runnable {
    private long _time;

    public long scheduledExecutionTime(long time) {
	return _time = time;
    }

    public long scheduledExecutionTime() {
	return _time;
    }

    @Override
    public int compareTo(TimerTask task) {
	if (_time < task.scheduledExecutionTime())
	    return -1;
	if (_time > task.scheduledExecutionTime())
	    return 1;
	return 0;
    }
}