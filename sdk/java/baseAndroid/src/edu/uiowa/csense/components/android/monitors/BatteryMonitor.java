package edu.uiowa.csense.components.android.monitors;

import java.util.concurrent.TimeUnit;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.compatibility.SystemInfo;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryMonitor extends edu.uiowa.csense.runtime.v4.CSenseSource<BatteryMonitorMessage> {
    public final OutputPort<BatteryMonitorMessage> out = newOutputPort(this, "out");

    // local data
    protected int batteryStatus;
    protected float batteryLevel;
    protected final Context context;
    protected final long period;
    
    // 
    protected final TimerEvent pollBatteryEvent = new TimerEvent();

    // the receiver
    class BatteryMonitorReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
	    BatteryMonitor.this.saveInformation(intent);
	}	
    };
    protected final BatteryMonitorReceiver batteryMonitorReceiver = new BatteryMonitorReceiver();
    protected final String chargeFile = "/sys/class/power_supply/battery/charge_counter";
    protected final String capacityFile = "/sys/class/power_supply/battery/capacity";

    protected final IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private SystemInfo sysInfo;

    public BatteryMonitor(TypeInfo type, final Context context, long periodMs) throws CSenseException {
	super(type);
	this.context = context;
	this.period = periodMs;
	
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	sysInfo = SystemInfo.getInstance();
	
    }

    public double getCapacity() {
	if(capacityFile == null) return -1.0;
	long cap = sysInfo.readLongFromFile(capacityFile);
	return cap;
    }


    protected void saveInformation(Intent intent) {
	batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);	
	batteryLevel = (float) getCapacity();
	
	Log.d("battery", Float.toString(batteryLevel));
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	context.unregisterReceiver(batteryMonitorReceiver);
    }

    @Override
    public void onEvent(Event t) throws CSenseException {
	Intent batteryStatus = context.registerReceiver(null, ifilter);
	BatteryMonitor.this.saveInformation(batteryStatus);

	BatteryMonitorMessage m = getNextMessageToWriteInto();
	if (m != null) {
	    m.setStatus(this.batteryStatus);
	    m.setLevel(this.batteryLevel);
	    out.push(m);
	} else {
	    error("The memory pool is empty");
	}
	getScheduler().schedule(this, pollBatteryEvent, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();
	getScheduler().schedule(BatteryMonitor.this, pollBatteryEvent, 10, TimeUnit.SECONDS);
    }
}
