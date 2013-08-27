package base;

import java.util.ArrayList;
import java.util.List;

import compatibility.ThreadCPUUsage;
import api.IRoute;

public class Route implements IRoute, Cloneable {
    private List<RouteEntry> _entries;
    private int _capacity; // allocated capacity
    private int _next;     // next allocated slot to set
    private int _pass;

    public Route() {
	this(4);
    }

    public Route(int stations) {
	_entries = new ArrayList<RouteEntry>(_capacity = (stations * 2));
	for (int i = 0; i < _capacity; i++) {
	    _entries.add(new RouteEntry());
	}
    }

    /**
     * Add a station and its statistics according the calling thread.
     */
    @Override
    public boolean add(String station, int loc, long mid) {
//	long nanos = System.nanoTime();
	ThreadCPUUsage u = ThreadCPUUsage.getCPUUsage();
	return add(station, loc, mid, u.getRealTime(), u.getThreadTime(), u.getThreadUserTime(), u.getThreadSystemTime());
    }

    @Override
    public boolean add(String station, int loc, long mid, long timestamp, long threadTime, long utime, long stime) {
	return add(station, loc, mid, Thread.currentThread().getId(), timestamp, threadTime, utime, stime);
    }

    /**
     * Added a station and its statistics for the purpose of copy or clone.
     */
    @Override
    public boolean add(String station, int loc, long mid, long tid, long timestamp, long threadTime, long utime, long stime) {
	if(_next == _capacity) {
	    _entries.add(new RouteEntry());
	    _capacity++;
	}
	_entries.get(_next).set(station, loc, mid, tid, timestamp, threadTime, utime, stime);
	_next++;
	return true;
    }

    @Override
    public void clear() {
	_next = 0;
	_pass = 0;
    }

    @Override
    public boolean isEmpty() {
	return _next == 0;
    }
    
    @Override
    public int bytes() {
	// 4-byte number of entries
	// 4-byte station id
	// 4-byte trace loc id
	// 8-byte mid
	// 8-byte tid
	// 8-byte timestamp
	// 8-byte thread time
	// 8-byte user time
	// 8-byte system time
	return 4 + (4 * 2 + 8 * 6) * size();
    }
    
    @Override
    public int size() {
	return _next;
    }

    @Override
    public int capacity() {
	return _capacity;
    }

    @Override
    public String getStation(int idx) {
	return _entries.get(idx).getStation();
    }

    @Override
    public void setStation(int idx, String station) {
	_entries.get(idx).setStation(station);
    }

    @Override
    public int getLoggingLocation(int idx) {
	return _entries.get(idx).getLoggingLocation();
    }

    @Override
    public void setLoggingLocation(int idx, int loc) {
	_entries.get(idx).setLoggingLocation(loc);
    }

    @Override
    public long getMessageId(int idx) {
	return _entries.get(idx).getMessageId();
    }
    
    @Override
    public long getThreadId(int idx) {
	return _entries.get(idx).getThreadId();
    }

    @Override
    public long getTimestamp(int idx) {
	return _entries.get(idx).getTimestamp();
    }

    @Override
    public void setTimestamp(int idx, long timestamp) {
	_entries.get(idx).setTimestamp(timestamp);
    }

    @Override
    public long getThreadTime(int idx) {
	return _entries.get(idx).getThreadTime();
    }

    @Override
    public void setThreadTime(int idx, long threadTime) {
	_entries.get(idx).setThreadTime(threadTime);
    }

    @Override
    public long getUserTime(int idx) {
	return _entries.get(idx).getUserTime();
    }

    @Override
    public void setUserTime(int idx, long utime) {
	_entries.get(idx).setUserTime(utime);
    }

    @Override
    public long getSystemTime(int idx) {
	return _entries.get(idx).getSystemTime();
    }

    @Override
    public void setSystemTime(int idx, long stime) {
	_entries.get(idx).setSystemTime(stime);
    }

    @Override
    public long getLatency() {
	return _entries.isEmpty() ? 0 : getTimestamp(size() - 1) - getTimestamp(0);	    
    }

    @Override
    public int getPass() {
	return _pass;
    }

    @Override
    public int pass() {
	return ++_pass;
    }

    @Override
    public void normalize() {
	if(isEmpty()) return;
	long timestamp = getTimestamp(0);
	for(int i = 0; i < size(); i++)
	    setTimestamp(i, getTimestamp(i) - timestamp);
    }

    @Override
    public int prev(int idx) {
	for(int i = idx - 1; i >=0; i--)
	    if(getStation(i).equals(getStation(idx))) return i;

	return -1;
    }

    @Override
    public String lastStation() {
	return getStation(size()-1);
    }
    
    @Override
    public int lastLoggingLocation() {
	return getLoggingLocation(size()-1);
    }
    
    @Override
    public long lastMessageId() {
	return getMessageId(size()-1);
    }

    @Override
    public long lastTimestamp() {
	return getTimestamp(size()-1);
    }

    @Override
    public long lastThreadTime() {
	return getThreadTime(size()-1);
    }

    @Override
    public long lastUserTime() {
	return getUserTime(size()-1);
    }

    @Override
    public long lastSystemTime() {
	return getSystemTime(size()-1);
    }

    @Override
    public IRoute clone() {
	IRoute route = new Route((size() + 1)/ 2);
	for(int i = 0; i < size(); i++)
	    route.add(getStation(i), getLoggingLocation(i), getThreadId(i), getTimestamp(i), getThreadTime(i), getUserTime(i), getSystemTime(i));

	return route;
    }

    @Override
    public void copy(IRoute route) {
	clear();
	for(int i = 0; i < route.size(); i++)
	    add(route.getStation(i), route.getLoggingLocation(i), route.getThreadId(i), route.getTimestamp(i), route.getThreadTime(i), route.getUserTime(i), route.getSystemTime(i));

	_pass = route.getPass();
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Message Route").append("<").append(getPass()).append(">: ");
	for(int i = 0; i < size(); i++) {
	    builder.append(getStation(i)).append("(");
	    builder.append(getTimestamp(i)).append("ns,");
	    builder.append(getThreadTime(i)).append("ns)");
	    if(i != size() - 1) builder.append("->");
	}
	return builder.toString();
    }

    @Override
    public String debug() {
	StringBuilder builder = new StringBuilder();
	builder.append("Message Route").append("<").append(getPass()).append(">:\n");
	for(int i = 0; i < size(); i++) {
	    builder.append(getStation(i)).append("@").append(Debug.getTraceEventDescription(getLoggingLocation(i))).append("(");
	    builder.append(getThreadId(i)).append(",");
	    builder.append(getTimestamp(i)).append("ns,");
	    builder.append(getThreadTime(i)).append("ns,");
	    builder.append(getUserTime(i)).append("us,");
	    builder.append(getSystemTime(i)).append("us)");
	    if(i != size() - 1) builder.append("->\n");
	}
	return builder.toString();
    }

    @Override
    public int hashCode(){
	return bytes();
    }
    
    @Override
    public boolean equals(Object o) {
	if(o instanceof IRoute) {
	    IRoute route = (IRoute)o;
	    if(size() != route.size()) return false;			
	    for(int i = 0; i < size(); i++) {
		if(!getStation(i).equals(route.getStation(i))) 
		    return false;
	    }

	    return true;
	}

	return false;
    }

    @Override
    public int compareTo(IRoute route) {
	if(!isEmpty() && route.isEmpty()) return -1;
	else if(isEmpty() && !route.isEmpty()) return 1;
	else if(isEmpty() && route.isEmpty()) return 0;

	if(getTimestamp(0) > route.getTimestamp(0)) return 1;
	else if(getTimestamp(0) < route.getTimestamp(0)) return -1;
	else {
	    if(getTimestamp(size()-1) > route.getTimestamp(route.size()-1)) return 1;
	    else if(getTimestamp(size()-1) < route.getTimestamp(route.size()-1)) return -1;
	    else return 0;
	}
    }

    @Override
    public List<RouteEntry> getEntries() {
	return _entries;
    }

    
  
}