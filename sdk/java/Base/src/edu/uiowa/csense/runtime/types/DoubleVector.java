package edu.uiowa.csense.runtime.types;

import java.nio.DoubleBuffer;
import java.text.NumberFormat;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class DoubleVector extends Vector<Double> {
    protected final TypeInfo<DoubleVector> _type;
    protected final DoubleBuffer _doubleBuffer;
    
    public DoubleVector(FramePool pool, TypeInfo<DoubleVector> type) throws CSenseException {
	super(pool, type);
	_type = type;
	_doubleBuffer = buffer.asDoubleBuffer();
    }

    @Override
    public TypeInfo<DoubleVector> getTypeInfo() {
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
