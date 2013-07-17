package components.monitors.android;

import java.util.Timer;
import java.util.TimerTask;

import compatibility.SystemInfo;
import components.network.HTMLFormMessage;

import messages.TypeInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Task;

public class BatteryMonitor extends CSenseSource<BatteryMonitorMessage> {
    public final IOutPort<BatteryMonitorMessage> out = newOutputPort(this, "out");

    // local data
    protected int batteryStatus;
    protected float batteryLevel;
    protected final Context context;
    protected final long period;

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

    // the timer 
    protected final Timer timer = new Timer();
    protected final IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private SystemInfo sysInfo;

    public BatteryMonitor(TypeInfo<BatteryMonitorMessage> type, final Context context, long periodMs) throws CSenseException {
	super(type);
	this.context = context;
	this.period = periodMs;
	
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
	context.registerReceiver(batteryMonitorReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	sysInfo = SystemInfo.getInstance();

	timer.scheduleAtFixedRate(new TimerTask() {
	    @Override
	    public void run() {
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		BatteryMonitor.this.saveInformation(batteryStatus);
	    }
	}, 1000, periodMs);
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
	getScheduler().schedule(BatteryMonitor.this, asTask());
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	context.unregisterReceiver(batteryMonitorReceiver);
    }

    @Override
    public void doEvent(Task t) throws CSenseException {
	BatteryMonitorMessage m = getNextMessageToWriteInto();
	if (m != null) {
	    m.setStatus(this.batteryStatus);
	    m.setLevel(this.batteryLevel);
	    out.push(m);
	} else {
	    error("The memory pool is empty");
	}
    }
}
