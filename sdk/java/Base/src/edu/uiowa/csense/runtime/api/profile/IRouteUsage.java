package edu.uiowa.csense.runtime.api.profile;

public interface IRouteUsage {
    public boolean add(String station, 
	    		long waitingTime,
	    		long waitingThreadTime,
	    		long waitingUserTime, 
	    		long waitingSystemTime,
	    		long execTime, 
	    		long execThreadTime,
	    		long execUserTime, 
	    		long execSystemTime);
    
    public int size();		
    public String getStation(int idx);	
    public long getWaitingTime(int idx);
    public long getWaitingThreadTime(int idx);
    public long getWaitingUserTime(int idx);
    public long getWaitingSystemTime(int idx);
    public long getWaitingCPUTime(int idx);
    public long getExecTime(int idx);
    public long getExecThreadTime(int idx);
    public long getExecUserTime(int idx);
    public long getExecSystemTime(int idx);
    public long getExecCPUTime(int idx);

    public void setStation(int idx, String station);
    public void setWaitingTime(int idx, long waitingTime);
    public void setWaitingThreadTime(int idx, long waitingThreadTime);
    public void setWaitingUserTime(int idx, long waitingUserTime);
    public void setWaitingSystemTime(int idx, long waitingSystemTime);
    public void setExecTime(int idx, long execTime);
    public void setExecThreadTime(int idx, long execThreadTime);
    public void setExecUserTime(int idx, long execUserTime);
    public void setExecSystemTime(int idx, long execSystemTime);
    
    public long getTotalTime(int idx);
    public long getTotalThreadTime(int idx);
    public long getTotalCPUTime(int idx);

    int indexOfStation(String station);
    boolean add(String station);
    public void addWaitingTime(int idx, long l);
    public void addWaitingThreadTime(int idx, long l);
    public void addWaitingUserTime(int idx, long l);
    public void addWaitingSystemTime(int idx, long l);
    public void addExecTime(int idx, long t);
    public void addExecThreadTime(int idx, long t);
    public void addExecUserTime(int idx, long t);
    public void addExecSystemTime(int idx, long t);

    int getPass();
    int pass();
}
