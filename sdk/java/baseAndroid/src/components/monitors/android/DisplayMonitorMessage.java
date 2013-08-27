package components.monitors.android;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.compatibility.Utils;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class DisplayMonitorMessage extends Frame {
    private int screenOn;

    public DisplayMonitorMessage(FramePool<DisplayMonitorMessage> pool, TypeInfo<DisplayMonitorMessage> type) throws CSenseException {
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
