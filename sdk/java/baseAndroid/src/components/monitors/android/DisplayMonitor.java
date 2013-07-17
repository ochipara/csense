package components.monitors.android;

import components.network.HTMLFormMessage;

import messages.TypeInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Task;

public class DisplayMonitor extends CSenseSource<DisplayMonitorMessage>{
    public final IOutPort<DisplayMonitorMessage> out = newOutputPort(this, "out");
;
    protected final Context context;
    private BroadcastReceiver broadcastReceiver;
    private boolean screenOn = false;
	    
    public DisplayMonitor(TypeInfo<DisplayMonitorMessage> type, Context context) throws CSenseException {
	super(type);

	broadcastReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
		synchronized(this) {
		    if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			screenOn = false;
		    } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			screenOn = true;
		    }
		}
		getScheduler().schedule(DisplayMonitor.this, asTask());
	    };
	};
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	intentFilter.addAction(Intent.ACTION_SCREEN_ON);
	context.registerReceiver(broadcastReceiver, intentFilter);
	this.context = context;
    }
    
    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	context.unregisterReceiver(broadcastReceiver);
    }
    
    @Override
    public void doEvent(Task t) throws CSenseException {
	DisplayMonitorMessage m = getNextMessageToWriteInto();
	if (m != null) {
	    if (screenOn) m.setScreenOn(1);
	    else m.setScreenOn(0);
	    out.push(m);
	} else {
	    error("The memory pool is empty");
	}
    }
}
