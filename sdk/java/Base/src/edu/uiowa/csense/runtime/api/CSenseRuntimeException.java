package edu.uiowa.csense.runtime.api;

public class CSenseRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected CSenseError _error = null;

    public CSenseRuntimeException() {
	super();
    }

    public CSenseRuntimeException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

    public CSenseRuntimeException(String arg0) {
	super(arg0);
    }

    public CSenseRuntimeException(Throwable arg0) {
	super(arg0);
    }

    public CSenseRuntimeException(CSenseError error) {
	_error = error;
    }    

    public CSenseError error() {
	return _error;
    }

    @Override
    public String toString() {
	if (_error != null)
	    return "error: " + _error.toString() + " " + getMessage();
	else
	    return getMessage();
    }
}
