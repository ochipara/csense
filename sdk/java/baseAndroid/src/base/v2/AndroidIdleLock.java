package base.v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import api.CSense;
import api.concurrent.IIdleLock;

/**
 * This is a specialized IdleLock implementation for Android.
 * Aside from doing the usual locking, it also manages sleep locks. 
 * This will the cpu go to sleep as desired by the OS when the locks are released
 * 
 * @author ochipara
 *
 */
public class AndroidIdleLock implements IIdleLock {
    private static long SLEEP_THRESHOLD = 5000000000L; //ns
    
    protected final Context context;
    protected final BroadcastReceiver receiver;
    protected final PowerManager.WakeLock cpuLock;    
    protected final AlarmManager am;
    private final PendingIntent alarm;
    private final String action;

    protected final String scheduler;
    private boolean wakeup;
    private boolean held;

    public AndroidIdleLock(Context context, String scheduler) {
	super();
	this.context = context;
	this.scheduler = scheduler;
	this.action = "edu.uiowa.csense.scheduler.WAKEUP-" + scheduler;
	this.receiver = new BroadcastReceiver() {
	    public static final String TAG = "scheduler";

	    @Override
	    public void onReceive(Context context, Intent intent) {
		String schedulerId = intent.getStringExtra(TAG);
		CSense.getImplementation().getScheduler(schedulerId).getIdleLock().wakeup();
//		Log.d(schedulerId, "woken up on alarm");
	    }
	};
	
	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);	
	cpuLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, scheduler + "-APL");
	cpuLock.setReferenceCounted(false);

	am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	Intent intent = new Intent(action);
	intent.putExtra(AndroidEventWakeup.TAG, scheduler);
	alarm = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void sleep() throws InterruptedException {
	sleep(0);
    }

    @Override
    public void sleep(long nano) throws InterruptedException {
	long ms = nano / 1000000;
	int ns = (int)(nano - ms * 1000000);
	long future = System.currentTimeMillis() + ms;	
	synchronized(this) {
	    if(wakeup) {
		wakeup = false;
		return;
	    }
	    	    
	    if(nano > SLEEP_THRESHOLD) {
//		Log.d(scheduler, "set alarm to release wake lock", System.currentTimeMillis(), future);
		am.set(AlarmManager.RTC_WAKEUP, future, alarm); 
		release();
	    } else if(nano == 0) 
		release();
	    
	    try {
//		Log.d(scheduler, "wait", ms, ns);
		wait(ms, ns);
	    } finally {
		acquire();
		am.cancel(alarm);
		wakeup = false;
	    }
	}	
    }

    @Override
    public synchronized void wakeup() {
	if(wakeup) return;
	wakeup = true;
	acquire();
	notify();
    }

    @Override
    public void start() {
	acquire();
	context.registerReceiver(receiver, new IntentFilter(action));
    }
    
    @Override
    public void stop() {
	context.unregisterReceiver(receiver);
	release();
    }
    
    private void acquire() {
	if(!held) {
	    cpuLock.acquire();
	    held = true;
	}
    }
    
    private void release() {
	if(held) {
	    held = false;
	    cpuLock.release();
	}
    }
}
