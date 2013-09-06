package edu.uiowa.csense.runtime.api;

public class Options {    
    public static boolean PROFILE = true;
    public static boolean CHECK_CURRENT_THREAD = false;    
    public static int INIT_MSG_POOL_CAPACITY = 32;
    
    
    // ===> FEEDBACK KINDS <===
    public static final int FEEDBACK_REACHED_TAP = 0;
    public static final int FEEDBACK_EXCEPTION = 1;
}
