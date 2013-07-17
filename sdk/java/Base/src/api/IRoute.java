package api;

import java.util.List;

import base.RouteEntry;

public interface IRoute extends Comparable<IRoute> {
    public boolean add(String station, int loc, long mid);
    public boolean add(String station, int loc, long mid, long timestamp, long threadTime, long utime, long stime);
    public boolean add(String station, int loc, long mid, long tid, long timestamp, long threadTime, long utime, long stime);

    public void clear();		
    public boolean isEmpty();		
    public int size();
    public int capacity();
    public int bytes();
    
    public String lastStation();
    public int lastLoggingLocation();
    public long lastMessageId();
    public long lastTimestamp();
    public long lastThreadTime();
    public long lastUserTime();
    public long lastSystemTime();
    
    public String getStation(int idx);	
    public void setStation(int idx, String station);
    
    public int  getLoggingLocation(int idx);
    public long getMessageId(int idx);   
    public long getThreadId(int idx);
    public long getTimestamp(int idx);
    public long getThreadTime(int idx);
    public long getUserTime(int idx);
    public long getSystemTime(int idx);
    public void setLoggingLocation(int idx, int loc);
    public void setTimestamp(int idx, long timestamp);
    public void setThreadTime(int idx, long threadTime);
    public void setUserTime(int idx, long utime);
    public void setSystemTime(int idx, long stime);
    
    public long getLatency();
    public int getPass();
    public int pass();
    public void normalize();
    
    @Override
    public boolean equals(Object o); 
    public void copy(IRoute route);
    public IRoute clone();
    public String debug();
    @Override
    public String toString();
    
    int prev(int idx);
    
    public List<RouteEntry> getEntries();
    
    
    
}