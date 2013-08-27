package edu.uiowa.csense.runtime.compatibility;

import java.util.logging.Level;

import edu.uiowa.csense.runtime.api.ILog;
public class Log {
    protected static boolean ANDROID = true;
    protected static ILog _log = null;

    static {
	Class<?> logCls = null;
	try {
	    
	    logCls = Class.forName("edu.uiowa.csense.runtime.compatibility.AndroidLogger");
	} catch (ClassNotFoundException e) {
	}

	try {
	    if (logCls == null)
		logCls = Class.forName("compatibility.DesktopLogger");
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace();
	}

	try {
	    _log = (ILog) logCls.newInstance();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	}
    }

    public static void e(String tag, Object... args) {
	_log.e(tag, args);
    }

    public static void w(String tag, Object... args) {
	_log.w(tag, args);
    }

    public static void i(String tag, Object... args) {
	_log.i(tag, args);
    }

    public static void d(String tag, Object... args) {
	_log.d(tag, args);
    }

    public static void v(String tag, Object... args) {
	_log.v(tag, args);
    }

    public static void level(Level level) {
	_log.setLevel(level);
    }
}
