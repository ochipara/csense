package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import compatibility.Log;
import compatibility.ThreadCPUUsage;

import api.CSense;
import api.CSenseRuntimeException;
import api.IComponent;
import api.IMessage;

public class Debug {    
    private static class Trace {
	// entry: tid, eid, cid, mid, stats (clock, thread, utime, stime)
	public static volatile boolean _loggable;
	public static final int ENTRY_SIZE = 8 + 4 * 3 + 4 * 4;	
	private static long _startClock;
	private long _startThreadTime;
	private long _startUserTime;
	private long _startSystemTime;
	private final String TAG;
	private final long TID;	

	private File _file;
	private FileChannel _channel;
	private int _traceSize;

	private StringBuilder _header;
	private ByteBuffer _traceBuffer;
	private long _savingTime;
	private int _saved;
	private int _totalSaved;
	
	public Trace(String traceName, int traceSize, int traceBufferSize) throws FileNotFoundException {    
	    ThreadCPUUsage u = ThreadCPUUsage.getCPUUsage();
	    _startThreadTime = u.getThreadTime();
	    _startUserTime = u.getThreadUserTime();
	    _startSystemTime = u.getThreadSystemTime();
	    
	    _file = new File(traceName);
	    _traceSize = traceSize;
	    _channel = new FileOutputStream(_file).getChannel();
	    _header = new StringBuilder(1024);
	    _traceBuffer = ByteBuffer.allocate(traceBufferSize); // Big-endian
	    _traceBuffer.order(ByteOrder.LITTLE_ENDIAN);
	    TAG = Thread.currentThread().getName();
	    TID = Thread.currentThread().getId();
	    
	    Log.i(TAG, String.format("trace begins at (%d, %d, %d, %d)", _startClock, _startThreadTime, _startUserTime, _startSystemTime));
	}
	
	public String getName() {
	    return _file.getName();
	}

	public int getSize() {
	    return _traceSize;
	}

	public int total() {
	    return _totalSaved;
	}
	
	public long savingTime() {
	    return _savingTime;
	}
	
	public boolean isLoggable() {
	    return _loggable && _traceSize >= _traceBuffer.position() + ENTRY_SIZE;
	}

	private void save() throws IOException {
	    if(_header.length() == 0) {
		_header.append("components=").append(CSense.components.size()).append("\n");
		for(Map.Entry<String, IComponent> entry: CSense.components.entrySet()) {
		    _header.append(entry.getKey()).append("=").append(entry.getValue().getId()).append("\n");
		}

		_header.append("events=").append(_events.size()).append("\n");
		for(int i = 0; i < _events.size(); i++)
		    _header.append(_events.get(i)).append("=").append(i).append("\n");
		
		ByteBuffer _headerBuffer = ByteBuffer.allocate(_header.length());
		_headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < _header.length(); i++)
		    _headerBuffer.put((byte)_header.charAt(i));

		_headerBuffer.flip();
		_channel.write(_headerBuffer);
		_traceSize -= _header.length();
	    }

	    _traceBuffer.flip();
	    _traceSize -= _traceBuffer.remaining();
	    while(_traceBuffer.hasRemaining())  _channel.write(_traceBuffer);
	    _traceBuffer.clear();	
	}

	private void dump() {
	    long time = System.nanoTime();
	    try {
		save();
	    } catch (IOException e) {
		throw new CSenseRuntimeException("failed to save traces to " + getName(), e);
	    }
	    time = System.nanoTime() - time;
	    _savingTime += time;
	    _totalSaved += _saved;
	    Log.i(TAG, String.format("%.2fms taken to save %d trace entries", time / 1000000.0, _saved));
	    _saved = 0;
	}

	public void close() throws IOException {
	    dump();
	    _channel.close();
	    if(_totalSaved == 0 && _file.delete())
		Log.w(TAG, "no traces are saved, delete", _file);
	    
	    ThreadCPUUsage u = ThreadCPUUsage.getCPUUsage();
	    Log.i(TAG, String.format("trace ends at (%d, %d) for %.2fs", u.getRealTime(), u.getThreadTime(), (u.getRealTime() - _startClock) / 1000000000.0));
	}

	public void log(int eid) {
	    log(eid, -1, -1);
	}
	
	public void log(int eid, int cid) {
	    log(eid, cid, -1);
	}
	
	public void log(int eid, int cid, int mid) {
	    if(!isLoggable()) {
		_loggable = false;
		return;
	    }
	    
	    boolean dumped = false;
	    ThreadCPUUsage u = ThreadCPUUsage.getCPUUsage();
	    if(_traceBuffer.remaining() < ENTRY_SIZE) {
		dump();
		dumped = true;
		Log.d(TAG, "trace buffer dumped,", _traceSize, "bytes of trace file remains");
	    }

	    // entry: tid, eid, cid, mid, stats (clock, thread, utime, stime)    
	    _traceBuffer.putLong(TID);
	    _traceBuffer.putInt(eid);
	    _traceBuffer.putInt(cid);
	    _traceBuffer.putInt(mid);
	    _traceBuffer.putInt((int)((u.getRealTime() - _startClock) / 1000));
	    _traceBuffer.putInt((int)((u.getThreadTime() - _startThreadTime) / 1000));
	    _traceBuffer.putInt((int)((u.getThreadUserTime() - _startUserTime) / 1000));
	    _traceBuffer.putInt((int)((u.getThreadSystemTime() - _startSystemTime) / 1000));
	    _saved++;	    
	    if(dumped) log(TRACE_FLUSH, cid, mid);
	}	
    }
    
    private static List<Trace> _traces;
    private static ThreadLocal<Trace> _storage;
    private static final List<String> _events = new ArrayList<String>();
    public static final int TRACE_THREAD_START = addTraceEvent("THREAD_START");
    public static final int TRACE_THREAD_STOP = addTraceEvent("THREAD_STOP");
    public static final int TRACE_THREAD_SLEEP = addTraceEvent("THREAD_SLEEP");
    public static final int TRACE_THREAD_WAKEUP = addTraceEvent("THREAD_WAKEUP");
    public static final int TRACE_SCHED_TASK_EXEC = addTraceEvent("TASK_EXEC");
    public static final int TRACE_SCHED_TASK_RETURN = addTraceEvent("TASK_RET");
    public static final int TRACE_SCHED_TIMER_EVENT_EXEC = addTraceEvent("TIMER_EXEC");
    public static final int TRACE_SCHED_TIMER_EVENT_RETURN = addTraceEvent("TIMER_RET");	     
    public static final int TRACE_MSG_SRC = addTraceEvent("MSG_SRC");
    public static final int TRACE_MSG_PUSH = addTraceEvent("MSG_PUSH");
    public static final int TRACE_MSG_PUSH_RETURN = addTraceEvent("MSG_PUSHR");
    public static final int TRACE_MSG_INPUT = addTraceEvent("MSG_INPUT");
    public static final int TRACE_MSG_RETURN = addTraceEvent("MSG_RET");
    public static final int TRACE_CMP_READY = addTraceEvent("CMP_READY");
    public static final int TRACE_FLUSH = addTraceEvent("TRACE_FLUSH");

    public static synchronized int addTraceEvent(String description) {
	if(_events.contains(description))
	    return _events.indexOf(description);
	else {
	    _events.add(description);
	    return _events.size() - 1;
	}
    }
    
    public static synchronized String getTraceEventDescription(int i) {
	return i < 0 || i >= _events.size() ? null : _events.get(i);
    }
    
    public static boolean isTracing() {
	return _storage != null;
    }
    
    public static void startTracing(String tracePath) {
	startTracing(new File(tracePath));
    }
    
    public static void startTracing(File tracePath) {
	startTracing(tracePath, 4 * 1024 * 1024, 512 * 1024);
    }
    
    public static void startTracing(String tracePath, int traceSize) {
	startTracing(new File(tracePath), traceSize);
    }
    
    public static void startTracing(File tracePath, int traceSize) {
	startTracing(tracePath, traceSize, 512 * 1024);
    }
    
    /**
     * Starts per thread tracing. This is assumed to be called by the main thread without race.
     * @param tracePath the path to save the trace file
     * @param traceSize the maximum trace file size
     */
    public static void startTracing(final File tracePath, final int traceSize, final int traceBufferSize) {
	if(isTracing()) return;
	_traces = new ArrayList<Trace>();
	_storage = new ThreadLocal<Trace>() {
	    @Override
	    public Trace initialValue() {
		String traceName = tracePath + File.separator + Thread.currentThread().getName() + ".trace";
		Trace trace;
		try {
		    trace = new Trace(traceName, traceSize, traceBufferSize);
		} catch (FileNotFoundException e) {
		    throw new CSenseRuntimeException("failed to create a trace file: " +  traceName, e);
		}
		synchronized(_traces) {
		    _traces.add(trace);
		}
		Log.d(Thread.currentThread().getName(), String.format("create trace file '%s' of size: %.2fMB", traceName, traceSize / 1024f / 1024));
		return trace;
	    }
	};
	ThreadCPUUsage u = ThreadCPUUsage.getCPUUsage();
	Trace._startClock = u.getRealTime();
	Trace._loggable = true;
    }

    /**
     * Stops per thread tracing. This is assumed to be called by the main thread without race.
     */
    public static void stopTracing() {
	if(!isTracing()) return;
	for(Trace trace: _traces) {
	    try {
		trace.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	_traces.clear();
	_storage.remove();
	_traces = null;
	_storage = null;
    }
    
    public static void log(int eid) {
	if(!isTracing()) return;
	_storage.get().log(eid);
    }
    
    public static void log(int eid, IComponent c) {
	if(!isTracing()) return;
	_storage.get().log(eid, c.getId());
    }
    
    public static void log(int eid, IComponent c, int mid) {
	if(!isTracing()) return;
	_storage.get().log(eid, c.getId(), mid);
    }
    
    public static void log(int eid, IComponent c, IMessage m) {
	if(!isTracing()) return;
	_storage.get().log(eid, c.getId(), m.getId());
    }
    
    public static void logMessageSource(IComponent c, IMessage m) {
	log(TRACE_MSG_SRC, c, m);
    }
    
    public static void logMessagePush(IComponent c, IMessage m) {
	log(TRACE_MSG_PUSH, c, m);
    }
    
    public static void logMessageInput(IComponent c, IMessage m) {
	log(TRACE_MSG_INPUT, c, m);
    }
    
    public static void logMessagePushReturn(IComponent c, IMessage m) {
	log(TRACE_MSG_PUSH_RETURN, c, m);
    }
    
    public static void logMessagePushReturn(IComponent c, int mid) {
	log(TRACE_MSG_PUSH_RETURN, c, mid);
    }
    
    public static void logMessageReturn(IComponent c, IMessage m) {
	log(TRACE_MSG_RETURN, c, m);
    }    
    
    public static void logThreadStart() {
	log(TRACE_THREAD_START);
    }

    public static void logThreadStop() {
	log(TRACE_THREAD_STOP);
    }

    public static void logThreadSleep() {
	log(TRACE_THREAD_SLEEP);
    }

    public static void logThreadWakeup() {
	log(TRACE_THREAD_WAKEUP);
    }
    
    public static void logScheduledTaskExec(IComponent c) {
	log(TRACE_SCHED_TASK_EXEC, c);
    }
    
    public static void logScheduledTaskReturn(IComponent c) {
	log(TRACE_SCHED_TASK_RETURN, c);
    }
    
    public static void logScheduledTimerEventExec(IComponent c) {
	log(TRACE_SCHED_TIMER_EVENT_EXEC, c);
    }
    
    public static void logScheduledTimerEventReturn(IComponent c) {
	log(TRACE_SCHED_TIMER_EVENT_RETURN, c);
    }
    
    public static void logComponentReady(IComponent c) {
	log(TRACE_CMP_READY, c);
    } 
}