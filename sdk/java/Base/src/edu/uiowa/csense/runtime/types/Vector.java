package messages.fixed;

import java.nio.ByteBuffer;

import api.CSenseException;
import api.IMessage;
import api.IMessagePool;
import messages.RawMessage;
import messages.TypeInfo;

public abstract class Vector<T> extends RawMessage {
    /**
	 * 
	 */
    private TypeInfo<? extends Vector<T>> _vectorType;

    public Vector(IMessagePool<? extends Vector<T>> pool, TypeInfo<? extends Vector<T>> type) throws CSenseException {
	super(pool, type);
	_vectorType = type;
    }

    public Vector(IMessagePool<? extends Vector<T>> pool, TypeInfo<? extends Vector<T>>  type, IMessage parent, ByteBuffer bb) throws CSenseException {
	super(pool, type, parent, bb);
	_vectorType = type;
    }

    /**
     * 
     * @return the number of elements in the vector
     */
    public int getNumberOfElements() {
	return _vectorType.getNumberOfElements();
    }

    /**
     * 
     * @return
     * @return the type information associated with this type
     */
    public abstract TypeInfo<? extends Vector<T>> getTypeInfo();

    public abstract void slice(int lower, int upper);

    public abstract T get();

    public abstract void put(T val);

    public abstract T get(int index);

    public abstract void put(int index, T val);

    //
    @Override
    public abstract Vector<T> flip();

    @Override
    public abstract Vector<T> clear();

    @Override
    public abstract Vector<T> position(int position);

    @Override
    public abstract int capacity();

    @Override
    public abstract int position();

    @Override
    public abstract int remaining();

    @Override
    public abstract int limit();

    public abstract String displayValues();
    public abstract String debugValues();

    @Override
    public void initialize() {
	super.initialize();
    }

}
