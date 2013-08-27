package edu.uiowa.csense.benchmarks.pc;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Task;
import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.api.bindings.Source;
import edu.uiowa.csense.runtime.types.TypeInfo;


public class BenchmarkSource<T extends Frame> extends Source<T> {
    OutputPort<T> out = newOutputPort(this, "out");
    protected final long _delay;
    protected final long _burst;

    protected long _skips = 0;
    protected long _drops = 0;
    protected long _success = 0;
    protected long _total = 0;
    protected long _maxCount = 0;
    
    protected long prev = -1;
    
    private TimerEvent event;

    public BenchmarkSource(TypeInfo<T> type, long delay, long burst) throws CSenseException {
	super(type);
	_delay = delay;
	_burst = burst;
    }

    protected boolean isTerminated() { return false; }

    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	_maxCount = 0;	
	event = new TimerEvent();
	getScheduler().scheduleAt(this, event , System.nanoTime() + _delay);
    }

    @Override
    public void onStop() throws CSenseException {
	//if (_pool != null) ((MessagePoolAtomic)_pool).stop();
	super.onStop();
	info("total msg:", _total);
	info("push success:", _success);
	info("push failure:", _drops);
	info("skipped msg:", _skips);
	info("max count:", _maxCount);	
    }

    @Override
    public void doEvent(Task t) throws CSenseException {
	if(isTerminated() == false) {
	    if (_delay > 0) {
		getScheduler().scheduleAt(this, event , ((TimerEvent)t).scheduledExecutionTime() + _delay);
	    } else {
		getScheduler().schedule(this, asTask());
	    }
	} else {
	    info("terminates the scheduler with", _success, "messages pushed and", _drops, "dropped");
	    getScheduler().stop();
	}
	
	try {
	    for (int i = 0; i < _burst; i++) {	    
		_total += 1;
		T m;

		//T m = getNextMessageToWriteInto();
		m = getNextMessageToWriteIntoAndBlock();
		if(m == null) {
		    _skips++;
		} else {
		    out.push(m);		    
		}	 
	    }
	} catch (InterruptedException e) {
	    return;
	}
    }

}
