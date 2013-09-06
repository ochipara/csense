package edu.uiowa.csense.components.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import edu.uiowa.csense.runtime.workspace.Variable;

/**
 * 
 * @author Austin
 * 
 */
public class FromDiskComponent<T extends RawFrame> extends CSenseSource<T> {
    private static final String TAG = "fromDisk";

    public OutputPort<T> out = newOutputPort(this, "out");
    
    // options to pass the file name
    protected final Variable fileNameVar;
    protected final String fileName;
    
    // state
    protected File file = null;    
    protected FileInputStream fileInputStream = null;
    protected FileChannel channel = null;

    public FromDiskComponent(TypeInfo<T> type, Variable fileNameVar) throws CSenseException {
	super(type);
	this.fileName = null;
	this.fileNameVar = fileNameVar;    
    }
    
    public FromDiskComponent(TypeInfo<T> type, String fileName) throws CSenseException {
	super(type);
	this.fileName = fileName;
	this.fileNameVar = null;
    }

    @Override
    public void onEvent(Event t) throws CSenseException {
	try {
	    //if (channel.position() < channel.size()) {
	    T message = getNextMessageToWriteInto();
	    int r = channel.read(message.getBuffer());
	    if (r != -1 ) {
		message.flip();
		out.push(message);
		getScheduler().schedule(this, asTask());	    
	    } else {	    
		edu.uiowa.csense.runtime.compatibility.Log.d(TAG, "eof");
		channel.close();
		fileInputStream.close();
		//T message = getNextMessageToWriteInto();
		message.eof();
		out.push(message);
	    }	    
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void onCreate() throws CSenseException {
	try {
	    super.onCreate();
	    if (fileNameVar != null) file = new File((String) fileNameVar.getValue());
	    else if (fileName != null) file = new File(fileName);
	    else throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "File name not initialized");
	    
	    if (file == null) throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "File was not specified [" + fileNameVar + "]");
	    fileInputStream = new FileInputStream(file);
	    channel = fileInputStream.getChannel();
	    getScheduler().schedule(this, asTask());
	} catch (FileNotFoundException e) {
	    throw new CSenseException(e);
	} 
    }

}
