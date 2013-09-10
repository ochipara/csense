package edu.uiowa.csense.runtime.v4;

import java.io.File;
import java.io.IOException;

import edu.uiowa.csense.profiler.Utility;
import edu.uiowa.csense.runtime.compatibility.AndroidFileLogger;

public class CSenseAndroidComponent extends CSenseComponent {
    private static ThreadLocal<AndroidFileLogger> logger = new ThreadLocal<AndroidFileLogger>() {
	@Override protected AndroidFileLogger initialValue() {	    
	    try {
		String name = "t" + Thread.currentThread().getName() + ".log";
		AndroidFileLogger logger = new AndroidFileLogger(new File("/sdcard/AudiologyApp/", name));
		return logger;
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    
	    return null;
	}
    };
    
    public CSenseAndroidComponent() {
	super();
	
    }
    
    @Override
    public void error(Object... args) {
	StringBuilder sb = Utility.getStringBuilder();
	for (Object arg: args) {
		sb.append(arg.toString() + " ");
	}

	logger.get().e(_name, sb.toString());
    }
    
    @Override
    public void warn(Object... args) {
	StringBuilder sb = Utility.getStringBuilder();
	for (Object arg: args) {
		sb.append(arg.toString() + " ");
	}

	logger.get().w(_name, sb.toString());	
    }
    
    @Override
    public void info(Object... args) {
	StringBuilder sb = Utility.getStringBuilder();
	for (Object arg: args) {
		sb.append(arg.toString() + " ");
	}

	logger.get().i(_name, sb.toString());

    }
    
    @Override
    public void debug(Object... args) {
	StringBuilder sb = Utility.getStringBuilder();
	for (Object arg: args) {
		sb.append(arg.toString() + " ");
	}

	logger.get().d(_name, sb.toString());

    }
    
    @Override
    public void verbose(Object... args) {
	StringBuilder sb = Utility.getStringBuilder();
	for (Object arg: args) {
		sb.append(arg.toString() + " ");
	}

	logger.get().v(_name, sb.toString());

    }
}
