package components.monitors.android;

import compatibility.Utils;

import api.CSenseException;
import api.IMessagePool;
import api.Message;
import messages.TypeInfo;

public class BatteryMonitorMessage extends Message {
    protected int status = -1;
    protected float level = -1.0f; // percent
    protected long timestamp = 0;
    
    public BatteryMonitorMessage(IMessagePool<BatteryMonitorMessage> pool, TypeInfo<BatteryMonitorMessage> type) throws CSenseException {
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
