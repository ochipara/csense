package edu.uiowa.csense.runtime.compatibility;

public class Environment {
    public static EnvironmentInterface environment = null;
    
    public static boolean isAndroid() {
	try {
	     Class.forName("compatibility.AndroidEnvironment");
	     return true;
	} catch (ClassNotFoundException e) {
	    return false;
	}
    }

    static {
	Class<?> cls = null;
	try {
	    cls = Class.forName("compatibility.AndroidEnvironment");
	} catch (ClassNotFoundException e) {
	}

	try {
	    if (cls == null) {
		cls = Class.forName("compatibility.DesktopEnvironment");
	    }
	} catch (ClassNotFoundException e) {
	    System.err.println("Environment not found! Quitting!");
	    System.exit(-1);
	}

	try {
	    environment = (EnvironmentInterface) cls.newInstance();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	    System.exit(-1);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
}
