package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class CharVector extends Vector<Character> {
    public CharVector(FramePool pool, TypeInfo type) throws CSenseException {
	super(pool, type);
    }
}
