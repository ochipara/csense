package components.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import base.Utility;

import api.CSenseException;
import api.CSenseRuntimeException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;
import api.IResult;
import api.Message;
import messages.RawMessage;
import messages.TypeInfo;
import messages.fixed.FilenameType;

public abstract class FileOutputComponent<T extends RawMessage> extends CSenseSource<FilenameType> {    
    public final IInPort<T> in;
    public final IOutPort<T> out;
    public final IOutPort<FilenameType> outPath;

    private long _fileSizeLimitInBytes;
    private long _spaceLimitInBytes;
    private long _spaceUsedInBytes;
    private boolean _polled;
    private File _path;
    private String _extension;
    private FilenameFilter _filter;
    private FileChannel _outChannel;
    private File _outFile;

    private String _prefix;
    private Map<String, File> _outFiles = new TreeMap<String, File>();
    private Map<String, FileChannel> _outChannels = new TreeMap<String, FileChannel>();

    /**
     * @param spaceLimitInPercentage 	space limit in percentage, such as 5, 10, 50, 70 and etc.
     * @param fileSizeLimitInSamples 	recording duration in samples.
     * @param path 						storage path.
     * @param extension 				filename suffix.
     * @param numOutgoing 				number of outgoing connections for the previous message.
     * @param numOutgoing2 				number of outgoing connections for the newly generated message.
     * @throws CSenseException 
     */
    public FileOutputComponent(int spaceLimitInPercentage, int fileSizeLimitInSamples, String path, String extension) throws CSenseException {
	this(new File(path).getUsableSpace() * spaceLimitInPercentage / 100, fileSizeLimitInSamples, path, extension);
    }

    /**
     * @param spaceLimitInBytes			space limit in bytes.
     * @param fileSizeLimitInSamples	recording duration in samples.
     * @param path						storage path.
     * @param extension					filename suffix.
     * @param numOutgoing				number of outgoing connections for the previous message.
     * @param numOutgoing2				number of outgoing connections for the newly generated message.
     * @throws CSenseException 
     */
    public FileOutputComponent(long spaceLimitInBytes, int fileSizeLimitInSamples, String path, String extension) throws CSenseException {
	super(TypeInfo.newFilenameType());
	in = newInputPort(this, "in");
	out = newOutputPort(this, "out");
	outPath = newOutputPort(this, "path");

	_spaceLimitInBytes = spaceLimitInBytes;
	_fileSizeLimitInBytes = getFileSizeInBytes(fileSizeLimitInSamples);
	_extension = extension;
	_filter = new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		String lowercaseName = name.toLowerCase();
		if (lowercaseName.endsWith("." + _extension))
		    return true;
		else
		    return false;
	    }
	};
	_path = new File(path);
	if(!_path.exists() && !_path.mkdirs()) {
	    throw new CSenseRuntimeException("failed to make dir " + _path);
	}
    }

    /**
     * Returns the filename prefix.

     * @return the prefix of the filename.
     */
    protected String getPrefix(T msg) {
	return null;
    }

    /**
     * Returns the header size of the file format.
     * 
     * @return the header size in bytes.
     */
    abstract protected int getHeaderSize();

    /**
     * Returns the size of a single recording which is converted from the
     * recording duration.
     * 
     * @param secs the recording duration in seconds.
     * @return the size of a single recording in bytes.
     */
    abstract protected long getFileSizeInBytes(int samples);

    /**
     * Writes the header of the recording file to the output channel..
     * 
     * @param msg TODO
     * @return the number of bytes written.
     */
    abstract protected int writeHeader(T msg) throws IOException;

    /**
     * Advances the message timestamp after a number of samples in bytes are
     * written.
     * 
     * @param msg the message of the timestamp to update
     * @param bytes the number of samples in bytes just written.
     */
    abstract protected void advanceTimestamp(T msg, int bytes);

    /**
     * Finalizes the recording file. Some file format may require to write some
     * information in the beginning.
     */
    protected void close() throws IOException {
	if (_outChannel == null) return;
	_outFiles.remove(_prefix == null ? "" : _prefix);
	_outChannels.remove(_prefix == null ? "" : _prefix);
	_outChannel.close();
	_outChannel = null;
	_outFile = null;
    }

    protected File getOutputFile() {
	return _outFile;
    }

    protected FileChannel getOutputChannel() {
	return _outChannel;
    }

    protected long getFileSizeLimitInBytes() {
	return _fileSizeLimitInBytes;
    }

    private boolean isOutputFile(File file) {
	for (File f : _outFiles.values())
	    if (f.equals(file))	return true;

	return false;
    }

    private boolean isSpaceAvailable() {
	long limit = _spaceLimitInBytes > _path.getUsableSpace() 
		? _path.getUsableSpace() 
			: _spaceLimitInBytes;
		return _spaceUsedInBytes < limit && limit - _spaceUsedInBytes >= _fileSizeLimitInBytes;
    }

    private File getOldestOutputFile() {
	File[] files = _path.listFiles(_filter);
	if (files == null) return null;
	File oldest = null;
	for (int i = 0; i < files.length; i++) {
	    if (oldest == null)	oldest = files[i];
	    else if (files[i].lastModified() < oldest.lastModified()) oldest = files[i];
	}
	return oldest;
    }

    private File nextFileToDelete() {
	File file = isSpaceAvailable() ? null : getOldestOutputFile();
	return isOutputFile(file) ? null : file;
    }

    private File nextFileToCreate(T msg, String prefix) {
	StringBuilder filename = new StringBuilder(prefix == null ? "" : prefix + "-");
	DateFormat dateFormat = new SimpleDateFormat(("yyyyMMdd-HHmmss-SSS"));
	filename.append(dateFormat.format(msg.getTimeStamp()));
	filename.append(".");
	filename.append(_extension);
	return new File(_path, filename.toString());
    }

    private boolean recycleSpace() {
	File victim = null;
	while ((victim = nextFileToDelete()) != null) {
	    long bytes = victim.length();
	    if (victim.delete()) {
		_spaceUsedInBytes -= bytes;
		debug(victim.getName(), "is recycled to free", bytes, "bytes, space used decreases to", _spaceUsedInBytes, "bytes");
	    } else
		error("failed to recycle", victim.getName());
	}
	return isSpaceAvailable();
    }

    @Override
    public void doInput() throws CSenseException {
	T msg = in.getMessage();
	while (msg.hasRemaining()) {
	    try {
		if (msg.position() == 0) {
		    _prefix = getPrefix(msg);
		    _outFile = _outFiles.get(_prefix == null ? "" : _prefix);
		    _outChannel = _outChannels.get(_prefix == null ? "" : _prefix);
		}

		if (_outChannel == null && recycleSpace()) {
		    _outFile = nextFileToCreate(msg, _prefix);
		    _outChannel = new FileOutputStream(_outFile).getChannel();
		    _outFiles.put(_prefix == null ? "" : _prefix, _outFile);
		    _outChannels.put(_prefix == null ? "" : _prefix, _outChannel);
		    writeHeader(msg);
		    info(_outFile.getPath(), "is created a to record");
		}

		if (_outChannel.size() + msg.remaining() <= _fileSizeLimitInBytes)
		    _outChannel.write(msg.buffer());
		else {
		    int bytesToWrite = (int) (_fileSizeLimitInBytes - _outChannel.size());
		    int limit = msg.limit();
		    int offset = msg.remaining() - bytesToWrite;
		    msg.limit(limit - offset);
		    int bytes = _outChannel.write(msg.buffer());
		    msg.limit(limit);
		    advanceTimestamp(msg, bytes);
		}

		if (_outChannel.size() == _fileSizeLimitInBytes) {
		    _spaceUsedInBytes += _fileSizeLimitInBytes;
		    info("finished recording", _outFile.getName(), "while increasing space used to", _spaceUsedInBytes, "bytes");
		    close();
		    if(_polled)	doOutput();
		}
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
		throw new CSenseException(e);
	    } catch (IOException e) {
		e.printStackTrace();
		throw new CSenseException(e);
	    }
	}

	out.push(msg);
    }

    @Override
    public void onStart() throws CSenseException {
	Utility.clearDirectory(_path);
	File[] files = _path.listFiles(_filter);
	for (int i = 0; i < files.length; i++)
	    _spaceUsedInBytes += files[i].length();
	info(files.length, "files exist in storage path", _path.getPath());
	info("Usable space", _path.getUsableSpace() / 1024.0 / 1024 / 1024, "GB, storage limit", _spaceLimitInBytes / 1024.0, "KB, space used:", _spaceUsedInBytes, "bytes");
	info("Single recording size limit:", _fileSizeLimitInBytes, "bytes");
    }

    public void cleanup() {
	try {
	    close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	File file = getOldestOutputFile();
	if (file == null) in.poll();
	_polled = true;
	return null;
    }

    protected void doOutput() throws CSenseException {
	File file = getOldestOutputFile();
	if (file != null && !isOutputFile(file)) {
	    FilenameType outMsg = getNextMessageToWriteInto();
	    outMsg.put(file.getPath());
	    outMsg.flip();
	    long size = file.length();
	    if (outPath.push(outMsg) == IResult.PUSH_SUCCESS) {
		_spaceUsedInBytes -= size;
		info("notified to upload", file.getName(), "while decreasing space used to", _spaceUsedInBytes, "bytes");
	    } else {
		warn("failed to push a message to satisfy a pull request because the requester may restart, cancel the request");
		_polled = false;
		outMsg.free();
	    }
	} else
	    info("output file recording is not finished yet, wait for a moment");
    }
}