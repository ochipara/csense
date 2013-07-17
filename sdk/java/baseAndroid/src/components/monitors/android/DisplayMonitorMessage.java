package components.monitors.android;

import messages.TypeInfo;
import api.CSenseException;
import api.IMessagePool;
import api.Message;
import compatibility.Utils;

public class DisplayMonitorMessage extends Message {
    private int screenOn;

    public DisplayMonitorMessage(IMessagePool<DisplayMonitorMessage> pool, TypeInfo<DisplayMonitorMessage> type) throws CSenseException {
	super(pool, type);
    }

    public String toString() {
	if (screenOn == 0) {
	    return Utils.formatTime(Utils.now()) + " screen OFF\n";
	} else if (screenOn == 1) {
	    return Utils.formatTime(Utils.now()) + " screen ON\n";
	} else {
	    return Utils.formatTime(Utils.now()) + " screen ??\n";
	}  	 
      }
    
    public void initialize() {
	super.initialize();
	screenOn = -1;
    }

    public int getScreenOn() {
	return screenOn;
    }

    public void setScreenOn(int screenOn) {
	this.screenOn = screenOn;
    }
}
