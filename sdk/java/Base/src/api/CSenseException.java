package api;

public class CSenseException extends Exception {
    private static final long serialVersionUID = 1L;
    protected CSenseError _error = null;

    public CSenseException() {
	super();
    }

    public CSenseException(CSenseError error) {
	super();
	_error = error;
    }

    public CSenseException(CSenseError error, String message) {
	super(message);
	_error = error;
    }

    public CSenseException(Throwable cause) {
	super(cause);
    }

    public CSenseException(CSenseError error, Throwable cause) {
	super(cause);
	_error = error;
    }

    public CSenseException(String message) {
	super(message);
    }

    public CSenseException(String message, Throwable cause) {
	super(message, cause);
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
