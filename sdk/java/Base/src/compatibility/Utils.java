package compatibility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    protected static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatTime(Calendar c) {
	return timeFormat.format(c.getTime());
    }

    public static Calendar now() {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(System.currentTimeMillis());
	return calendar;
    }
}

