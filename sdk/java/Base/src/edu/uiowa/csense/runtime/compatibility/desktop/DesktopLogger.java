package edu.uiowa.csense.runtime.compatibility.desktop;

import java.util.logging.Level;

import edu.uiowa.csense.runtime.api.ILog;

public class DesktopLogger implements ILog {
    @Override
    public void d(String tag, Object... args) {
	StringBuffer sb = new StringBuffer();
	for (Object arg : args) {
	    sb.append(arg.toString() + " ");
	}

	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(tag);
	log.debug(sb.toString());
    }

    @Override
    public void e(String tag, Object... args) {
	StringBuffer sb = new StringBuffer();
	for (Object arg : args) {
	    sb.append(arg.toString() + " ");
	}

	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(tag);
	log.error(sb.toString());
    }

    @Override
    public void i(String tag, Object... args) {
	StringBuffer sb = new StringBuffer();
	for (Object arg : args) {
	    sb.append(arg.toString() + " ");
	}

	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(tag);
	log.info(sb.toString());
    }

    @Override
    public void v(String tag, Object... args) {
	StringBuffer sb = new StringBuffer();
	for (Object arg : args) {
	    sb.append(arg.toString() + " ");
	}

	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(tag);
	log.trace(sb.toString());
    }

    @Override
    public void w(String tag, Object... args) {
	StringBuffer sb = new StringBuffer();
	for (Object arg : args) {
	    sb.append(arg.toString() + " ");
	}

	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(tag);
	log.warn(sb.toString());
    }

    @Override
    public void setLevel(Level level) {
	if (level == Level.ALL) {
	    org.apache.log4j.Logger.getRootLogger().setLevel(
		    org.apache.log4j.Level.ALL);
	} else if (level == Level.INFO) {
	    org.apache.log4j.Logger.getRootLogger().setLevel(
		    org.apache.log4j.Level.INFO);
	} else if (level == Level.FINE) {
	    org.apache.log4j.Logger.getRootLogger().setLevel(
		    org.apache.log4j.Level.DEBUG);
	}
    }

}
