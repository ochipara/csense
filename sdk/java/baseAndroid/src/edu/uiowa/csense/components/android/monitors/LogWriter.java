package edu.uiowa.csense.components.android.monitors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class LogWriter extends CSenseSource<LogMessage> {
    public final OutputPort<LogMessage> out = newOutputPort(this, "out");

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
