package messages.fixed;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import api.CSenseException;
import api.IMessage;
import api.IMessagePool;

import messages.ReadOnlyMessageException;
import messages.TypeInfo;

public class DoubleVector extends Vector<Double> {
    protected TypeInfo<DoubleVector> _type;
    protected DoubleBuffer _doubleBuffer;
    protected NumberFormat _format = null;
    protected NumberFormat _small = null;

    public DoubleVector(IMessagePool pool, TypeInfo<DoubleVector> type)
	    throws CSenseException {
	super(pool, type);
	_type = type;
	_doubleBuffer = buffer().asDoubleBuffer();
    }

    public DoubleVector(IMessagePool pool, TypeInfo<DoubleVector> type, IMessage parent, ByteBuffer bb) throws CSenseException {
	super(pool, type, parent, bb);
	_type = type;
	_doubleBuffer = bb.asDoubleBuffer();
    }

    @Override
    public TypeInfo<DoubleVector> getTypeInfo() {
	return _type;
    }

    @Override
    public String toString() {
	return "DoubleVector[C:" + buffer().capacity() + "] "
		+ super.toString();
    }

    @Override
    public String displayValues() {
	if (_format == null) {
	    _format = new DecimalFormat("0.0000");
	    _small = new DecimalFormat("0.0000E0");
	    _format.setRoundingMode(RoundingMode.HALF_EVEN);
	}
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _doubleBuffer.capacity(); i++) {
	    double d = _doubleBuffer.get(i);
	    if (Math.abs(d) > 1e-4) {
		sb.append(_format.format(d) + " ");
	    } else {
		sb.append(_small.format(d) + " ");
	    }
	}

	return sb.toString();
    }
    
    @Override
    public String debugValues() {
	StringBuffer sb = new StringBuffer();
	double s = 0;
	for (int i = 0; i < _doubleBuffer.capacity(); i++) {
	    double d = _doubleBuffer.get(i);
	    s = s + d;
	    if (Math.abs(d) > 1e-4) {
		sb.append(_format.format(d) + " ");
		// sb.append(d + " ");
	    } else {
		sb.append(_small.format(d) + " ");

	    }
	}

	return "sum=" + s + " " + sb.toString();
    }

    public DoubleBuffer getBuffer() {
	return _doubleBuffer;
    }

    @Override
    public Double get() {
	return _doubleBuffer.get();
    }

    @Override
    public void put(Double val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_doubleBuffer.put(val);
    }

    public void put(double val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_doubleBuffer.put(val);
    }

    @Override
    public Double get(int index) {
	return _doubleBuffer.get(index);
    }

    @Override
    public void put(int index, Double val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_doubleBuffer.put(index, val);
    }

    public void put(int index, double val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_doubleBuffer.put(index, val);
    }

    @Override
    public void slice(int lower, int upper) {
	_doubleBuffer.position(lower);
	_doubleBuffer.limit(upper);
	_doubleBuffer = _doubleBuffer.slice();
    }

    @Override
    public Vector<Double> flip() {
	_doubleBuffer.flip();
	return this;
    }

    @Override
    public Vector<Double> clear() {
	_doubleBuffer.clear();
	return this;
    }

    @Override
    public int capacity() {
	return _doubleBuffer.capacity();
    }

    @Override
    public int position() {
	return _doubleBuffer.position();
    }

    @Override
    public int limit() {
	return _doubleBuffer.limit();
    }

    @Override
    public Vector<Double> position(int position) {
	_doubleBuffer.position(position);
	return this;
    }

    @Override
    public int remaining() {
	return _doubleBuffer.remaining();
    }

    @Override
    public void initialize() {
	super.initialize();
	_doubleBuffer.clear();
    }
}
