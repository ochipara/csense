package edu.uiowa.csense.runtime.compatibility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AndroidFileLogger {
    protected final File file;
    protected final FileWriter writer;

    public AndroidFileLogger(File file) throws IOException {
	this.file = file;
	this.writer = new FileWriter(file, true);
    }

    public void e(String tag, String msg) {
	android.util.Log.e(tag, msg);
	writeMessage(tag, "E", msg, true);
    }

    public void w(String tag, String msg) {
	android.util.Log.w(tag, msg);
	writeMessage(tag, "W", msg, true);	
    }

    public void i(String tag, String msg) {
	android.util.Log.i(tag, msg);
	writeMessage(tag, "I", msg, false);
    }
    
    
    public void d(String tag, String msg) {
	android.util.Log.d(tag, msg);
	writeMessage(tag, "D", msg, false);
    }
    
    public void v(String tag, String msg) {
	android.util.Log.v(tag, msg);
	writeMessage(tag, "V", msg, false);   	
    }
    
    protected void writeMessage(String tag, String level, String msg, boolean flush) {
	try {
	    String time = Utils.formatTime(Utils.now());
	    writer.write(time + " " + tag + " " + level + " " + msg + "\n");
	    if (flush) writer.flush();
	} catch (IOException e) {
	    android.util.Log.e("LOG", e.getMessage());
	}
    }

      
}
