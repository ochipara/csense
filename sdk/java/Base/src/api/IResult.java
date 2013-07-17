package api;

public class IResult {
    private static final int R_SUCCCESS = 0;
    private static final int R_PUSH_FAILED = 1;
    private static final int R_PUSH_DROP = 2;
    
    public static final IResult PUSH_SUCCESS = new IResult(R_SUCCCESS);
    public static final IResult PUSH_FAILED = new IResult(R_PUSH_FAILED);
    public static final IResult PUSH_DROP = new IResult(R_PUSH_DROP);
    
    protected final int result;
    public IResult(int result) {
	this.result = result;
    }
}
