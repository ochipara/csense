package edu.uiowa.csense.runtime.types;


import java.nio.ShortBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class ShortVector extends Vector<Short> {
    private final TypeInfo<ShortVector> _type;
    private final ShortBuffer _shortBuffer; 

    /**
     * 
     */
    public ShortVector(FramePool pool, TypeInfo<ShortVector> type)
	    throws CSenseException {
	super(pool, type);
	_type = type;
	_shortBuffer = buffer.asShortBuffer();
    }

    @Override
    public TypeInfo<ShortVector> getTypeInfo() {
	return _type;
    }


    public ShortBuffer getShortBuffer() {
	return _shortBuffer;
    }
}
