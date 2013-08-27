package base.test;

import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.profiler.Route;
import edu.uiowa.csense.profiler.RoutingTable;
import edu.uiowa.csense.profiler.Tracer;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.profile.IRoute;
import edu.uiowa.csense.runtime.api.profile.IRouteUsage;
import edu.uiowa.csense.runtime.api.profile.IRoutingTable;
import edu.uiowa.csense.runtime.compatibility.ThreadCPUUsage;

public class MessageRouteTest {
    private static final String VERSION = "v2";
    static {
	new CSense(VERSION);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    IRoute newRoute(String[] cmps, int... timestamps) {
	IRoute r = new Route(cmps.length);
	for(int i = 0;  i < cmps.length; i++)
	    r.add(cmps[i], Debug.TRACE_MSG_SRC, timestamps[i], 0, 0, 0);
		
	return r;
    }


    
    @Test
    public void testRouteUsage() throws CSenseException {
	IRoute route = new Route();
	assertEquals(0, route.size());
	assertEquals(8, route.capacity());
	route.clear();
	assertEquals(0, route.size());
	assertEquals(8, route.capacity());
	
	final int TRACE_LOC_UPLOADER_START = Debug.addTraceLocation("C2.uploader.run().start");
	final int TRACE_LOC_UPLOADER_END = Debug.addTraceLocation("C2.uploader.run().start");
	route.add("C1", Debug.TRACE_MSG_SRC, 16789, 0, 2000, 500, 750);
	route.add("Q", Debug.TRACE_MSG_PUSH, 16789, 100, 2050, 520, 780);
	route.add("Q", Debug.TRACE_MSG_INPUT, 17762, 500, 2250, 570, 930);
	route.add("C2", Debug.TRACE_MSG_PUSH, 17762, 1000, 2500, 620, 1130);
	route.add("C2", Debug.TRACE_MSG_INPUT, 17762, 1250, 2600, 650, 1200);
	route.add("C2", TRACE_LOC_UPLOADER_START, 17862, 1300, 600, 250, 400);
	route.add("C2", TRACE_LOC_UPLOADER_END, 17862, 1450, 650, 270, 430);	
	route.add("T", Debug.TRACE_MSG_PUSH, 17762, 1500, 2800, 700, 1350);
	route.add("T", Debug.TRACE_MSG_INPUT, 17762, 1700, 2900, 730, 1420);
	route.add("C1", Debug.TRACE_MSG_RETURN, 17762, 2000, 3000, 750, 1500);
	
	assertFalse(route.isEmpty());
	assertEquals(10, route.size());
	assertEquals(10, route.capacity());	
	IRoute clone = route.clone();
	route.clear();
	assertEquals(0, route.size());
	assertEquals(10, route.capacity());	
	route.copy(clone);
	assertEquals(10, route.size());
	assertEquals(10, route.capacity());
	
	IRouteUsage usage = Tracer.getRouteUsage(route);
	assertEquals(4, usage.size());
//	System.out.println(route.debug());
//	System.out.println(usage);
	for(int i = 0; i < usage.size(); i++) {
	    long waitingTime = 0;
	    long waitingThreadTime = 0;
	    long waitingUserTime = 0;
	    long waitingSystemTime = 0;
	    long execTime = 0;
	    long execThreadTime = 0;
	    long execUserTime = 0;
	    long execSystemTime = 0;	    
	    switch(i) {
	    case 0:
		waitingTime = waitingThreadTime = waitingUserTime = waitingSystemTime = 0;
		execTime = 100;
		execThreadTime = 50;
		execUserTime = 20;
		execSystemTime = 30;
		break;
	    case 1:
		waitingTime = 400;
		waitingThreadTime = 0;
		waitingUserTime = 0;
		waitingSystemTime = 0;
		execTime = 500;
		execThreadTime = 250;
		execUserTime = 50;
		execSystemTime = 200;
		break;
	    case 2:
		waitingTime = 250;
		waitingThreadTime = 100;
		waitingUserTime = 30;
		waitingSystemTime = 70;
		execTime = 250;
		execThreadTime = 250 ;
		execUserTime = 70;
		execSystemTime = 180;
		break;
	    case 3:
		waitingTime = 200;
		waitingThreadTime = 100;
		waitingUserTime = 30;
		waitingSystemTime = 70;
		execTime = 300;
		execThreadTime = 100;
		execUserTime = 20;
		execSystemTime = 80;
		break;
	    }
	    assertEquals(waitingTime, usage.getWaitingTime(i));
	    assertEquals(waitingThreadTime, usage.getWaitingThreadTime(i));
	    assertEquals(waitingUserTime, usage.getWaitingUserTime(i));
	    assertEquals(waitingSystemTime, usage.getWaitingSystemTime(i));
	    assertEquals(execTime, usage.getExecTime(i));
	    assertEquals(execThreadTime, usage.getExecThreadTime(i));
	    assertEquals(execUserTime, usage.getExecUserTime(i));
	    assertEquals(execSystemTime, usage.getExecSystemTime(i));
	}
    }
    
    @Test
    public void testRouteUnion() throws CSenseException {
	final int ROUTE_LENGTH = 5+1;
	final int ROUTING_TABLE_CAPACITY = 8;
	String[] path1 = new String[] {"S0", "C1", "C2", "C3", "C4", "S0"};
	
	// Path1 passes
	IRoute[] passes1 = new Route[ROUTING_TABLE_CAPACITY];
	passes1[0] = newRoute(path1, 5,6,7,8,9,10);
	passes1[1] = newRoute(path1, 6,7,8,9,10,11);
	passes1[2] = newRoute(path1, 7,8,9,10,11,12);
	passes1[3] = newRoute(path1, 15,17,19,23,24,25);	
	passes1[4] = newRoute(path1, 0,1,2,3,4,5);	
	
	IRoutingTable table1 = new RoutingTable(ROUTE_LENGTH, ROUTING_TABLE_CAPACITY);	
	for(int i = 0; i < passes1.length; i++) table1.add(passes1[i]);	
	
	// Path2 passes
	String[] path2 = new String[]{"S1", "C5", "C2", "C6", "C7", "S1"};
	IRoute[] passes2 = new Route[ROUTING_TABLE_CAPACITY];
	passes2[0] = newRoute(path2, 6,7,8,9,10,11);
	passes2[1] = newRoute(path2, 7,8,9,10,11,12);
	passes2[2] = newRoute(path2, 8,9,10,11,12,13);
	passes2[3] = newRoute(path2, 16,18,20,24,25,26);	
	passes2[4] = newRoute(path2, 1,2,3,4,5,6);	
	
	IRoutingTable table2 = new RoutingTable(ROUTE_LENGTH, ROUTING_TABLE_CAPACITY);	
	for(int i = 0; i < passes2.length; i++) table2.add(passes2[i]);	
	
	// Passes 1 Union
	IRoutingTable p1u = table1.union();
	assertEquals(12+10, p1u.routeTime());
	assertEquals(25-0, p1u.timeSpan());
//	System.out.println("path1 union:\n"+p1u.toString());
	
	IRoutingTable p2u = table2.union();
	assertEquals(12+10, p2u.routeTime());
	assertEquals(26-1, p2u.timeSpan());
//	System.out.println("path2 union:\n"+p2u.toString());
	
	// Path1 and Path2 Union
	IRoutingTable p12u = p1u.clone();
	p12u.add(p2u);
	p12u = p12u.union();
	assertEquals(13+11, p12u.routeTime());
	assertEquals(26-0, p12u.timeSpan());
//	System.out.println("path12 union:\n"+p12u.toString());
	
	//Path Utilization
	//Component Time Union / a Path Time Union
	//Component Time Union / all Path Time Union
    }
    
    @Test
    public void testRoutingTablePeekRemoveSort() throws CSenseException {
	final int ROUTE_LENGTH = 5+1;
	final int ROUTING_TABLE_CAPACITY = 7;
	
	IRoute[] routes = new Route[ROUTING_TABLE_CAPACITY];
	String[] path = new String[] {"S0", "C1", "C2", "C3", "C4", "S0"};
	routes[0] = newRoute(path, 5,6,7,8,9,10);
	routes[1] = newRoute(path, 6,7,8,9,10,11);
	routes[2] = newRoute(path, 7,8,9,10,11,12);
	routes[3] = newRoute(path, 0,1,2,3,4,5);	
	routes[4] = newRoute(path, 6,7,8,9,10,11);
	routes[5] = newRoute(path, 8,9,10,11,12,13);
	routes[6] = newRoute(path, 9,10,11,12,13,14);
	
	IRoutingTable table = new RoutingTable(ROUTE_LENGTH, ROUTING_TABLE_CAPACITY);	
	assertTrue(table.isEmpty());
	for(int i = 0; i < routes.length; i++) {
	    boolean ret = table.add(routes[i]);
	    assertTrue(ret);
	    assertEquals(i + 1, table.size());
	}
	
	assertTrue(table.isFull());
	assertEquals(ROUTING_TABLE_CAPACITY, table.capacity());
	assertEquals(table.size(), table.capacity());	
	assertEquals(-1, routes[0].compareTo(routes[1]));
	
	table.sort();
	assertEquals(routes[3], table.get(0));
	assertEquals(routes[0], table.get(1));
	assertEquals(routes[1], table.get(2));
	assertEquals(routes[4], table.get(3));
	assertEquals(routes[2], table.get(4));
	assertEquals(routes[5], table.get(5));
	assertEquals(routes[6], table.get(6));
	assertNotNull(table.get(6));
	
	assertEquals(routes[6], table.peek());
	assertEquals(routes[6], table.remove());
	assertEquals(routes[5], table.peek());
	assertEquals(ROUTING_TABLE_CAPACITY - 1, table.size());
	assertFalse(table.isFull());
    }
    
    @Test
    public void testConvertRouteUsageToMatlabData() throws IOException {
	List<String> traces = new ArrayList<String>();
	traces.add("fileMonitor.crt");
	traces.add("form.crt");
	traces.add("sound.crt");
	traces.add("gps.crt");
	traces.add("gpsLogger.crt");
	Tracer.convertRouteUsageToMatlabData("AudioSense/v6-tracer", traces);
	
//	int col = 0;
//	List<IRouteUsage> totalUsages = new ArrayList<IRouteUsage>();
//	for(String filename: filenames) {
//	    Map<IRouteUsage, List<IRouteUsage>> usages = Tracer.loadMessageRouteUsages(filename);
//	    totalUsages.addAll(usages.keySet());
//	    for(IRouteUsage usage: usages.keySet()) {
//		List<IRouteUsage> passes = usages.get(usage);
//		if(usage.size() > col) col = usage.size();
//		System.out.printf("Mean<%d> %s\n", usage.getPass(), usage);
//		System.out.println();
//
//		for(int c = 0; c < usage.size(); c++) {			
//		    double data[] = new double[passes.size() * 2];
//		    for(int j = 0; j < passes.size(); j++) {
//			int idx = 0;
//			IRouteUsage u = passes.get(j);		    
//			// data[j + idx++] = u.getWaitingTime(c);
//			data[j + idx++ * passes.size()] = u.getWaitingThreadTime(c) == 0 ? u.getWaitingTime(c) : u.getWaitingThreadTime(c);
//			// data[j + idx++ * passes.size()] = u.getExecTime(c);
//			data[j + idx++ * passes.size()] = u.getExecThreadTime(c);
//			// data[j + idx++ * passes.size()] = (u.getWaitingUserTime(c) + u.getExecUserTime(c)) * 1000;
//			// data[j + idx++ * passes.size()] = (u.getWaitingSystemTime(c) + u.getExecSystemTime(c)) * 1000;
//		    }
//
//		    String route = usage.getStation(0);
//		    String station = usage.getStation(c);
//		    String name = "route_" + route + "_station_" + station;
//		    MLDouble mlDouble = new MLDouble(name, data, passes.size());
//		    List list = new ArrayList();
//		    list.add(mlDouble);
//		    new MatFileWriter( "AudioSense/" + name + ".mat", list);
//		}
//	    }
//	}
//	
//	col *= 2;
//	double data[] = new double[totalUsages.size() * col];
//	for(int i = 0; i < totalUsages.size(); i++) {	
//	    IRouteUsage usage = totalUsages.get(i);
//	    for(int j = 0; j < usage.size(); j++) {
//		data[i * col + 2 * j] = usage.getWaitingThreadTime(j) == 0 ? usage.getWaitingTime(j) : usage.getWaitingThreadTime(j);
//		data[i * col + 2 * j + 1] = usage.getExecThreadTime(j);		 
//	    }
//	}
//	
//	MLDouble mlDouble = new MLDouble( "MessageRouteUsages", data, col);
//	List list = new ArrayList();
//	list.add( mlDouble );
//	new MatFileWriter( "AudioSense/Audiology-MessageRouteUsages.mat", list);
    }       
}
