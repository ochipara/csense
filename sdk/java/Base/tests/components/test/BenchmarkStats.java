package components.test;

import java.text.DecimalFormat;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;

public class BenchmarkStats extends CSenseComponent {
    IInPort<BenchmarkMessage> in;
    IOutPort<BenchmarkMessage> out;

    protected final DecimalFormat dformat = new DecimalFormat("#.#######");
    protected long _drops = 0;
    protected long _success = 0;
    protected long _total = 0;

    protected long _window = 100000;
    protected long _start = 0;
    protected long _end = 0;
    protected long _latency = 0;
    protected long _expectedCount = 0;

    public BenchmarkStats() throws CSenseException {
	in = newInputPort(this, "in");
	out = newOutputPort(this, "out");
    }

    public long upTime() {
	return _end - _start;
    }

    /**
     * Returns the number of messages pushed successfully per second.
     * 
     * @return message throughput in msgs/s
     */
    public double getMessageThroughput() {
	return _success * 1000000000.0 / upTime();
    }

    /**
     * Returns the average message passing delay from generation to be freed.
     * 
     * @return the message passing delay in nano seconds
     */
    public double getAverageMessageLatency() {
	return _latency * 1.0 / _total;
    }

    protected boolean isTerminated() {
	return false;
    }

    @Override
    public void onStart() {
	_start = System.nanoTime();
    }

    @Override
    public void onStop() {
	// info("successes", _success);
	// info("drops", _drops);
    }

    @Override
    public void doInput() throws CSenseException {
	BenchmarkMessage m = in.getMessage();
	m.setEndTime(_end = System.nanoTime());
	if (m.getCount() != _expectedCount) {
	    error("counts do not match", _expectedCount, "but got",
		    m.getCount());
	    _expectedCount = m.getCount() + 1;
	} else
	    _expectedCount++;

	_latency += m.getEndTime() - m.getStartTime();
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

	if (isTerminated()) {
	    info("terminates the scheduler with", _success, "messages pushed and", _drops, "dropped");
	    getScheduler().stop();
	}
    }
}
