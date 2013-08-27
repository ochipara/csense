package components.monitors.android;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.compatibility.Utils;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class BatteryMonitorMessage extends Frame {
    protected int status = -1;
    protected float level = -1.0f; // percent
    protected long timestamp = 0;
    
    public BatteryMonitorMessage(FramePool<BatteryMonitorMessage> pool, TypeInfo<BatteryMonitorMessage> type) throws CSenseException {
	super(pool, type);
    }

    @Override
    public void initialize() {
	super.initialize();
	status = -1;
	level = -1.0f;
    }
    
    public String toString() {
	return Utils.formatTime(Utils.now()) + " level=" + level + "% status=" + status + "\n"; 
    }


    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }
}
