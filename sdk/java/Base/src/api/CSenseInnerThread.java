package api;

import base.Debug;

public class CSenseInnerThread extends Thread {
    public CSenseInnerThread(String threadName) {
	super(threadName);
    }
    
    public CSenseInnerThread(Runnable runnable, String threadName) {
	super(runnable, threadName);
    }
    
    public void doRun() { super.run(); }
    
    @Override
    public final void run() {
	Debug.logThreadStart();
	onStart();
	doRun();
	onStop();
	Debug.logThreadStop();
    }
    
    public void onStart() {
    }
    
    public void onStop() {
    }
}
