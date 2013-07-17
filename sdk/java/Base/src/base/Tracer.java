package base;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

import api.IRoute;
import api.IRouteUsage;

public class Tracer {
    public static IRouteUsage getRouteUsage(IRoute r) {	
	if(r.isEmpty()) return null;
	IRouteUsage usage = new RouteUsage();
	for(int i = 0; i < r.size(); i++) {
	    String station = r.getStation(i);
	    if(i == 0)
		usage.add(station);
	    else {
		int idx = usage.indexOfStation(station);
		if(idx == -1) usage.add(station);
		// added station
		idx = usage.indexOfStation(r.getStation(i-1));
		if(idx > 0 && usage.getWaitingTime(idx) == 0)
		    usage.addWaitingTime(idx, r.getTimestamp(i) - r.getTimestamp(i-1));
		else
		    usage.addExecTime(idx, r.getTimestamp(i) - r.getTimestamp(i-1));		    
		
		for(int j = i-1; j >= 0; j--) {		    
		    if(r.getThreadId(i) == r.getThreadId(j)) {
			idx = usage.indexOfStation(r.getStation(j));
			if(r.getStation(i).equals(r.getStation(j)) && usage.getWaitingThreadTime(idx) == 0) {
			    usage.addWaitingThreadTime(idx, r.getThreadTime(i) - r.getThreadTime(j));
			    usage.addWaitingUserTime(idx, r.getUserTime(i) - r.getUserTime(j));
			    usage.addWaitingSystemTime(idx, r.getSystemTime(i) - r.getSystemTime(j));
			} else {
			    usage.addExecThreadTime(idx, r.getThreadTime(i) - r.getThreadTime(j));
			    usage.addExecUserTime(idx, r.getUserTime(i) - r.getUserTime(j));
			    usage.addExecSystemTime(idx, r.getSystemTime(i) - r.getSystemTime(j));
			}
			break;
		    }
		}
	    }
	}
	return usage;
    }
    
    public static IRoute loadTrace(String filename) throws IOException {
 	// header info of ids of the stations and trace locations on the routes
 	// for exampe:
 	// stations=5
 	// source=0
 	// queue0=1
 	// worker0=2
 	// stats=3
 	// tap=4
 	// locations=4
 	// loc0=0
 	// loc1=1
 	// loc2=2
 	// loc3=3
 	//
 	// 4 bytes: route size
 	// 4 bytes: component id
 	// 4 bytes: loc id
	// 8 bytes: message id
 	// 8 bytes: thread id
 	// 8 bytes: timestamp
 	// 8 bytes: threadTime
 	// 8 bytes: utime
 	// 8 bytes: stime
 	// ... and so on
 	// => 4 + (4 * 2 + 8 * 5) * (route size) bytes for each route entry

 	RandomAccessFile file = new RandomAccessFile(filename, "r");
 	String line = file.readLine();	
 	int count = Integer.parseInt(line.substring(line.indexOf("=") + 1));
 	Map<Integer, String> stations = new HashMap<Integer, String>();
 	for(int i = 0; i < count; i++) {
 	    line = file.readLine();
 	    StringTokenizer tokenizer = new StringTokenizer(line, "=");
 	    String station = tokenizer.nextToken();
 	    int idx = Integer.parseInt(tokenizer.nextToken());
 	    stations.put(idx, station);
 	    System.out.printf("added station %d: %s\n", idx, station);
 	}
 	
 	line = file.readLine();	
 	count = Integer.parseInt(line.substring(line.indexOf("=") + 1));
 	for(int i = 0; i < count; i++) {
 	    line = file.readLine();
 	    StringTokenizer tokenizer = new StringTokenizer(line, "=");
 	    String location = tokenizer.nextToken();
 	    int idx = Integer.parseInt(tokenizer.nextToken());
 	    if(Debug.getTraceEventDescription(idx) == null) Debug.addTraceEvent(location);
 	    System.out.printf("added trace location %d: %s\n", idx, location);
 	}

 	IRoute r = new Route();	 
 	while(true) {
 	    int length = 0;
 	    try {
 		length = file.readInt();
 	    } catch(EOFException e) {
 		break;
 	    }

 	    for(int i = 0; i < length; i++) {
 		int stationId = file.readInt();
 		int locationId = file.readInt();
 		long mid = file.readLong();
 		long tid = file.readLong();
 		long timestamp = file.readLong();
 		long threadTime = file.readLong();
 		long utime = file.readLong();
 		long stime = file.readLong();
 		r.add(stations.get(stationId), locationId, mid, tid, timestamp, threadTime, utime, stime);
 	    } 	   
 	}

 	file.close();
 	return r;
    }
    
    public static Map<IRouteUsage, List<IRouteUsage>> loadMessageRouteUsages(String filename) throws IOException {
	// header info of ids of the stations and trace locations on the routes
	// for exampe:
	// stations=5
	// source=0
	// queue0=1
	// worker0=2
	// stats=3
	// tap=4
	// locations=4
	// loc0=0
	// loc1=1
	// loc2=2
	// loc3=3
	//
	// 4 bytes: route size
	// 4 bytes: component id
	// 4 bytes: loc id
	// 8 bytes: message id
	// 8 bytes: thread id
	// 8 bytes: timestamp
	// 8 bytes: threadTime
	// 8 bytes: utime
	// 8 bytes: stime
	// ... and so on
	// => 4 + (4 * 2 + 8 * 5) * (route size) bytes for each route entry

	RandomAccessFile file = new RandomAccessFile(filename, "r");
	String line = file.readLine();	
	int count = Integer.parseInt(line.substring(line.indexOf("=") + 1));
	Map<Integer, String> stations = new HashMap<Integer, String>();
	for(int i = 0; i < count; i++) {
	    line = file.readLine();
	    StringTokenizer tokenizer = new StringTokenizer(line, "=");
	    String station = tokenizer.nextToken();
	    int idx = Integer.parseInt(tokenizer.nextToken());
	    stations.put(idx, station);
	    System.out.printf("added station %d: %s\n", idx, station);
	}
	
	line = file.readLine();	
	count = Integer.parseInt(line.substring(line.indexOf("=") + 1));
	for(int i = 0; i < count; i++) {
	    line = file.readLine();
	    StringTokenizer tokenizer = new StringTokenizer(line, "=");
	    String location = tokenizer.nextToken();
	    int idx = Integer.parseInt(tokenizer.nextToken());
	    if(Debug.getTraceEventDescription(idx) == null) Debug.addTraceEvent(location);
	    System.out.printf("added trace location %d: %s\n", idx, location);
	}

	Map<IRouteUsage, List<IRouteUsage>> usages = new HashMap<IRouteUsage, List<IRouteUsage>>();
	while(true) {
	    int length = 0;
	    try {
		length = file.readInt();
	    } catch(EOFException e) {
		break;
	    }

	    IRoute r = new Route();	    
	    for(int i = 0; i < length; i++) {
		int stationId = file.readInt();
		int locationId = file.readInt();
		long tid = file.readLong();
		long timestamp = file.readLong();
		long threadTime = file.readLong();
		long utime = file.readLong();
		long stime = file.readLong();
		r.add(stations.get(stationId), locationId, tid, timestamp, threadTime, utime, stime);
	    }
	    

	    IRouteUsage usage = getRouteUsage(r);
	    if(usages.containsKey(usage)) {
		for(IRouteUsage u: usages.keySet()) {
		    if(!u.equals(usage)) continue; 
		    for(int i = 0; i < u.size(); i++) {
			u.addWaitingTime(i, usage.getWaitingTime(i));
			u.addWaitingThreadTime(i, usage.getWaitingThreadTime(i));
			u.addWaitingUserTime(i, usage.getWaitingUserTime(i));
			u.addWaitingSystemTime(i, usage.getWaitingSystemTime(i));
			u.addExecTime(i, usage.getExecTime(i));
			u.addExecThreadTime(i, usage.getExecThreadTime(i));
			u.addExecUserTime(i, usage.getExecUserTime(i));
			u.addExecSystemTime(i, usage.getExecSystemTime(i));
		    }
		    u.pass();
		    usages.get(u).add(usage);
		    break;
		}
	    } else {
		usage.pass();
		usages.put(usage, new ArrayList<IRouteUsage>());
		usages.get(usage).add(getRouteUsage(r));
		System.out.println("added route: " + r.debug());
	    }
	}

	file.close();
	for(IRouteUsage u: usages.keySet()) {
	    for(int i = 0; i < u.size(); i++) {
		u.setWaitingTime(i, (long)((double)u.getWaitingTime(i) / u.getPass() + .5));
		u.setWaitingThreadTime(i, (long)((double)u.getWaitingThreadTime(i) / u.getPass() + .5));
		u.setWaitingUserTime(i, (long)((double)u.getWaitingUserTime(i) / u.getPass() + .5));
		u.setWaitingSystemTime(i, (long)((double)u.getWaitingSystemTime(i) / u.getPass() + .5));
		u.setExecTime(i, (long)((double)u.getExecTime(i) / u.getPass() + .5));
		u.setExecThreadTime(i, (long)((double)u.getExecThreadTime(i) / u.getPass() + .5));
		u.setExecUserTime(i, (long)((double)u.getExecUserTime(i) / u.getPass() + .5));
		u.setExecSystemTime(i, (long)((double)u.getExecSystemTime(i) / u.getPass() + .5));
	    }
	}
	
	return usages;
    }
    
    public static void saveRoutesToText(String path, List<String> traces) throws IOException {
	List<File> files = new ArrayList<File>();
	for (String fn : traces) {
	    files.add(new File(path, fn));
	}
	
	saveRoutesToText(files);
    }
    
    private static void saveRoutesToText(List<File> files) throws IOException {	
	for (File file : files) {
	    try {
		saveTrace(file);
	    } catch (NumberFormatException e) {
		System.err.println("Error processing " + file.getName());
	    }
	}
    }

    private static void saveTrace(File file) throws IOException {
	IRoute route = loadTrace(file.getAbsolutePath());

	BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile() + ".txt"));
	for (RouteEntry entry : route.getEntries()) {
//		_station = station;
//		_loc = loc;
//	    	_mid = mid;
//		_tid = tid;
//		_timestamp = timestamp;
//		_threadTime = threadTime;
//		_utime = utime;
//		_stime = stime;
	String station = entry.getStation();
	int location = entry.getLoggingLocation();
	long mid = entry.getMessageId();
	long tid = entry.getThreadId();
	long timestamp = entry.getTimestamp();	
	long ttime = entry.getThreadTime();
	long utime = entry.getUserTime();
	long stime = entry.getSystemTime();
	
	String s = station + " " + location + " " + mid + " " + timestamp + " " + tid + " " + ttime + " " + utime + " " + stime + "\n";
	bw.write(s);		
	}
	bw.close();
    }

    public static boolean convertRouteUsageToMatlabData(String path, List<String> traces) throws IOException {
	File folder = new File(path);
	if(!folder.exists() || traces == null || traces.isEmpty()) return false;
	File routes = new File(folder, "routes");
	File components = new File(folder, "components");
	if(!routes.exists()) routes.mkdir();
	if(!components.exists()) components.mkdir();	
	for(int i = 0; i < traces.size(); i++)
	    traces.set(i, path + File.separator + traces.get(i));	
	
	int col = 0;
	List<IRouteUsage> totalUsages = new ArrayList<IRouteUsage>();
	for(String filename: traces) {
	    Map<IRouteUsage, List<IRouteUsage>> usages = Tracer.loadMessageRouteUsages(filename);
	    totalUsages.addAll(usages.keySet());
	    for(IRouteUsage usage: usages.keySet()) {
		List<IRouteUsage> passes = usages.get(usage);
		if(usage.size() > col) col = usage.size();
		System.out.printf("Mean<%d> %s\n", usage.getPass(), usage);
		System.out.println();

		for(int c = 0; c < usage.size(); c++) {			
		    double data[] = new double[passes.size() * 2];
		    for(int j = 0; j < passes.size(); j++) {
			int idx = 0;
			IRouteUsage u = passes.get(j);		    
			// data[j + idx++] = u.getWaitingTime(c);
			data[j + idx++ * passes.size()] = u.getWaitingThreadTime(c) == 0 ? u.getWaitingTime(c) : u.getWaitingThreadTime(c);
			// data[j + idx++ * passes.size()] = u.getExecTime(c);
			data[j + idx++ * passes.size()] = u.getExecThreadTime(c);
			// data[j + idx++ * passes.size()] = (u.getWaitingUserTime(c) + u.getExecUserTime(c)) * 1000;
			// data[j + idx++ * passes.size()] = (u.getWaitingSystemTime(c) + u.getExecSystemTime(c)) * 1000;
		    }

		    String route = usage.getStation(0);
		    String station = usage.getStation(c);
		    String name = "route_" + route + "_station_" + station;
		    MLDouble mlDouble = new MLDouble(name, data, passes.size());
		    List list = new ArrayList();
		    list.add(mlDouble);
		    new MatFileWriter(components + File.separator + name + ".mat", list);
		}
	    }
	}
	
	col *= 2;
	double data[] = new double[totalUsages.size() * col];
	for(int i = 0; i < totalUsages.size(); i++) {	
	    IRouteUsage usage = totalUsages.get(i);
	    for(int j = 0; j < usage.size(); j++) {
		data[i * col + 2 * j] = usage.getWaitingThreadTime(j) == 0 ? usage.getWaitingTime(j) : usage.getWaitingThreadTime(j);
		data[i * col + 2 * j + 1] = usage.getExecThreadTime(j);		 
	    }
	}
	
	MLDouble mlDouble = new MLDouble( "MessageRouteUsages", data, col);
	List list = new ArrayList();
	list.add( mlDouble );
	new MatFileWriter( routes + File.separator + "MessageRouteUsages.mat", list);
	return true;
    }
}
