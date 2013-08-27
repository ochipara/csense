package edu.uiowa.csense.runtime.v4;

import edu.uiowa.csense.runtime.api.CSenseToolkit;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AndroidEventWakeup extends BroadcastReceiver{
    public static final String TAG = "scheduler";

    @Override
    public void onReceive(Context context, Intent intent) {
	String schedulerId = intent.getStringExtra(TAG);
	IScheduler scheduler = CSenseToolkit.getImplementation().getScheduler(schedulerId);
	IIdleLock lock = scheduler.getIdleLock();
	lock.wakeup();
    }
}
