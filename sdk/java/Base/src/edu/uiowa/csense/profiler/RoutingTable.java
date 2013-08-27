package edu.uiowa.csense.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uiowa.csense.runtime.api.profile.IRoute;
import edu.uiowa.csense.runtime.api.profile.IRoutingTable;

public class RoutingTable implements IRoutingTable, Cloneable {
    private List<IRoute> _routes;
    private int _next;
    private int _stations;
    private int _capacity;

    public RoutingTable(int stations, int capacity) {
	_stations = stations;
	_capacity = capacity;
	_routes = new ArrayList<IRoute>(_capacity);
	for(int i = 0; i < capacity; i++)
	    _routes.add(new Route(_stations));
    }
    
    @Override
    public boolean add(IRoute route) {
	if(isFull() || route == null) return false;
	IRoute r = _routes.get(_next);
	r.copy(route);
	_next++;
	return true;
    }

    @Override
    public int add(List<IRoute> routes) {
	if(isFull() || routes == null) return 0;
	int count = 0;
	for(int i = 0; i < routes.size(); i++) {
	    if(add(routes.get(i))) count++;
	    else break;
	}
	return count;
    }

    @Override
    public int add(IRoutingTable table) {
	int count = 0;
	for(int i = 0; i < table.size(); i++) {
	    if(add(table.get(i))) count++;
	    else break;
	}
	return count;
    }
    
    @Override
    public IRoute peek() {
	if(isEmpty()) return null;
	return _routes.get(_next - 1);
    }
    
    @Override
    public IRoute remove() {
	if(isEmpty()) return null;
	return _routes.get(--_next);
    }

    @Override
    public void clear() {
	_next = 0;
    }

    @Override
    public boolean isEmpty() {
	return _next == 0;
    }

    @Override
    public boolean isFull() {
	return _next == _capacity;
    }
    
    @Override
    public int size() {
	return _next;
    }
    
    @Override
    public int capacity() {
	return _routes.size();
    }

    @Override
    public IRoute get(int idx) {
	if(idx < 0 || idx >= size()) return null;
	return _routes.get(idx);
    }

    @Override
    public IRoutingTable sort() {
	Collections.sort(_routes);
	return this;
    }

    @Override
    public IRoutingTable union() {
	sort();
	IRoutingTable union = new RoutingTable(_stations, size());
	for(int i = 0; i < size(); i++) {
	    IRoute pass = get(i);
	    if(union.isEmpty()) {
		IRoute r = new Route();
		r.add(pass.getStation(0), pass.getLoggingLocation(0), pass.getMessageId(0), pass.getTimestamp(0), pass.getThreadTime(0), pass.getUserTime(0), pass.getSystemTime(0));
		r.add(pass.lastStation(), pass.lastLoggingLocation(), pass.lastMessageId(), pass.lastTimestamp(), pass.lastThreadTime(), pass.lastUserTime(), pass.lastSystemTime());
		union.add(r);
	    } else {
		IRoute prev = union.peek();
		if(prev.lastTimestamp() >= pass.getTimestamp(0)) {
		    // overlap
		    if(prev.lastTimestamp() < pass.lastTimestamp()) {
			prev.setStation(prev.size()-1, pass.lastStation());
			prev.setTimestamp(prev.size()-1, pass.lastTimestamp());
		    }
		} else {
		    // non-overlap
		    IRoute r = new Route();
		    r.add(pass.getStation(0), pass.getLoggingLocation(0), pass.getMessageId(0), pass.getTimestamp(0), pass.getThreadTime(0), pass.getUserTime(0), pass.getSystemTime(0));
    		    r.add(pass.lastStation(), pass.lastLoggingLocation(), pass.lastTimestamp(), pass.lastTimestamp(), pass.lastThreadTime(), pass.lastUserTime(), pass.lastSystemTime());
    		    union.add(r);
		}
	    }
	}
	return union;
    }

    @Override
    public long routeTime() {
	long time = 0;
	for(int i = 0; i < size(); i++) {
	    IRoute r = get(i);
	    time += r.lastTimestamp() - r.getTimestamp(0);
	}
	return time;
    }

    @Override
    public long timeSpan() {
	IRoutingTable union = union();
	return union.peek().lastTimestamp() - union.get(0).getTimestamp(0);
    }
    
    @Override
    public IRoutingTable clone() {
	IRoutingTable table = new RoutingTable(_stations, _capacity);
	table.add(this);
	return table;
    }
    
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	for(int i = 0; i < size(); i++) {
	    builder.append(get(i).toString()).append("\n");
	}
	return builder.toString();
    }
}
