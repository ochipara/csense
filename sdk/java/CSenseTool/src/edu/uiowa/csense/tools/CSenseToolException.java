package edu.uiowa.csense.tools;

public class CSenseToolException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public CSenseToolException(String msg) {
	super(msg);
    }

    public CSenseToolException(Exception e) {
	super(e);
    }

}
