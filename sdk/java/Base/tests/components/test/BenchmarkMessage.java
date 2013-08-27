package components.test;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class BenchmarkMessage extends Frame {
    protected long startTime = 0;
    protected long endTime = 0;
    protected long count = 0;

    public long getCount() {
	return count;
    }

    public void setCount(long count) {
	this.count = count;
    }

    public BenchmarkMessage(FramePool<? extends Frame> pool,
	    TypeInfo<? extends Frame> type) throws CSenseException {
	super(pool, type);
    }

    public static TypeInfo<BenchmarkMessage> newMessage() {
	return new TypeInfo<BenchmarkMessage>(BenchmarkMessage.class);
    }

    @Override
    public void initialize() {
	startTime = 0;
	endTime = 0;
	count = 0;
	super.initialize();
    }

    public void setStartTime(long startTime) {
	this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
	this.endTime = endTime;
    }

    public long getEndTime() {
	return endTime;
    }

    public long getStartTime() {
	return startTime;
    }

}
