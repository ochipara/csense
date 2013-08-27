package edu.uiowa.csense.pm;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.profiler.Tracer;

public class Profiler {   
    public static void main(String[] args) throws IOException {	
	String dirName = null;
	if (args.length == 0) dirName = System.getProperty("user.dir");
	else if (args.length == 1) dirName = args[0];
	else {
	    System.err.println("Profiler <dir>");
	    System.exit(-1);
	}
	
	File dir = new File(dirName);
	if (dir.exists() == false) {
	    throw new IllegalArgumentException("Specified directory does now exist");
	}
	
	String[] files = dir.list();
	List<String> filenames = new ArrayList<String>();
	for (int i = 0; i < files.length; i++) {
	    String filename = files[i];
	    if (filename.endsWith(".trace")) {
//		filenames.add(new File(dir, filename));
		filenames.add(filename);
		System.out.println("Found trace " + filename + " ...");
	    }
	}
	
	if (filenames.size() == 0) {
	    System.err.println("No trace files were found in the current directory");
	    System.exit(-1);
	} 
	
//	List<String> traces = new ArrayList<String>();
//	traces.add("fileMonitor.crt");
//	traces.add("form.crt");
//	traces.add("sound.crt");
//	traces.add("gps.crt");
//	traces.add("gpsLogger.crt");
//	Tracer.convertRouteUsageToMatlabData(dir.getAbsolutePath(), filenames);
	Tracer.saveRoutesToText(dir.getAbsolutePath(), filenames);

    }       
}
