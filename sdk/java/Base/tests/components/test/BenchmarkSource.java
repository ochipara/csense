package components.test;

import java.util.Properties;

import base.Statistics;

import api.CSenseErrors;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Task;

public class BenchmarkSource extends CSenseSource<BenchmarkMessage> {
    IOutPort<BenchmarkMessage> out = newOutputPort(this, "out");
    protected final long _delay;
    private final int _burst;

    protected long _skips = 0;
    protected long _drops = 0;
    protected long _success = 0;
    protected long _total = 0;

    public BenchmarkSource(long delay, int burst) throws CSenseException {
	super(BenchmarkMessage.newMessage());
	_delay = delay;
	_burst = burst;
    }

    protected boolean isTerminated() { return false; }

    public Statistics report() {
	Statistics stat = new Statistics(getName());
	stat.set("total", _total);
	stat.set("pushed", _success);
	return stat;
    }

    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	getScheduler().schedule(this, asTask());
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	info("total msg:", _total);
	info("push success:", _success);
	info("push failure:", _drops);
	info("skipped msg:", _skips);
    }

    @Override
    public void doEvent(Task t) throws CSenseException {
	for (int i = 0; i < _burst; i++) {
	    BenchmarkMessage m = getNextMessageToWriteInto();
	    if(m == null) {
		_skips++;
		break;			
	    }
	    m.setStartTime(System.nanoTime());
	    m.setCount(_success);
	    _total += 1;
	    switch(out.push(m)) {
	    case PUSH_SUCCESS:
		_success++; break;
	    case PUSH_DROP:
		_drops++;
		break;
	    default:
		// TODO what else?
	    }
	}

	if (_delay > 0) {
	    try {
		Thread.sleep(_delay);
	    } catch (InterruptedException e) {
		throw new CSenseException(CSenseErrors.INTERRUPTED_OPERATION);
	    }
	}

	if(isTerminated()) {
	    info("terminates the scheduler with", _success, "messages pushed and", _drops, "dropped");
	    getScheduler().stop();
	} else
	    getScheduler().schedule(this, asTask());
    }
}
