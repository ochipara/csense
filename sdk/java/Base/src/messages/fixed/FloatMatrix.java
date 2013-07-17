package messages.fixed;

import java.nio.FloatBuffer;

import api.CSenseException;
import api.IMessagePool;
import api.Message;

import messages.TypeInfo;

public class FloatMatrix extends Matrix<Float> {
    /**
     * 
     */
    protected FloatBuffer floats;

    public FloatMatrix(IMessagePool pool, TypeInfo<FloatMatrix> typeInfo)
	    throws CSenseException {
	super(pool, typeInfo);
	floats = buffer().asFloatBuffer();

	assert (floats.capacity() > 0);
    }

    @Override
    public Float get(int r, int c) {
	int offset = r + c * rows;
	return floats.get(offset);
    }

    @Override
    public void put(int r, int c, Float val) {
	int offset = r + c * rows;
	floats.put(offset, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		sb.append(floats.get(r + c * rows) + " ");
	    }
	    sb.append("\n");
	}

	return sb.toString();
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
	floats.put(val);
    }

    @Override
    public void put(Float val) {
	floats.put(val);
    }


    @Override
    public void initialize() {
	floats.clear();
	super.initialize();
    }

    @Override
    public String toString() {
	return "FloatMatrix[C:" + floats.capacity() + " P:" + floats.position() + "] "
		+ super.toString();
    }
    
    @Override
    public Message position(int position) {
	floats.position(position);
	return this;
    }

    @Override
    public Message flip() {
	floats.flip();
	buffer().position(0);
	buffer().limit(floats.remaining() * 4);
	return this;
    }
    
    @Override
    public int remaining() {
	return floats.remaining();
    }

    public FloatBuffer getBuffer() {
	return floats;
    }

}
