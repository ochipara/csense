package base.concurrent;

import api.concurrent.IIdleLock;

public class MonitorIdleLock implements IIdleLock {
    private final Object lock = new Object();
    private volatile boolean wakeup;

    @Override
    public void sleep() throws InterruptedException {
	sleep(0);
    }

    @Override
    public void sleep(long timeout) throws InterruptedException {
	synchronized (lock) {
	    if(wakeup) {
		wakeup = false;
	    } else {
		//				sleep = true;
		//				while (sleep)
		
		long milli = timeout / 1000000;
		timeout = timeout - milli * 1000000;
		lock.wait(milli, (int) timeout);
		lock.wait(timeout);
	    }
	}	
    }

    @Override
    public void wakeup() {
	synchronized (lock) {
	    //			sleep = false;
	    lock.notifyAll();
	    wakeup = true;
	}
    }

    @Override
    public void stop() {	
    }

    @Override
    public void start() {	
    }
}
