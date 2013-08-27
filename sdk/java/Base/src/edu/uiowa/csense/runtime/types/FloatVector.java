package messages.fixed;

import java.nio.FloatBuffer;

import api.CSenseException;
import api.IMessagePool;
import messages.ReadOnlyMessageException;
import messages.TypeInfo;

public class FloatVector extends Vector<Float> {
    protected TypeInfo<FloatVector> _type;
    protected FloatBuffer _floatBuffer;

    public FloatVector(IMessagePool pool, TypeInfo<FloatVector> type)
	    throws CSenseException {
	super(pool, type);
	_type = type;
	_floatBuffer = buffer().asFloatBuffer();
    }

    @Override
    public TypeInfo<FloatVector> getTypeInfo() {
	return _type;
    }

    @Override
    public String toString() {
	return "FloatVector[cap=" + buffer().capacity() + "] "
		+ super.toString();
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _floatBuffer.capacity(); i++) {
	    Float d = _floatBuffer.get(i);
	    sb.append(d + " ");
	}

	return sb.toString();
    }
    
    @Override
    public String debugValues() {
	StringBuffer sb = new StringBuffer();
	float s = 0;
	for (int i = 0; i < _floatBuffer.capacity(); i++) {
	    Float d = _floatBuffer.get(i);
	    s = s + d;
	    sb.append(d + " ");
	}

	return "sum=" + s + " " + sb.toString();
    }

    @Override
    public Float get() {
	return _floatBuffer.get();
    }

    /**
     * It is important to have also the variant with the primitive value due to
     * performance issues By doing this we avoid allocating unnecessary objects
     * during the boxing/unboxing of primitives Unfortunately, we need to add
     * these methods manually as java generics and primitives do not play
     * 
     * @param val
     */
    public void put(float val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_floatBuffer.put(val);
    }

    @Override
    public void put(Float val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_floatBuffer.put(val);
    }

    @Override
    public Float get(int index) {
	return _floatBuffer.get(index);
    }

    @Override
    public void put(int index, Float val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_floatBuffer.put(index, val);

    }

    @Override
    public void slice(int lower, int upper) {
	_floatBuffer.position(lower);
	_floatBuffer.limit(upper);
	_floatBuffer = _floatBuffer.slice();
    }

    @Override
    public Vector<Float> flip() {
	_floatBuffer.flip();
	return this;
    }

    @Override
    public Vector<Float> clear() {
	_floatBuffer.clear();
	return this;
    }

    @Override
    public int capacity() {
	return _floatBuffer.capacity();
    }

    @Override
    public int position() {
	return _floatBuffer.position();
    }

    @Override
    public int limit() {
	return _floatBuffer.limit();
    }

    @Override
    public Vector<Float> position(int position) {
	_floatBuffer.position(position);
	return this;
    }

    @Override
    public int remaining() {
	return _floatBuffer.remaining();
    }

    @Override
    public void initialize() {
	_floatBuffer.clear();
	super.initialize();
    }

    public FloatBuffer getBuffer() {
	return _floatBuffer;
    }


}
