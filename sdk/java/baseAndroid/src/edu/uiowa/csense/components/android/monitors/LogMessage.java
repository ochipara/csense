package edu.uiowa.csense.components.android.monitors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class LogMessage extends RawFrame {
    String message;
    
    public LogMessage(FramePool<LogMessage> pool, TypeInfo<LogMessage> type) throws CSenseException {
	super(pool, type);	
    }
    
    @Override
    public void initialize() {
	super.initialize();
	message = "";
    }
    
    @Override
    public String toString() {
	return message;
    }
    
    public synchronized void setMessage(String msg) {
	message = msg;
    }
    
    

}
