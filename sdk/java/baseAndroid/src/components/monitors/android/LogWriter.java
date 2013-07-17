package components.monitors.android;

import messages.TypeInfo;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;

public class LogWriter extends CSenseSource<LogMessage> {
    public final IOutPort<LogMessage> out = newOutputPort(this, "out");

    public LogWriter() throws CSenseException {
	super(TypeInfo.newJavaMessage(LogMessage.class));
    }
    
    public synchronized void logMessage(final String msg) throws CSenseException {
	LogMessage m = getNextMessageToWriteInto();
	if (m != null) {
	    m.setMessage(msg);
	    out.push(m);
	}
    }
}
