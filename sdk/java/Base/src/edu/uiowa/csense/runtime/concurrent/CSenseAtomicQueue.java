package edu.uiowa.csense.runtime.concurrent;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Efficient implementation of blocking queue. In contrast to the default implementation of BlockingQueue on android,
 * this implementation does not create any objects. This will result in better performance, particularly at high data rates.
 * 
 * @author ochipara
 *
 * @param <T>
 */
public class CSenseAtomicQueue<Telem, T extends Queue<Telem>> {
    protected final T _list;
    protected final int _capacity;

    //protected final Lock _lock = new ReentrantLock();
    protected static final int UNLOCKED = 200;
    protected static final int LOCKED = 300;
    protected final AtomicInteger _lock = new AtomicInteger(UNLOCKED);
    protected volatile int _waitOnEmpty = 0; //it is a counter of the number of elements in queue

    protected final Object _empty = new Object();
    protected boolean _emptySignaled = false;

    public CSenseAtomicQueue(T list, int capacity) {
	_list = list;
	_capacity = capacity;
    }

    public void put(Telem element) throws InterruptedException{
	offer(element);
    }

    public boolean offer(Telem element) {
	if (element == null) throw new IllegalArgumentException("element cannot be null");

	while(_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}


	_list.add(element);
	_waitOnEmpty += 1;
	if (_waitOnEmpty == 1) {
	    synchronized (_empty) {
		_emptySignaled = true;
		_empty.notify();
	    }
	}

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}

	return true;
    }

    public Telem take() throws InterruptedException {
	while (true) {
	    while(_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
		// spin -- hopefully not too long
	    }

	    if (_waitOnEmpty > 0) {
		// we got lucky, we will get the result and return
		Telem elem = _list.poll();
		_waitOnEmpty -= 1;

		if (_waitOnEmpty < 0) {
		    throw new IllegalStateException("This state cannot be reached");
		}

		// unlock
		if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
		    throw new IllegalStateException("This state cannot be reached");
		}

		return elem;
	    } else {
		// release the lock for other threads to deposit elements
		if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
		    throw new IllegalStateException("This state cannot be reached");
		}	    	   

		// wait for a notify
		if (_emptySignaled == false) {
		    while (_waitOnEmpty == 0) {
			synchronized(_empty) {
			    if (_waitOnEmpty == 0) {
				_empty.wait();
				_emptySignaled = false;
			    }
			}
		    }
		}
		_emptySignaled = false;
	    }
	}	
    }

    public Telem poll() {
	while(_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}

	Telem element = null;

	if (_list.size() > 0) {	    
	    element = _list.poll();	    
	    _waitOnEmpty -= 1;
	}

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}

	return element;
    }

    public void clear() {
	while(_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}

	_waitOnEmpty = 0;
	_list.clear();

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}
    }

    public int size() {
	while (_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}

	int size = _list.size();
	if (_waitOnEmpty != size) {
	    throw new IllegalAccessError("_waitOnEmpty should be equal to the size of the queue"); 
	}

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}

	return size;
    }

    public boolean contains(T element) {
	while (_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}

	boolean r = _list.contains(element);

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}	
	return r;
    }

    public boolean isEmpty() {
	while (_lock.compareAndSet(UNLOCKED, LOCKED) == false) {
	    // spin -- hopefully not too long
	}

	int size = _list.size();
	if (_waitOnEmpty != size) {
	    throw new IllegalAccessError("_waitOnEmpty should be equal to the size of the queue"); 
	}

	if (_lock.compareAndSet(LOCKED, UNLOCKED) == false) {
	    throw new IllegalStateException("This state cannot be reached");
	}

	return size == 0;
    }
}
