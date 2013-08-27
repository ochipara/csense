package edu.uiowa.csense.compiler;

public class RuntimeCompilerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RuntimeCompilerException(String msg) {
	super(msg);
    }
    
    public RuntimeCompilerException(Exception e) {
	super(e);
    }
    
    public RuntimeCompilerException(String msg, Exception e) {
	super(msg, e);
    }
}
