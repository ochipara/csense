package edu.uiowa.csense.runtime.types;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class FilenameType extends CharVector {
    /**
	 * 
	 */
    public static final int MAX_SIZE = 1024;

    public FilenameType(FramePool pool, TypeInfo<FilenameType> type) throws CSenseException {
	super(pool, new TypeInfo<CharVector>(CharVector.class, 2, 1, MAX_SIZE ,true, false));
    }

}
