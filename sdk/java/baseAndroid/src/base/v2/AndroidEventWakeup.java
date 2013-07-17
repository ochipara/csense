package base.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import api.CSense;
import api.IScheduler;
import api.concurrent.IIdleLock;

public class AndroidEventWakeup extends BroadcastReceiver{
    public static final String TAG = "scheduler";

    @Override
    public void onReceive(Context context, Intent intent) {
	String schedulerId = intent.getStringExtra(TAG);
	IScheduler scheduler = CSense.getImplementation().getScheduler(schedulerId);
	IIdleLock lock = scheduler.getIdleLock();
	lock.wakeup();
    }
}
