package api;

public class CSenseOptions {
    public static final boolean USE_AUTOTHREAD_SWITCH = true;
    public static final boolean PRINT_DEBUG = true;
    public static final int AUDIO_BLOCK_SIZE_IN_SAMPLES = 512;

    // public static final String SERVER_IP = "192.168.0.104";
    // public static final String SERVER_IP = "128.255.145.224";
    public static final String SERVER_IP = "127.0.0.1";
    // public static final String SERVER_IP = "10.0.0.5";
    public static final String SERVER_IP_FARLEY_MBA_LAB = "172.23.48.192";
    public static final String SERVER_IP_FARLEY_MBA_APT = "192.168.0.101";

    public static final String SERVER_LAB = "alacran.cs.uiowa.edu";
    public static final String DEFAULT_FTP_USER = "egosense";
    public static final String DEFAULT_FTP_PASS = "egosense";
    public static final int SERVER_PORT = 4445;
    public static final int INIT_MSG_POOL_CAPACITY = 32; // 4; FIXME Some
							 // implementation may
							 // not increase the
							 // pool capacity at
							 // runtime.

    public static final String DEFAULT_XML = "<start>" + "" + "<question>"
	    + "<text>What kind of activity are you performing?</text>"
	    + "<options>Physical Activity</options>"
	    + "<options>Social Activity</options>" + "<record></record>"
	    + "</question>" + "" + "<question>"
	    + "<text>What kind of Physical Activity are you performing?</text>"
	    + "<options>Walking</options>" + "<options>Running</options>"
	    + "<options>Eating</options>" + "<options>Working</options>"
	    + "<options>Reading</options>" + "<options>Cooking</options>"
	    + "<if>0</if>" + "<from>1</from>" + "<to>1</to>"
	    + "<record></record>" + "</question>" + "" + "<question>"
	    + "<text>With whom are you talking?</text>"
	    + "<multiple></multiple>" + "<options>John</options>"
	    + "<options>Mary</options>" + "<options>Foo</options>"
	    + "<options>Others not listed above</options>"
	    + "<options>None of the above</options>"
	    + "<options>No one</options>" + "<if>0</if>" + "<from>2</from>"
	    + "<to>2</to>" + "</question>" + "" + "</start>";
}
