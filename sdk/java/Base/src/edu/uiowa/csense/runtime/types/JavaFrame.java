package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.profile.IRoute;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.JavaFrame;

/**
 * This is for objects sent between components. Messages carry information
 * between components. Every message is associated by a particular message pool.
 * No component is allowed to create a message.
 * 
 * @author Austin, Farley
 * 
 */
public class JavaFrame<T> implements Frame {
    private static final String TAG = "RawFrame";
    protected final FramePool pool;
    protected final TypeInfo type;

    protected T data;

    // fields whose access must be synchronized
    // these fields are shared across potentially multiple threads
    protected int refs = 1;    
    protected boolean eof = false;
    protected int id = -1;

    public JavaFrame(FramePool pool, TypeInfo type) {
	this.pool = pool;
	this.type = type;
	refs = 1;
    }

    @Override
    public synchronized void initialize() {
	refs = 1;
	eof = false;
	data = null;
    }

    /**
     * The methods incrementReference/decrementReference/free must provide
     * concurrent access to the _ref The ref variable may be accessed from
     * multiple threads, when the stream is split
     * 
     */
    @Override
    public synchronized void incrementReference() {
	refs++;
    }

    @Override
    public synchronized void incrementReference(int count) {
	refs += count;
    }

    @Override
    public synchronized void decrementReference() {
	refs--;
	if (refs < 0) {
	    throw new IllegalStateException();
	}
	if (refs == 0) free();
    }

    @Override
    public synchronized void free() {
	pool.put(this);

    }

    @Override
    public synchronized void drop() {
	Log.w(TAG, "drop");
	decrementReference();
    }

    @Override
    public synchronized int getReference() {
	return refs;
    }

    @Override
    public synchronized IRoute getRoute() {
	throw new UnsupportedOperationException();
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
	throw new UnsupportedOperationException("JavaFrames do not have parents");
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
    public Frame[] window(int numFrames) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
	return "JavaFrame ";
    }

    public T unbox() {
	return data;
    }

    public void setValue(T data) {
	this.data = data;
    }

    @Override
    public Frame[] window(int splits, int increment) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Frame slice(int start, int end) {
	throw new UnsupportedOperationException();
    }

}