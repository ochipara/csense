package messages.fixed;

import java.nio.ByteBuffer;

import api.CSenseException;
import api.IMessagePool;
import messages.ReadOnlyMessageException;
import messages.TypeInfo;

public class ByteVector extends Vector<Byte> {
    /**
     * 
     */
    private ByteBuffer _byteBuffer;
    private TypeInfo<ByteVector> _type;

    public ByteVector(IMessagePool<ByteVector> pool, TypeInfo<ByteVector> type)
	    throws CSenseException {
	super(pool, type);
	_byteBuffer = buffer();
	_type = type;
    }

    @Override
    public TypeInfo<ByteVector> getTypeInfo() {
	return _type;
    }

    @Override
    public void slice(int lower, int upper) {
	_byteBuffer.position(lower);
	_byteBuffer.limit(upper);
	_byteBuffer = _byteBuffer.slice();
    }

    @Override
    public Byte get() {
	return _byteBuffer.get();
    }

    @Override
    public void put(Byte val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_byteBuffer.put(val);
    }

    @Override
    public Byte get(int index) {
	return _byteBuffer.get(index);
    }

    @Override
    public void put(int index, Byte val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_byteBuffer.put(index, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _byteBuffer.capacity(); i++) {
	    byte d = _byteBuffer.get(i);
	    sb.append(d + " ");
	}

	return sb.toString();
    }

    @Override
    public String debugValues() {
	return displayValues();
    }


    @Override
    public void put(byte[] bytes) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_byteBuffer.put(bytes);
    }

    public void get(byte[] payload) {
	_byteBuffer.get(payload);
    }

    @Override
    public Vector<Byte> flip() {
	_byteBuffer.flip();
	return this;
    }

    @Override
    public Vector<Byte> clear() {
	_byteBuffer.clear();
	return this;
    }

    @Override
    public int capacity() {
	return _byteBuffer.capacity();
    }

    @Override
    public int position() {
	return _byteBuffer.position();
    }

    @Override
    public int limit() {
	return _byteBuffer.limit();
    }

    @Override
    public Vector<Byte> position(int position) {
	_byteBuffer.position(position);
	return this;
    }

    @Override
    public int remaining() {
	return _byteBuffer.remaining();
    }

    @Override
    public void initialize() {
	super.initialize();
	_byteBuffer.clear();
    }
}
