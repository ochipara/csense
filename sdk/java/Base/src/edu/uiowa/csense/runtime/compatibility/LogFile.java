package compatibility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogFile {
    protected static Map<String, LogFile> logs = new HashMap<String, LogFile>();
    protected final File file;
    protected final BufferedWriter bw;

    public LogFile(File file) throws IOException {
	this.file = file;
	bw = new BufferedWriter(new FileWriter(file, true));
    }

    protected synchronized void write(String str) {
	try {
	    bw.write(str);
	    bw.flush();
	} catch (IOException e) {	 
	    e.printStackTrace();
	}
    }
    private void write(Object... args) {
	StringBuffer sb = new StringBuffer(Utils.formatTime(Utils.now()));
	for (Object arg : args) {
	    sb.append(" ");
	    sb.append(arg.toString());	    
	}
	sb.append("\n");
	write(sb.toString());
    }

    public void debug(String str) {
	write(str);
    }

    public void info(String str) {
	write(str);
    }

    public static LogFile getLogger(String fileName) throws IOException {
	LogFile file = logs.get(fileName);
	if (file == null) {
	    file = new LogFile(new File(fileName));
	    logs.put(fileName, file);
	} 

	return file;
    }


    public void warn(Object... args) {
	write(args);
    }

    public void info(Object... args) {
	write(args);	
    }
}
