package messages.fixed;

import java.nio.ShortBuffer;

import api.CSenseException;
import api.IMessagePool;
import api.Message;

import messages.TypeInfo;

public class ShortMatrix extends Matrix<Short> {
    /**
     * 
     */
    protected ShortBuffer shorts;

    public <T extends ShortMatrix>ShortMatrix(IMessagePool<T> pool, TypeInfo<T> typeInfo)
	    throws CSenseException {
	super(pool, typeInfo);
	shorts = buffer().asShortBuffer();

	assert (shorts.capacity() > 0);
    }

    @Override
    public Short get(int r, int c) {
	int offset = r + c * rows;
	return shorts.get(offset);
    }

    @Override
    public void put(int r, int c, Short val) {
	int offset = r + c * rows;
	shorts.put(offset, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		sb.append(shorts.get(r + c * rows) + " ");
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
    public void put(short val) {
	shorts.put(val);
	buffer().position(shorts.position() * 2);
    }

    @Override
    public void put(Short val) {
	shorts.put(val);
	buffer().position(shorts.position() * 2);
    }

    @Override
    public void putShort(short s) {
	put(s);
    }

    @Override
    public void initialize() {
	shorts.clear();
	super.initialize();
    }

    @Override
    public String toString() {
	return "ShortMatrix[C:" + shorts.capacity() + " P:" + shorts.position() + "] "
		+ super.toString();
    }
    
    @Override
    public Message position(int position) {
	shorts.position(position);
	return this;
    }

    @Override
    public Message flip() {
	shorts.flip();
	buffer().flip();
	return this;
    }
    
    @Override
    public int remaining() {
	return shorts.remaining();
    }

    public ShortBuffer getBuffer() {
	return shorts;
    }

}
