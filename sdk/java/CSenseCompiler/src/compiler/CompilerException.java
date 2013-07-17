package compiler;

public class CompilerException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public CompilerException(String message) {
	super(message);
    }

    public CompilerException(String msg, Exception e) {
	super(msg, e);
    }

    public CompilerException(Exception e) {
	super(e);
    }
}
