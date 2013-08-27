package edu.uiowa.csense.runtime.compatibility;

import edu.uiowa.csense.profiler.Debug;
import android.os.HandlerThread;

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