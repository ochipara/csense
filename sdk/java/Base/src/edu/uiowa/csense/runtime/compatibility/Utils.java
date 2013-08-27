package edu.uiowa.csense.runtime.compatibility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    protected static final ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
	@Override
  	protected SimpleDateFormat initialValue() {
  	    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	    return s;
  	}
    };

    public static String formatTime(Calendar c) {
	return timeFormat.get().format(c.getTime());
    }

    public static Calendar now() {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(System.currentTimeMillis());
	return calendar;
    }
}

