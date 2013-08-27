package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public abstract class Vector<T> extends RawFrame {
    /**
     * 
     */
    private TypeInfo<? extends Vector<T>> vectorType;

    public Vector(FramePool pool, TypeInfo<? extends Vector<T>> type) throws CSenseException {
	super(pool, type);
	vectorType = type;
    }

    /**
     * 
     * @return the number of elements in the vector
     */
    public final int size() {
	return vectorType.getNumberOfElements();
    }

    /**
     * 
     * @return
     * @return the type information associated with this type
     */
    public abstract TypeInfo<? extends Vector<T>> getTypeInfo();
}
