package edu.uiowa.csense.runtime.types;


import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class FilenameType extends JavaFrame<String> {
    /**
     * 
     */
    public final static TypeInfo type = TypeInfo.newJavaMessage(FilenameType.class);    
    
    public FilenameType(FramePool pool, TypeInfo type) throws CSenseException {
	super(pool, type);
	data = "";
    }    
}
