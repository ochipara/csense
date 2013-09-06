package edu.uiowa.csense.components.android.monitors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.compatibility.Utils;
import edu.uiowa.csense.runtime.types.JavaFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class DisplayMonitorMessage extends JavaFrame<DisplayMonitor> {
    private int screenOn;

    public DisplayMonitorMessage(FramePool pool, TypeInfo type) throws CSenseException {
	super(pool, type);
    }

    @Override
    public String toString() {
	if (screenOn == 0) {
	    return Utils.formatTime(Utils.now()) + " screen OFF\n";
	} else if (screenOn == 1) {
	    return Utils.formatTime(Utils.now()) + " screen ON\n";
	} else {
	    return Utils.formatTime(Utils.now()) + " screen ??\n";
	}  	 
      }
    
    @Override
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
