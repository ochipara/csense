package edu.uiowa.csense.runtime.types;


import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class ShortVector extends Vector<Short> {
    private final ShortBuffer _shortBuffer; 

    /**
     * 
     */
    public ShortVector(FramePool pool, TypeInfo type)
	    throws CSenseException {
	super(pool, type);
	_shortBuffer = buffer.asShortBuffer();
    }

    public ShortBuffer getShortBuffer() {
	return _shortBuffer;
    }

    public final short[] array() {
	return _shortBuffer.array();
    }

    public final int arrayOffset() {
	return _shortBuffer.arrayOffset();
    }

    public ShortBuffer asReadOnlyBuffer() {
	return _shortBuffer.asReadOnlyBuffer();
    }

    public final int capacity() {
	return _shortBuffer.capacity();
    }

    public final Buffer clear() {
	return _shortBuffer.clear();
    }

    public ShortBuffer compact() {
	return _shortBuffer.compact();
    }

    public int compareTo(ShortBuffer arg0) {
	return _shortBuffer.compareTo(arg0);
    }

    public ShortBuffer duplicate() {
	return _shortBuffer.duplicate();
    }

    @Override
    public boolean equals(Object arg0) {
	return _shortBuffer.equals(arg0);
    }

    public final Buffer flip() {
	return _shortBuffer.flip();
    }

    public short get() {
	return _shortBuffer.get();
    }

    public short get(int arg0) {
	return _shortBuffer.get(arg0);
    }

    public ShortBuffer get(short[] arg0, int arg1, int arg2) {
	return _shortBuffer.get(arg0, arg1, arg2);
    }

    public ShortBuffer get(short[] arg0) {
	return _shortBuffer.get(arg0);
    }

    public final boolean hasArray() {
	return _shortBuffer.hasArray();
    }

    public final boolean hasRemaining() {
	return _shortBuffer.hasRemaining();
    }

    @Override
    public int hashCode() {
	return _shortBuffer.hashCode();
    }

    public boolean isDirect() {
	return _shortBuffer.isDirect();
    }

    public boolean isReadOnly() {
	return _shortBuffer.isReadOnly();
    }

    public final int limit() {
	return _shortBuffer.limit();
    }

    public final Buffer limit(int arg0) {
	return _shortBuffer.limit(arg0);
    }

    public final Buffer mark() {
	return _shortBuffer.mark();
    }

    public ByteOrder order() {
	return _shortBuffer.order();
    }

    public final int position() {
	return _shortBuffer.position();
    }

    public final Buffer position(int arg0) {
	return _shortBuffer.position(arg0);
    }

    public ShortBuffer put(int arg0, short arg1) {
	return _shortBuffer.put(arg0, arg1);
    }

    public ShortBuffer put(short arg0) {
	return _shortBuffer.put(arg0);
    }

    public ShortBuffer put(short[] arg0, int arg1, int arg2) {
	return _shortBuffer.put(arg0, arg1, arg2);
    }

    public final ShortBuffer put(short[] arg0) {
	return _shortBuffer.put(arg0);
    }

    public ShortBuffer put(ShortBuffer arg0) {
	return _shortBuffer.put(arg0);
    }

    public final int remaining() {
	return _shortBuffer.remaining();
    }

    public final Buffer reset() {
	return _shortBuffer.reset();
    }

    public final Buffer rewind() {
	return _shortBuffer.rewind();
    }

    public ShortBuffer slice() {
	return _shortBuffer.slice();
    }

    @Override
    public String toString() {
	return _shortBuffer.toString();
    }
}
