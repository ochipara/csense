package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class CharVector extends Vector<Character> {
    /**
     * 
     */
    private TypeInfo<CharVector> _type;

    public CharVector(FramePool pool, TypeInfo<CharVector> type) throws CSenseException {
	super(pool, type);
	_type = type;
    }

    @Override
    public TypeInfo<? extends CharVector> getTypeInfo() {
	return _type;
    }
}
