package edu.uiowa.csense.components.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Feedback;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.ICommandHandler;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.compatibility.Environment;
import edu.uiowa.csense.runtime.types.FilenameType;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.workspace.Variable;
import edu.uiowa.csense.runtime.workspace.Workspace;

public class ToDiskComponent<T extends Frame> extends edu.uiowa.csense.runtime.v4.CSenseSource<FilenameType> {
    public final static int SPLIT_NEVER = 0;
    public final static int SPLIT_BY_FILESIZE = 1;
    public final static int SPLIT_BY_INVOCATION_COUNT = 2;

    public final InputPort<T> in = newInputPort(this, "dataIn");
    public final OutputPort<T> out = newOutputPort(this, "dataOut");

    public final OutputPort<FilenameType> fileOutput = newOutputPort(this, "fileOutput");	

    // File writer
    protected RandomAccessFile _file;

    // Output file path
    protected String _filePath;

    // flag that helps us check if the storage component is actively writing to
    // disk at any point in time.
    protected boolean _isFileOpen = false;

    // name of the file written, provided by the user.
    protected String _fileName;
    protected Variable _fileVar = null;

    // extension of the file written, provided by the user.
    protected String _fileExtension;

    // the size after which we split the file
    protected long _splitSize;
    protected int _splitType;
    protected File _theFile = null;
    private int _count;

    // commands
    public final static String CMD_CLOSE = "close";
    public final static String CMD_CLOSE_FILE = "file";
    public final static String CMD_REGISTER = "register";
    private static final String TAG = "todisk";
    public static final int CLOSE_FILE = 0;

    protected List<ICommandHandler> handlers = new LinkedList<ICommandHandler>();
    //protected final Command closeCmd = new Command(this, CMD_CLOSE);

    // local buffer for java type conversion
    protected final ByteBuffer localBuffer = ByteBuffer.allocate(1024);
    private final boolean _append;

    public ToDiskComponent(String path, String filename, String extension, int splitType, long splitSize) throws CSenseException {
	super(FilenameType.type);
	// check for exceptions
	if (filename.equals(""))
	    throw new IllegalArgumentException(
		    "filename is an empty string. A filename is required for the storage component to save information.");
	if (extension.equals(""))
	    throw new IllegalArgumentException(
		    "extension is an empty string. An extension is required for the storage component to save information.");

	if (splitSize < 0)
	    throw new IllegalArgumentException(
		    "splitSize must be at least zero");

	_splitType = splitType;
	if ((_splitType != SPLIT_BY_FILESIZE)
		&& (_splitType != SPLIT_BY_INVOCATION_COUNT)
		&& (_splitType != SPLIT_NEVER)) {
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "Invalid configuration");
	}

	_splitSize = splitSize;
	_fileName = filename;
	_fileExtension = extension;

	File dir = new File(Environment.environment.getExternalStorageDirectory(), path);
	// create the directories
	dir.mkdirs();
	_filePath = dir.getAbsolutePath();
	_append = false;
    }

    public ToDiskComponent(String filename, boolean append) throws CSenseException {
	super(FilenameType.type);

	_fileName = filename;
	_fileExtension = null;
	_filePath = null;
	_splitType = SPLIT_NEVER;
	_append  = append;
    }

    public ToDiskComponent(Variable fileVar) throws CSenseException {
	super(TypeInfo.newJavaMessage(FilenameType.class));

	_fileVar = fileVar;
	_fileName = null;
	_fileExtension = null;
	_filePath = null;
	_splitType = SPLIT_NEVER;
	_append = false;
    }

    @Override
    public void onInput() throws CSenseException {
	if (_isFileOpen == false)
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR,
		    "File not open!");

	T msg = in.getFrame();
	if (msg.isEof() == false) {
	    try {
		writeToDisk(msg);
		out.push(msg);
	    } catch (IOException io) {
		io.printStackTrace();
		if(io instanceof ClosedByInterruptException) {			
		    error("I/O exception [interrupted exception]" + io + "msg: " + msg.hashCode());
		    throw new CSenseException(CSenseError.INTERRUPTED_OPERATION);
		} else {
		    error("I/O exception " + io + "msg: " + msg.hashCode());		   
		}
		//TODO: I think there is a bug in how the framing interacts with message drops
		//msg.drop();
	    }
	} else {
	    closeStream();
	    if (fileOutput.isConnected()) {
		FilenameType f = getNextMessageToWriteInto();
		f.setValue(_theFile.getAbsolutePath());
		
		fileOutput.push(f);
	    }
	}
    }

    /**
     * Creates an empty file or overwrites an existing file if there is a name
     * collision. File names should never collide because a time stamp (down to
     * milliseconds) is part of the filename.
     * 
     * @throws CSenseException
     */
    private void createFile() throws CSenseException {
	if (_filePath == null) {
	    //			_theFile = new File(
	    //					Environment.environment.getExternalStorageDirectory(),
	    //					_fileName);
	    if (_fileName == null) {
		throw new CSenseException("File name cannot be null");				
	    }
	    debug("Creating file " + _fileName);

	    _theFile = new File(_fileName);
	    try {
		_file = new RandomAccessFile(_theFile, "rw");
		if (_append) {
		    _file.seek(_file.length());
		} else { 
		    _file.setLength(0); // Set file length to 0, to prevent
		}
		// unexpected behavior in case the file
		// already existed
		_isFileOpen = true;
		info("Created file: " + _theFile);
	    } catch (FileNotFoundException e) {
		throw new CSenseException(e);
	    } catch (IOException e) {
		throw new CSenseException(e);
	    }
	} else {
	    // timestamp that is appended to the filename
	    DateFormat formatter = new SimpleDateFormat("-MMddyyyy-HHmmss.SS");
	    Date date = new Date();

	    // create a path that is in external storage, append a timestamp to
	    // the filename
	    String fn = _fileName + formatter.format(date) + _fileExtension;
	    debug("Creating file...");
	    try {
		_theFile = new File(_filePath, fn);
		_file = new RandomAccessFile(_theFile, "rw");
		_file.setLength(0); // Set file length to 0, to prevent
		// unexpected behavior in case the file
		// already existed
		_isFileOpen = true;
		info("Created file: " + _theFile);
	    } catch (FileNotFoundException e) {
		throw new CSenseException(e);
	    } catch (IOException e) {
		throw new CSenseException(e);
	    }
	}
    }

    public void writeToDisk(String str) throws IOException {
	try {
	    if (_file != null) {
		localBuffer.clear();
		localBuffer.put(str.getBytes());
		localBuffer.flip();
		_file.getChannel().write(localBuffer);
	    }
	} catch (BufferOverflowException e) {
	    e.printStackTrace();
	    System.err.println("len=" + str.length());
	    System.err.println("str=" + str);
	    System.err.println("bytes_len=" + str.getBytes().length);
	    System.err.println("bytes=" + str.getBytes());
	    System.err.println("localBuffer=" + localBuffer);
	}
    }

    /**
     * Writes the byte array stored in the current (instance variable) _message
     * to disk.
     * 
     * @throws CSenseException
     */
    private void writeToDisk(T msg) throws IOException, CSenseException {
	if (msg instanceof RawFrame) {
	    // this is typical message
	    ByteBuffer buf = ((RawFrame) msg).getBuffer();
	    buf.rewind();		
	    int bytes = _file.getChannel().write(buf);
	    buf.rewind();
	    //		debug(String.format("wrote %d bytes to %s", bytes, _theFile.getName()));
	} else {
	    String str = msg.toString();
	    writeToDisk(str);
	}

	if (_splitType == SPLIT_NEVER) {
	} else if (_splitType == SPLIT_BY_FILESIZE) {
	    if ((_file.getChannel().size() > _splitSize)
		    && (_splitSize > 0)) {
		debug("splitting the file");

		//
		if (fileOutput.isConnected()) {
		    FilenameType f = getNextMessageToWriteInto();
		    f.setValue(_theFile.getAbsolutePath());
		    fileOutput.push(f);
		}

		closeStream();
		createFile();
	    }
	} else if (_splitType == SPLIT_BY_INVOCATION_COUNT) {
	    _count += 1;
	    if (_count == _splitSize) {
		debug("splitting the file");

		//
		if (fileOutput.isConnected()) {
		    FilenameType f = getNextMessageToWriteInto();
		    f.setValue(_theFile.getAbsolutePath());
		    fileOutput.push(f);
		}

		closeStream();
		createFile();
		_count = 0;
	    }
	}
    } 


    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();

	if (_fileVar != null) {
	    _fileName = (String) Workspace.getWorkspace().getValue(_fileVar);	
	    if (_fileName == null) throw new CSenseException("Misconfigration -- filename cannot be null");
	}

	createFile();
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	if (_isFileOpen) {
	    closeStream();
	}
    }

    /**
     * Close the file we're writing data to.
     * @throws CSenseException 
     */
    private void closeStream() throws CSenseException {
	try {
	    if (null != _file) {
		_file.close();
		_isFileOpen = false;		
		if(_theFile.length() == 0) {
		    _theFile.delete();
		    warn("delete empty file", _theFile.getName());
		} else {
		    feedback(ToDiskComponent.CLOSE_FILE, new Feedback<String>(null, _theFile.getAbsolutePath()));
		}
		_file = null;
	    }			
	} catch (IOException e) {
	    error("I/O exception occured while closing output file");
	}
    }
}
