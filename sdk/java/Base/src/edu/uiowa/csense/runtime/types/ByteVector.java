package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class ByteVector extends Vector<Byte> {
    /**
     * 
     */
    private TypeInfo<ByteVector> _type;

    public ByteVector(FramePool pool, TypeInfo<ByteVector> type) throws CSenseException {
	super(pool, type);	
	_type = type;
    }

    @Override
    public TypeInfo<ByteVector> getTypeInfo() {
	return _type;
    }
}
