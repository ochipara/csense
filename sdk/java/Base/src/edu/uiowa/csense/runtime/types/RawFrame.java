package edu.uiowa.csense.runtime.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uiowa.csense.profiler.Route;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.profile.IRoute;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.RawFrame;

/**
 * This is for objects sent between components. Messages carry information
 * between components. Every message is associated by a particular message pool.
 * No component is allowed to create a message.
 * 
 * @author Austin, Farley
 * 
 */
public class RawFrame implements Frame {
    private static final String TAG = "RawFrame";
    protected final FramePool pool;
    protected final Frame parent;
    protected final TypeInfo type;

    protected final ByteBuffer buffer;
    protected final Map<Integer, List<Frame>> viewMap = new HashMap<Integer, List<Frame>>();
    
    // fields whose access must be synchronized
    // these fields are shared across potentially multiple threads
    protected int refs = 1;    
    protected RawFrame frame;
    private Route route = null;
    private boolean eof = false;
    private int id = -1;


    public RawFrame(FramePool pool, TypeInfo type, Frame parent) {	
	this.pool = pool;
	this.parent = parent;
	this.type = type;
	refs = 1;
	eof = false;
	buffer = type.isDirect() ? ByteBuffer.allocateDirect(type.getNumBytes()) : ByteBuffer.allocate(type.getNumBytes());
	buffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public RawFrame(FramePool pool, TypeInfo type) {
	this(pool, type, null);
    }

    public final ByteBuffer getBuffer() {
	return buffer;
    }

    @Override
    public synchronized void initialize() {
	refs = 1;
	eof = false;
    }

    /**
     * The methods incrementReference/decrementReference/free must provide
     * concurrent access to the _ref The ref variable may be accessed from
     * multiple threads, when the stream is split
     * 
     */
    @Override
    public synchronized void incrementReference() {
	if (parent == null) {
	    refs++;
	} else {
	    parent.incrementReference();	    
	}
    }

    @Override
    public synchronized void incrementReference(int count) {
	if (parent == null) {
	    refs += count;
	} else {
	    parent.incrementReference(count);
	} 
    }

    @Override
    public synchronized void decrementReference() {
	if (parent == null) {
	    refs--;
	    if (refs < 0) {
		throw new IllegalStateException();
	    }
	    if (refs == 0) free();
	   
	} else {
	    parent.decrementReference();
	}
    }

    @Override
    public synchronized void free() {
	if (parent == null) {
	    pool.put(this);
	}
    }

    @Override
    public synchronized void drop() {
	if(route != null) {
	    route.clear();
	}
	Log.w(TAG, "drop");
	decrementReference();
    }

    @Override
    public synchronized int getReference() {
	return refs;
    }

    @Override
    public synchronized IRoute getRoute() {
	return route;
    }

    @Override
    public synchronized void eof() {
	eof = true;

    }

    @Override
    public synchronized boolean isEof() {
	return eof;
    }

    public Frame getParent() {
	return parent;
    }

    @Override
    public void setPoolId(int id) throws CSenseException {
	if (this.id == -1) this.id = id;
	else throw new CSenseException("Pool id cannot be modified once they are set");
    }
    
    @Override
    public int getPoolId() {
	return this.id;

    }
    
    @Override
    public int getId() {
	return hashCode();
    }
    
    @Override
    public Frame[] split(int numFrames) {
	throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
	return "C: " + buffer.capacity() + " P:" + buffer.position() + " L:" + buffer.limit() + " R:" + refs;
    }

}