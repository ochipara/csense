package compatibility;

import android.os.HandlerThread;
import base.Debug;

public class CSenseHandlerThread extends HandlerThread {
    public CSenseHandlerThread(String threadName) {
	super(threadName);
    }
        
    @Override
    public void run() {
	Debug.logThreadStart();
	onStart();
	super.run();
	onStop();
	Debug.logThreadStop();
    }
    
    public void onStart() {}
    
    public void onStop() {}
}