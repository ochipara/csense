package components.test;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class BenchmarkWorker extends CSenseSource<BenchmarkMessage> {
    InputPort<BenchmarkMessage> in = newInputPort(this, "in");
    OutputPort<BenchmarkMessage> out = newOutputPort(this, "out");

    protected final long _delay;
    protected long _drops = 0;
    protected long _success = 0;
    protected long _total = 0;
    protected long _expectedCount = 0;

    public BenchmarkWorker(long delay) throws CSenseException {
	super(BenchmarkMessage.newMessage());
	_delay = delay;
    }

    protected boolean isTerminated() {
	return false;
    }

    @Override
    public void onStop() {
	// info("successes", _success);
	// info("drops", _drops);
    }

    @Override
    public void onInput() throws CSenseException {
	BenchmarkMessage m = in.getFrame();
	if (m.getCount() != _expectedCount) {
	    error("message count does not match", _expectedCount, "but got",
		    m.getCount());
	    _expectedCount = m.getCount() + 1;
	} else
	    _expectedCount++;

	if (_delay > 0) {
	    try {
		Thread.sleep(_delay);
	    } catch (InterruptedException e) {
		throw new CSenseException(CSenseErrors.INTERRUPTED_OPERATION);
	    }
	}

	m.setEndTime(System.nanoTime());
	_total += 1;
	switch (out.push(m)) {
	case PUSH_SUCCESS:
	    _success++;
	    break;
	case PUSH_DROP:
	    _drops++;
	    break;
	default:
	    // TODO what else?
	}
    }
}
