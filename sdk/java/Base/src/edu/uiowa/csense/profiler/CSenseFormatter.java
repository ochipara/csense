package edu.uiowa.csense.profiler;

//import java.util.Formatter;
import java.util.logging.*;

/**
 * This is used for custom formatting our .log files!
 * 
 * @author Austin
 * 
 */
public class CSenseFormatter extends Formatter {

    // This method is called for every log records
    @Override
    public String format(LogRecord rec) {
	StringBuffer buf = new StringBuffer(1000);
	// Bold any levels >= WARNING
	// if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
	// buf.append("<b>");
	// buf.append(rec.getLevel());
	// buf.append("</b>");
	// } else {
	// buf.append(rec.getLevel());
	// }
	// buf.append(' ');
	buf.append(rec.getMillis());
	buf.append(' ');
	buf.append(formatMessage(rec));
	buf.append('\n');
	return buf.toString();
    }

    // This method is called just after the handler using this
    // formatter is created
    @Override
    public String getHead(Handler h) {
	return super.getHead(h);
    }

    // This method is called just after the handler using this
    // formatter is closed
    @Override
    public String getTail(Handler h) {
	return super.getTail(h);
    }

}
