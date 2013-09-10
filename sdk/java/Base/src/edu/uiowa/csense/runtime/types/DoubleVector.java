package edu.uiowa.csense.runtime.types;

import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.text.NumberFormat;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class DoubleVector extends Vector<Double> {
    protected final TypeInfo _type;
    protected final DoubleBuffer _doubleBuffer;
    
    public DoubleVector(FramePool pool, TypeInfo type) throws CSenseException {
	super(pool, type);
	_type = type;
	_doubleBuffer = buffer.asDoubleBuffer();
    }

    @Override
    public TypeInfo getTypeInfo() {
	return _type;
    }

    @Override
    public String toString() {
	return "DoubleVector[C:" + getBuffer().capacity() + "] "
		+ super.toString();
    }
    
    public DoubleBuffer getDoubleBuffer() {
	return _doubleBuffer;
    }

    protected NumberFormat _format = null;
    protected NumberFormat _small = null;

    public final double[] array() {
	return _doubleBuffer.array();
    }

    public final int arrayOffset() {
	return _doubleBuffer.arrayOffset();
    }

    public DoubleBuffer asReadOnlyBuffer() {
	return _doubleBuffer.asReadOnlyBuffer();
    }

    public final int capacity() {
	return _doubleBuffer.capacity();
    }

    public final Buffer clear() {
	return _doubleBuffer.clear();
    }

    public DoubleBuffer compact() {
	return _doubleBuffer.compact();
    }

    public int compareTo(DoubleBuffer arg0) {
	return _doubleBuffer.compareTo(arg0);
    }

    public DoubleBuffer duplicate() {
	return _doubleBuffer.duplicate();
    }

    public boolean equals(Object arg0) {
	return _doubleBuffer.equals(arg0);
    }

    public final Buffer flip() {
	return _doubleBuffer.flip();
    }

    public double get() {
	return _doubleBuffer.get();
    }

    public DoubleBuffer get(double[] arg0, int arg1, int arg2) {
	return _doubleBuffer.get(arg0, arg1, arg2);
    }

    public DoubleBuffer get(double[] arg0) {
	return _doubleBuffer.get(arg0);
    }

    public double get(int arg0) {
	return _doubleBuffer.get(arg0);
    }

    public final boolean hasArray() {
	return _doubleBuffer.hasArray();
    }

    public final boolean hasRemaining() {
	return _doubleBuffer.hasRemaining();
    }

    public int hashCode() {
	return _doubleBuffer.hashCode();
    }

    public boolean isDirect() {
	return _doubleBuffer.isDirect();
    }

    public boolean isReadOnly() {
	return _doubleBuffer.isReadOnly();
    }

    public final int limit() {
	return _doubleBuffer.limit();
    }

    public final Buffer limit(int arg0) {
	return _doubleBuffer.limit(arg0);
    }

    public final Buffer mark() {
	return _doubleBuffer.mark();
    }

    public ByteOrder order() {
	return _doubleBuffer.order();
    }

    public final int position() {
	return _doubleBuffer.position();
    }

    public final Buffer position(int arg0) {
	return _doubleBuffer.position(arg0);
    }

    public DoubleBuffer put(double arg0) {
	return _doubleBuffer.put(arg0);
    }

    public DoubleBuffer put(double[] arg0, int arg1, int arg2) {
	return _doubleBuffer.put(arg0, arg1, arg2);
    }

    public final DoubleBuffer put(double[] arg0) {
	return _doubleBuffer.put(arg0);
    }

    public DoubleBuffer put(DoubleBuffer arg0) {
	return _doubleBuffer.put(arg0);
    }

    public DoubleBuffer put(int arg0, double arg1) {
	return _doubleBuffer.put(arg0, arg1);
    }

    public final int remaining() {
	return _doubleBuffer.remaining();
    }

    public final Buffer reset() {
	return _doubleBuffer.reset();
    }

    public final Buffer rewind() {
	return _doubleBuffer.rewind();
    }
    

//    @Override
//    public String displayValues() {
//	if (_format == null) {
//	    _format = new DecimalFormat("0.0000");
//	    _small = new DecimalFormat("0.0000E0");
//	    _format.setRoundingMode(RoundingMode.HALF_EVEN);
//	}
//	StringBuffer sb = new StringBuffer();
//	for (int i = 0; i < _doubleBuffer.capacity(); i++) {
//	    double d = _doubleBuffer.get(i);
//	    if (Math.abs(d) > 1e-4) {
//		sb.append(_format.format(d) + " ");
//	    } else {
//		sb.append(_small.format(d) + " ");
//	    }
//	}
//
//	return sb.toString();
//    }
//    
//    @Override
//    public String debugValues() {
//	StringBuffer sb = new StringBuffer();
//	double s = 0;
//	for (int i = 0; i < _doubleBuffer.capacity(); i++) {
//	    double d = _doubleBuffer.get(i);
//	    s = s + d;
//	    if (Math.abs(d) > 1e-4) {
//		sb.append(_format.format(d) + " ");
//		// sb.append(d + " ");
//	    } else {
//		sb.append(_small.format(d) + " ");
//
//	    }
//	}
//
//	return "sum=" + s + " " + sb.toString();
//    }
//
//   
}
