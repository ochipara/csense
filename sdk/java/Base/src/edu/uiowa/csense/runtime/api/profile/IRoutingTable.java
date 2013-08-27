package edu.uiowa.csense.runtime.api.profile;

import java.util.List;

public interface IRoutingTable {
    public boolean add(IRoute route);
    public int add(List<IRoute> routes);
    public int add(IRoutingTable table);
    public IRoute peek();
    public IRoute remove();
    
    public void clear();		
    public boolean isEmpty();
    public boolean isFull();
    public int size();
    public int capacity();
    public IRoute get(int idx);
    
    public IRoutingTable clone();
    public IRoutingTable sort();
    public IRoutingTable union();
    public long routeTime();
    public long timeSpan();
    
    @Override
    public String toString();
}
