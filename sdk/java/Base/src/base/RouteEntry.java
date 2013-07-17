package base;

public class RouteEntry {
    private String _station;
	private int  _loc;
	private long _tid;
	private long _mid;
	private long _timestamp;
	private long _threadTime;
	private long _utime;
	private long _stime;
	
	RouteEntry() {
	    _tid = _timestamp = _threadTime = _utime = _stime = _mid = -1;
	}

	public RouteEntry set(String station, int loc, long mid, long tid, long timestamp, long threadTime, long utime, long stime) {
	    _station = station;
	    _loc = loc;
	    _mid = mid;
	    _timestamp = timestamp;
	    _threadTime = threadTime;
	    _utime = utime;
	    _stime = stime;
	    _tid = tid;
	    return this;
	}

	public String getStation() {
	    return _station;
	}

	public String setStation(String station) {
	    String old = _station;
	    _station = station;
	    return old;
	}
	
	public int getLoggingLocation() {
	    return _loc;
	}

	public long getThreadId() {
	    return _tid;
	}
	
	public long getMessageId() {
	    return _mid;
	}

	public long getTimestamp() {
	    return _timestamp;
	}
	
	public int setLoggingLocation(int loc) {
	    int old = _loc;
	    _loc = loc;
	    return old;
	}

	public long setTimestamp(long timestamp) {
	    long old = _timestamp;
	    _timestamp = timestamp;
	    return old;
	}

	public long getThreadTime() {
	    return _threadTime;
	}

	public long setThreadTime(long threadTime) {
	    long old = _threadTime;
	    _threadTime = threadTime;
	    return old;
	}

	public long getUserTime() {
	    return _utime;
	}

	public long setUserTime(long utime) {
	    long old = _utime;
	    _utime = utime;
	    return old;
	}

	public long getSystemTime() {
	    return _stime;
	}

	public long setSystemTime(long stime) {
	    long old = _stime;
	    _stime = stime;
	    return old;
	}
}
