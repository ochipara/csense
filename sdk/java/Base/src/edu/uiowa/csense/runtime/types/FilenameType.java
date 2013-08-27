package messages.fixed;

import api.CSenseException;
import api.IMessagePool;
import messages.TypeInfo;

public class FilenameType extends CharVector {
    /**
	 * 
	 */
    public static final int MAX_SIZE = 1024;

    public FilenameType(IMessagePool pool, TypeInfo<FilenameType> type) throws CSenseException {
	super(pool, new TypeInfo<CharVector>(CharVector.class, 2, 1, MAX_SIZE ,true, false));
    }

}
