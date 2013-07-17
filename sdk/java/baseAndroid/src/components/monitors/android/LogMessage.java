package components.monitors.android;

import api.CSenseException;
import api.IMessagePool;
import messages.RawMessage;
import messages.TypeInfo;

public class LogMessage extends RawMessage {
    String message;
    
    public LogMessage(IMessagePool<LogMessage> pool, TypeInfo<LogMessage> type) throws CSenseException {
	super(pool, type);	
    }
    
    @Override
    public void initialize() {
	super.initialize();
	message = "";
    }
    
    public String toString() {
	return message;
    }
    
    public synchronized void setMessage(String msg) {
	message = msg;
    }
    
    

}
