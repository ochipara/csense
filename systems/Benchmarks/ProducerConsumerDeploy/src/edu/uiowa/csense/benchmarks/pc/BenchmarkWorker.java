package edu.uiowa.csense.benchmarks.pc;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.bindings.Source;
import edu.uiowa.csense.runtime.types.TypeInfo;


public class BenchmarkWorker<T extends Frame> extends Source<T> {
    InputPort<T> in = newInputPort(this, "dataIn");
    OutputPort<T> out = newOutputPort(this, "dataOut");

    protected final long _delay;
    protected long _drops = 0;
    protected long _success = 0;
    protected long _total = 0;
    protected long _received = 0;

    public BenchmarkWorker(TypeInfo<T> type, long delay) throws CSenseException {
	super(type);
	_delay = delay;
    }

    protected boolean isTerminated() {
	return false;
    }

    @Override
    public void onStop() {
	info("received", _received);
	info("successes", _success);
	info("drops", _drops);
    }

    @Override
    public void onInput() throws CSenseException {
	T m = in.getFrame();
	_received = _received + 1;
	
	if (_delay > 0) {
	    try {
		Thread.sleep(_delay);
	    } catch (InterruptedException e) {
		throw new CSenseException(CSenseError.INTERRUPTED_OPERATION);
	    }
	}
	
	_total += 1;
	out.push(m);
	_success++;	
    }
}
