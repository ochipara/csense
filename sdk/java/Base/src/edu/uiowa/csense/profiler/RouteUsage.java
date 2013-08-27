package edu.uiowa.csense.profiler;

import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.runtime.api.profile.IRouteUsage;

public class RouteUsage implements IRouteUsage {
    private class UsageEntry {
	private String _station;
	
	private long _waitingTime;
	private long _waitingThreadTime;
	private long _waitingUserTime;
	private long _waitingSystemTime;
	
	private long _execTime;
	private long _execThreadTime;
	private long _execUserTime;
	private long _execSystemTime;
	
	private UsageEntry(String station) {
	    this(station, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	private UsageEntry(String station, long waitingTime, long waitingThreadTime, long waitingUserTime, long waitingSystemTime, long execTime, long execThreadTime, long execUserTime, long execSystemTime) {
	    _station = station;
	    _waitingTime = waitingTime;
	    _waitingThreadTime = waitingThreadTime;
	    _waitingUserTime = waitingUserTime;
	    _waitingSystemTime = waitingSystemTime;
	    _execTime = execTime;	    
	    _execThreadTime = execThreadTime;
	    _execUserTime = execUserTime;
	    _execSystemTime = execSystemTime;
	}
	
	public String getStation() {
	    return _station;
	}
	
	/**
	 * Component waiting time for a message.
	 * @return Waiting time in nanoseconds.
	 */
	public long getWaitingTime() {
	    return _waitingTime;
	}
	
	/**
	 * Component execution time for a message.
	 * @return Execution time in nanoseconds.
	 */
	public long getExecTime() {
	    return _execTime;
	}
	
	public long getTotalTime() {
	    return _waitingTime + _execTime;
	}
	
	public long getWaitingThreadTime() {
	    return _waitingThreadTime;
	}
	
	public long getExecThreadTime() {
	    return _execThreadTime;
	}
	
	public long getTotalThreadTime() {
	    return _waitingThreadTime + _execThreadTime;
	}
	
	public long getWaitingUserTime() {
	    return _waitingUserTime;
	}
	
	public long getWaitingSystemTime() {
	    return _waitingSystemTime;
	}
	
	public long getExecUserTime() {
	    return _execUserTime;
	}
	
	public long getExecSystemTime() {
	    return _execSystemTime;
	}
	
	public long getWaitingCPUTime() {
	    return _waitingUserTime + _waitingSystemTime;
	}
	
	public long getExecCPUTime() {
	    return _execUserTime + _execSystemTime;
	}
	
	public long getTotalCPUTime() {
	    return getWaitingCPUTime() + getExecCPUTime();
	}
	
	public void setStation(String station) {
	    _station = station;
	}
	
	public void setWaitingTime(long waitingTime) {
	    _waitingTime = waitingTime;
	}
	
	public void setWaitingThreadTime(long waitingThreadTime) {
	    _waitingThreadTime = waitingThreadTime;
	}
	
	public void setWaitingUserTime(long waitingUserTime) {
	    _waitingUserTime = waitingUserTime;
	}
	
	public void setWaitingSystemTime(long waitingSystemTime) {
	    _waitingSystemTime = waitingSystemTime;
	}
	
	public void setExecTime(long execTime) {
	    _execTime = execTime;
	}
	
	public void setExecThreadTime(long execThreadTime) {
	    _execThreadTime = execThreadTime;
	}
	
	public void setExecUserTime(long execUserTime) {
	    _execUserTime = execUserTime;
	}
	
	public void setExecSystemTime(long execSystemTime) {
	    _execSystemTime = execSystemTime;
	}
    }

    private List<UsageEntry> _usages;
    private int _pass;
    
    public RouteUsage() {
	_usages = new ArrayList<UsageEntry>();
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
    public boolean add(String station) {
	return _usages.add(new UsageEntry(station));
    }
    
    @Override
    public boolean add(String station, 
		long waitingTime, 
		long waitingThreadTime,
		long waitingUserTime, 
		long waitingSystemTime,
		long execTime, 
		long execThreadTime,
		long execUserTime, 
		long execSystemTime) {
	return _usages.add(new UsageEntry(station, waitingTime, waitingThreadTime, waitingUserTime, waitingSystemTime, execTime, execThreadTime, execUserTime, execSystemTime));
    }
    
    @Override
    public int size() {
	return _usages.size();
    }
    
    @Override
    public void addWaitingTime(int idx, long t) {
	setWaitingTime(idx, getWaitingTime(idx) + t);
    }
    
    @Override
    public void addWaitingThreadTime(int idx, long t) {
	setWaitingThreadTime(idx, getWaitingThreadTime(idx) + t);
    }
    
    @Override
    public void addWaitingUserTime(int idx, long t) {
	setWaitingUserTime(idx, getWaitingUserTime(idx) + t);
    }
    
    @Override
    public void addWaitingSystemTime(int idx, long t) {
	setWaitingSystemTime(idx, getWaitingSystemTime(idx) + t);
    }
    
    @Override
    public void addExecTime(int idx, long t) {
	setExecTime(idx, getExecTime(idx) + t);
    }
    
    @Override
    public void addExecThreadTime(int idx, long t) {
	setExecThreadTime(idx, getExecThreadTime(idx) + t);
    }
    
    @Override
    public void addExecUserTime(int idx, long t) {
	setExecUserTime(idx, getExecUserTime(idx) + t);
    }
    
    @Override
    public void addExecSystemTime(int idx, long t) {
	setExecSystemTime(idx, getExecSystemTime(idx) + t);
    }
    
    @Override
    public void setStation(int idx, String station) {
	_usages.get(idx).setStation(station);
    }
    
    @Override
    public void setWaitingTime(int idx, long waitingTime) {
	_usages.get(idx).setWaitingTime(waitingTime);
    }
    
    @Override
    public void setWaitingThreadTime(int idx, long waitingThreadTime) {
	_usages.get(idx).setWaitingThreadTime(waitingThreadTime);
    }
    
    @Override
    public void setWaitingUserTime(int idx, long waitingUserTime) {
	_usages.get(idx).setWaitingUserTime(waitingUserTime);
    }
    
    @Override
    public void setWaitingSystemTime(int idx, long waitingSystemTime) {
	_usages.get(idx).setWaitingSystemTime(waitingSystemTime);
    }
    
    @Override
    public void setExecTime(int idx, long execTime) {
	_usages.get(idx).setExecTime(execTime);
    }
    
    @Override
    public void setExecThreadTime(int idx, long execThreadTime) {
	_usages.get(idx).setExecThreadTime(execThreadTime);
    }
    
    @Override
    public void setExecUserTime(int idx, long execUserTime) {
	_usages.get(idx).setExecUserTime(execUserTime);
    }
    
    @Override
    public void setExecSystemTime(int idx, long execSystemTime) {
	_usages.get(idx).setExecSystemTime(execSystemTime);
    }

    @Override
    public int indexOfStation(String station) {
	for(int i = 0; i < _usages.size(); i++)
	    if(getStation(i).equals(station)) return i;
	
	return -1;
    }
    
    @Override
    public String getStation(int idx) {
	return _usages.get(idx).getStation();
    }

    @Override
    public long getWaitingTime(int idx) {
	return _usages.get(idx).getWaitingTime();
    }

    @Override
    public long getWaitingThreadTime(int idx) {
	return _usages.get(idx).getWaitingThreadTime();
    }
    
    @Override
    public long getWaitingUserTime(int idx) {
	return _usages.get(idx).getWaitingUserTime();
    }

    @Override
    public long getWaitingSystemTime(int idx) {
	return _usages.get(idx).getWaitingSystemTime();
    }

    @Override
    public long getWaitingCPUTime(int idx) {
	return _usages.get(idx).getWaitingCPUTime();
    }

    @Override
    public long getExecTime(int idx) {
	return _usages.get(idx).getExecTime();
    }
    
    @Override
    public long getExecThreadTime(int idx) {
	return _usages.get(idx).getExecThreadTime();
    }

    @Override
    public long getExecUserTime(int idx) {
	return _usages.get(idx).getExecUserTime();
    }

    @Override
    public long getExecSystemTime(int idx) {
	return _usages.get(idx).getExecSystemTime();
    }

    @Override
    public long getExecCPUTime(int idx) {
	return _usages.get(idx).getExecCPUTime();
    }

    @Override
    public long getTotalTime(int idx) {
	return _usages.get(idx).getTotalTime();
    }

    @Override
    public long getTotalThreadTime(int idx) {
	return _usages.get(idx).getTotalThreadTime();
    }
    
    @Override
    public long getTotalCPUTime(int idx) {
	return _usages.get(idx).getTotalCPUTime();
    }
    
    @Override
    public int hashCode() {	
	return size();
    }
    
    @Override
    public boolean equals(Object o) {
	if(o instanceof IRouteUsage) {
	    IRouteUsage usage = (IRouteUsage)o;
	    if(size() != usage.size()) return false;			
	    for(int i = 0; i < size(); i++) {
		if(!getStation(i).equals(usage.getStation(i))) 
		    return false;
	    }

	    return true;
	}

	return false;
    }
    
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Route Usage").append(":\n");
	for(int i = 0; i < size(); i++) {
	    builder.append(getStation(i)).append("(");
	    builder.append(getWaitingTime(i)).append("ns,");
	    builder.append(getWaitingThreadTime(i)).append("ns [");
	    builder.append(getWaitingUserTime(i)).append("us,");
	    builder.append(getWaitingSystemTime(i)).append("us] | ");
	    builder.append(getExecTime(i)).append("ns,");
	    builder.append(getExecThreadTime(i)).append("ns [");
	    builder.append(getExecUserTime(i)).append("us,");
	    builder.append(getExecSystemTime(i)).append("us])");
	    if(i != size() - 1) builder.append("->\n");
	}
	return builder.toString();
    }
}
