package messages.fixed;

import java.nio.ShortBuffer;

import api.CSenseException;
import api.IMessagePool;
import messages.ReadOnlyMessageException;
import messages.TypeInfo;

public class ShortVector extends Vector<Short> {
    /**
     * 
     */
    private ShortBuffer _shortBuffer;
    private TypeInfo<ShortVector> _type;

    public ShortVector(IMessagePool<ShortVector> pool, TypeInfo<ShortVector> type)
	    throws CSenseException {
	super(pool, type);
	_shortBuffer = buffer().asShortBuffer();
	_type = type;
    }

    @Override
    public TypeInfo<ShortVector> getTypeInfo() {
	return _type;
    }

    @Override
    public void slice(int lower, int upper) {
	_shortBuffer.position(lower);
	_shortBuffer.limit(upper);
	_shortBuffer = _shortBuffer.slice();
    }

    @Override
    public Short get() {
	return _shortBuffer.get();
    }

    @Override
    public void put(Short val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_shortBuffer.put(val);
    }

    @Override
    public Short get(int index) {
	return _shortBuffer.get(index);
    }

    @Override
    public void put(int index, Short val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_shortBuffer.put(index, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _shortBuffer.capacity(); i++) {
	    Short d = _shortBuffer.get(i);
	    sb.append(d + " ");
	}

	return sb.toString();
    }

    @Override
    public String debugValues() {
	return displayValues();
    }

    public void get(short[] payload) {
	_shortBuffer.get(payload);
    }

    @Override
    public Vector<Short> flip() {
	_shortBuffer.flip();
	return this;
    }

    @Override
    public Vector<Short> clear() {
	_shortBuffer.clear();
	return this;
    }

    @Override
    public int capacity() {
	return _shortBuffer.capacity();
    }

    @Override
    public int position() {
	return _shortBuffer.position();
    }

    @Override
    public int limit() {
	return _shortBuffer.limit();
    }

    @Override
    public Vector<Short> position(int position) {
	_shortBuffer.position(position);
	return this;
    }

    @Override
    public int remaining() {
	return _shortBuffer.remaining();
    }

    @Override
    public void initialize() {
	super.initialize();
	_shortBuffer.clear();
    }

    @Override
    public short getShort() {
	return _shortBuffer.get();
    }

    public ShortBuffer getBuffer() {
	return _shortBuffer;
    }
}
