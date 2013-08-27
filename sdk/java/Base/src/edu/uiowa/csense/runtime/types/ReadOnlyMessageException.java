package edu.uiowa.csense.runtime.types;

public class ReadOnlyMessageException extends UnsupportedOperationException {
    private static final long serialVersionUID = 1L;

    public ReadOnlyMessageException() {
	super();
	// TODO Auto-generated constructor stub
    }

    public ReadOnlyMessageException(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

    public ReadOnlyMessageException(String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    public ReadOnlyMessageException(Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }
}
