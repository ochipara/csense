package edu.uiowa.csense.runtime.v4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.uiowa.csense.runtime.api.concurrent.IIdleLock;

public class ReentrantIdleLock implements IIdleLock {
    private final Lock _lock = new ReentrantLock();
    private final Condition _isSleeping = _lock.newCondition();
    private boolean _sleeps = false;

    @Override
    public void sleep(long nano) throws InterruptedException {
	_lock.lock();
	try {
	    _sleeps = true;
	    while (_sleeps == true) {
		_isSleeping.await(nano, TimeUnit.NANOSECONDS);
	    }
	} finally {
	    _lock.unlock();
	}
    }

    @Override
    public void sleep() throws InterruptedException {
	sleep(0);
    }

    @Override
    public void wakeup() {
	_lock.lock();
	try {
	    _sleeps = false;
	    _isSleeping.signalAll();
	} finally {
	    _lock.unlock();
	}
    }

    @Override
    public void stop() {	
    }

    @Override
    public void start() {	
    }

}
