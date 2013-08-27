package edu.uiowa.csense.components.network;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.StringTokenizer;

import edu.uiowa.csense.profiler.*;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.FilenameType;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

/*
 * TODO
 * test cases:
 * 1. server is down => timeout retry
 * 2. wireless is unavailable or disabled => timeout retry
 * 3. server crash or connectivity loss => timeout retry
 * 4. weak signal
 * 5. unexpected connection close
 */

public class FTPClientComponent extends CSenseComponent {
    InputPort<FilenameType> in;
    OutputPort<FilenameType> out;

    static protected final String CRLF = "\r\n";
    static protected final long CONNECTION_TIMEOUT = 15 * 1000;
    static protected final long PULL_REQUEST_TIMEOUT = 5 * 1000;

    private SocketChannel _server;
    private SocketChannel _dataChannel;
    private TimerTask _serverConnectionTimerTask = new TimerTask() {
	@Override
	public void run() {
	    boolean connected = _server.isConnected();
	    if (connected && _alive) {
		_alive = false;
		getOwner().getScheduler().schedule(getOwner(), this,
			CONNECTION_TIMEOUT);
		return;
	    }

	    if (!connected)
	    	;//Log.w(this, "[%s] failed to connect to %s:%d, retry", getOwner(), _host, _port);
	    else
	    	;//Log.w(this, "[%s] connection to %s:%d is not alive, retry", getOwner(), _host, _port);
	    try {
	    	restart();
	    } catch (CSenseException e) {
		throw new CSenseRuntimeException(Utility.toString(
			"failed to restart %s", getClass().getName()), e);
	    }
	}
    };

    private TimerTask _filePullTimerTask = new TimerTask() {
		@Override
		public void run() {
		    try {
				in.poll();
			} catch (CSenseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		    Log.d(this, "[%s] request to pull a file to upload is %s", getOwner(), ret ? "accepted" : "rejected");
			getOwner().getScheduler().schedule(getOwner(), this, PULL_REQUEST_TIMEOUT);
		}
    };

    private StringTokenizer _rds;
    private File _lwd;
    private File _file;
    private int _port = -1;
    private int _dataPort;
    private String _dataIP;
    private String _host;
    private String _user;
    private String _pass;
    private String _rwd;
    private String _cwd;
    private String _cmd;

    private FileChannel _fileChannel;
    private ByteBuffer _inCmdBuf;
    private ByteBuffer _outCmdBuf;
    private ByteBuffer _outDataBuf;
    private boolean _alive;
    private boolean _ready;
    private boolean _dataConnectionReady;

    /**
     * Constructs a instance of FTPClientComponent.
     * 
     * @param lwd
     *            local working directory from which the client uploads files.
     * @param host
     *            remote server IP
     * @param rwd
     *            remote server working directory in which the uploaded files
     *            are stored.
     * @param outGoingConnections
     *            number of the outgoing connections of the component.
     * @throws CSenseException 
     */
    public FTPClientComponent(String lwd, String host, String rwd) throws CSenseException {
    	this(lwd, host, "anonymous", "anonymous", rwd);
    }

    /**
     * Constructs a instance of FTPClientComponent.
     * 
     * @param lwd
     *            local working directory from which the client uploads files.
     * @param host
     *            remote server IP
     * @param user
     *            login username
     * @param pass
     *            login password
     * @param rwd
     *            remote server working directory in which the uploaded files
     *            are stored.
     * @param outGoingConnections
     *            number of the outgoing connections of the component.
     * @throws CSenseException 
     */
    public FTPClientComponent(String lwd, String host, String user, String pass, String rwd) throws CSenseException {
    	this(lwd, host, 21, user, pass, rwd);
    }

    /**
     * Constructs a instance of FTPClientComponent.
     * 
     * @param lwd
     *            local working directory from which the client uploads files.
     * @param host
     *            remote server IP
     * @param port
     *            remote server port
     * @param user
     *            login username
     * @param pass
     *            login password
     * @param rwd
     *            remote server working directory in which the uploaded files
     *            are stored.
     * @param outGoingConnections
     *            number of the outgoing connections of the component.
     * @throws CSenseException 
     */
    public FTPClientComponent(String lwd, String host, int port, String user, String pass, String rwd) throws CSenseException {
	_lwd = new File(lwd);
	_host = host;
	_port = port;
	_user = user;
	_pass = pass;
	_rwd = rwd;
	_inCmdBuf = ByteBuffer.allocate(128);
	_outCmdBuf = ByteBuffer.allocate(64);
	_outDataBuf = ByteBuffer.allocate(512 * 1024);
	in = newInputPort(this, "in");
	out = newOutputPort(this, "out");
    }

    public boolean isReady() {
	return _ready;
    }

    public void connect() throws IOException {
	if (_ready)
	    return;
	_server = SocketChannel.open();
	_server.configureBlocking(false);
	_server.connect(new InetSocketAddress(_host, _port));
	getScheduler().registerChannel(_server, SelectionKey.OP_CONNECT, this);
	getScheduler().schedule(this, _serverConnectionTimerTask,
		CONNECTION_TIMEOUT);
    }

    public void disconnect() {
	try {
	    send("QUIT");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void close() throws IOException {
	try {
	    _server.close();
	} catch (IOException e) {
	}
	_inCmdBuf.clear();
	_outCmdBuf.clear();
	_outDataBuf.clear();
	if (_fileChannel != null)
	    _fileChannel.close();
	if (_dataChannel != null)
	    _dataChannel.close();
	_fileChannel = null;
	_dataChannel = null;
	_file = null;
	_ready = false;
	_alive = false;
	_dataConnectionReady = false;
	getScheduler().cancel(_serverConnectionTimerTask);
	getScheduler().cancel(_filePullTimerTask);
    }

    public void send(String cmd) throws IOException {
	_cmd = cmd;
	_outCmdBuf.put(cmd.getBytes("US-ASCII"));
	_outCmdBuf.put(CRLF.getBytes("US-ASCII"));
	_outCmdBuf.flip();
	getScheduler().registerChannel(_server, SelectionKey.OP_WRITE, this);
    }

    public void send(String cmd, String arg) throws IOException {
	String line = cmd + " " + arg;
	_cmd = cmd;
	_outCmdBuf.put(line.getBytes("US-ASCII"));
	_outCmdBuf.put(CRLF.getBytes("US-ASCII"));
	_outCmdBuf.flip();
	getScheduler().registerChannel(_server, SelectionKey.OP_WRITE, this);
    }

    public String getResponse() throws IOException {
	int bytes = _server.read(_inCmdBuf);
	if (bytes > 0) {
	    int pos = _inCmdBuf.position();
	    if (pos >= CRLF.length() && _inCmdBuf.get(--pos) == '\n'
		    && _inCmdBuf.get(--pos) == '\r') {
		_inCmdBuf.put((byte) '\0');
		_inCmdBuf.flip();
		String response = new String(_inCmdBuf.array(), 0,
			_inCmdBuf.remaining() - 3, "US-ASCII");
		_inCmdBuf.clear();
		return response;
	    }
	} else if (bytes == 0)
	    ;//Log.w(this, "nothing read");
	else if (bytes == -1) {
	    getScheduler().registerChannel(_server, 0, this);
	    throw new ClosedChannelException();
	}

	return null;
    }

    @SuppressWarnings("resource")
    public boolean upload(String filename) {
	try {
	    File src = new File(filename);
	    File dest = new File(_lwd.getPath() + File.separator
		    + src.getName());
	    if (!src.renameTo(dest)) {
//		Log.e(this, "failed to move %s to %s", src.getPath(), dest.getPath());
		return false;
	    }
	    _fileChannel = new FileInputStream(dest).getChannel();
	    _file = dest;
	    send("PASV");
	    return true;
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	return false;
    }

    public void stor(String ip, int port) throws IOException {
	if (!_ready)
	    return;
	send("STOR", _file.getName());
	_dataChannel = SocketChannel.open();
	_dataChannel.configureBlocking(false);
	_dataChannel.connect(new InetSocketAddress(ip, port));
	getScheduler().registerChannel(_dataChannel, SelectionKey.OP_CONNECT,
		this);
    }

    public void doInput(SelectionKey key) throws IOException, CSenseException {
	if (key.isConnectable()) {
	    if (key.channel() == _server) {
		if (_server.finishConnect()) {
		    key.interestOps(SelectionKey.OP_READ);
//		    Log.i(this, "connected to %s:%d", _host, _port);
		}
	    } else if (key.channel() == _dataChannel) {
		if (_dataChannel.finishConnect()) {
		    if (_fileChannel.read(_outDataBuf) > 0)
			_outDataBuf.flip();
		    key.interestOps(SelectionKey.OP_WRITE);
//		    Log.i(this, "established data connection to %s",
//			    _dataChannel.socket().getRemoteSocketAddress()
//				    .toString());
		}
	    }
	}

	if (key.isReadable()) {
	    _alive = true;
	    String response = getResponse();
	    if (response == null) {
//		Log.w(this, "response is not ready yet");
		return;
	    }
//	    Log.d(this, "RESPONSE: %s", response);
	    if (response.startsWith("220 ")) { // server ready
		send("USER", _user);
	    } else if (response.startsWith("331 ")) { // User xxx accepted,
						      // provide password
		send("PASS", _pass);
	    } else if (response.startsWith("230 ")) { // User xxx logged in
		_rds = new StringTokenizer(_rwd, File.separator);
		_cwd = _rds.nextToken();
		send("MKD", _cwd);
	    } else if (response.startsWith("550 ")) { // [MKD | SIZE] failed,
						      // File exists or No
						      // such file or
						      // directory
		if (_cmd == "MKD")
		    send("CWD", _cwd);
		else if (_cmd == "SIZE")
		    send("PASV");
	    } else if (response.startsWith("257 ")) { // [PWD| MKD] succeeded,
						      // directory created
		send("CWD", _cwd);
	    } else if (response.startsWith("250 ")) { // CWD command successful
		if (_rds.hasMoreTokens()) {
		    _cwd = _rds.nextToken();
		    send("MKD", _cwd);
		} else
		    send("TYPE", "I");
	    } else if (response.startsWith("200 ")) { // Type set to I
		_ready = true;
		File[] files = _lwd.listFiles();
		if (files.length > 0) {
		    for (int i = 0; i < files.length; i++) {
			if (files[i].getName() == "."
				|| files[i].getName() == "..")
			    continue;
//			Log.i(this, "uploading %s might be resumed", files[i].getName());
			_fileChannel = new FileInputStream(files[i])
				.getChannel();
			_file = files[i];
			send("SIZE", _file.getName());
			break;
		    }
		} else {
			in.poll();
//		    if (in.poll())
//		    	;//Log.d(this, "ready, request to pull a file to upload is accepted");
//		    else {
//		    	//Log.d(this, "ready, request to pull a file to upload is rejected, schedule a timer task to pull");
//		    	getScheduler().schedule(this, _filePullTimerTask, PULL_REQUEST_TIMEOUT);
//		    }
		}
	    } else if (response.startsWith("213 ")) { // file exists and the
						      // size is returned
		long size = Long.parseLong(response.substring(4));
		_fileChannel.position(size);
//		Log.i(this, "resumes uploading %s from position %d", _file.getName(), size);
		send("PASV");
	    } else if (response.startsWith("350 ")) { // restarting at xxx
		stor(_dataIP, _dataPort);
	    } else if (response.startsWith("227 ")) { // enter passive mode
		_dataIP = null;
		_dataPort = -1;
		int opening = response.indexOf('(');
		int closing = response.indexOf(')', opening + 1);
		if (closing > 0) {
		    String address = response.substring(opening + 1, closing);
		    StringTokenizer tokenizer = new StringTokenizer(address,
			    ",");
		    _dataIP = tokenizer.nextToken() + "."
			    + tokenizer.nextToken() + "."
			    + tokenizer.nextToken() + "."
			    + tokenizer.nextToken();
		    _dataPort = Integer.parseInt(tokenizer.nextToken()) * 256
			    + Integer.parseInt(tokenizer.nextToken());
		    if (_fileChannel.position() > 0)
			send("REST", String.valueOf(_fileChannel.position()));
		    else
			stor(_dataIP, _dataPort);
		}
	    } else if (response.startsWith("150 ")) { // file status OK |
						      // opening BINARY mode
						      // data connection
		_dataConnectionReady = true;
	    } else if (response.startsWith("226 ")) { // Closing data
						      // connection. Requested
						      // file action
						      // successful
//		Log.i(this, "uploaded %s", _file.getName());
		_fileChannel.close();
		_fileChannel = null;
		_file.delete();
		_file = null;
		File[] files = _lwd.listFiles();
		if (files.length > 0) {
		    for (int i = 0; i < files.length; i++) {
			if (files[i].getName() == "."
				|| files[i].getName() == "..")
			    continue;
//			Log.i(this, "uploading %s might be resumed", files[i].getName());
			_fileChannel = new FileInputStream(files[i])
				.getChannel();
			_file = files[i];
			send("SIZE", _file.getName());
			break;
		    }
		} else {
			in.poll();
//		    if (in.poll())
//		    	Log.d(this, "transfer complete, request to pull the next file to upload is accepted");
//		    else {
//		    	Log.d(this, "transfer complete, request to pull the next file to upload is rejected, schedule a timer task to pull");
//			getScheduler().schedule(this, _filePullTimerTask, PULL_REQUEST_TIMEOUT);
//		    }
		}
		return;
	    } else if (response.startsWith("221 ")) { // Server close
		close();
		return;
	    }
	} else if (key.isWritable()) {
	    _alive = true;
	    if (key.channel() == _server) {
		_server.write(_outCmdBuf);
		if (!_outCmdBuf.hasRemaining()) {
		    _outCmdBuf.clear();
		    key.interestOps(SelectionKey.OP_READ);
		}
	    } else if (key.channel() == _dataChannel) {
		if (!_dataConnectionReady) {
//		    Log.w(this, "data connection is not ready yet");
		    return;
		}

		_dataChannel.write(_outDataBuf);
		if (_outDataBuf.hasRemaining())
		    return;

		_outDataBuf.clear();
		if (_fileChannel.read(_outDataBuf) == -1) {
//		    Log.d(this, "close file and data channels of %s", _file.getName());
		    _dataConnectionReady = false;
		    _dataChannel.close();
		    _fileChannel.close();
		}
		_outDataBuf.flip();
	    }
	}
    }

//    protected boolean accept(InPort<? extends Message> port, Message msg) {
//	boolean available = _ready && (_file == null && _fileChannel == null);
//	if (!_ready)
//	    Log.w(this, "not ready yet");
//	else if (_file != null || _fileChannel != null)
//	    Log.w(this, "still uploading");
//
//	return available;
//    }

//    @Override
//    protected boolean mayPull(OutPort<? extends Message> port) {
//	return false;
//    }

    @Override
    public void onInput() throws CSenseException {
		FilenameType msg = in.getFrame();
		upload(msg.getString());
		out.push(msg);
    }

    @Override
    public void onStart() throws CSenseException {
		if (!_lwd.exists())
		    _lwd.mkdirs();
		// Utility.clearDirectory(_lwd);
	//	Log.i(this, "Local working directory %s", _lwd.getPath());
	//	Log.i(this, "Remote working directory %s", _rwd);
		activate();
    }

    public void activate() throws CSenseException {
	try {
	    connect();
	} catch (IOException e) {
	    throw new CSenseException(e);
	}
    }

    @Override
    public void onStop() throws CSenseException {
	disconnect();
	try {
	    close();
	} catch (IOException e) {
	    throw new CSenseException(e);
	}
    }

    public void handleException(CSenseException e) {
	Throwable cause = e.getCause();
	boolean restart = false;
	if (cause instanceof SocketException) {
	    if (cause.getMessage().indexOf("EHOSTUNREACH") > 0) {
//		Log.w(this, "host is unreachable, restart later");
	    } else {
//		Log.w(this, "%s, restart", e.getMessage());
		restart = true;
	    }
	} else if (cause instanceof ClosedChannelException)
	    ;//Log.w(this, "socket connection closed, restart later");
	else if (cause instanceof SocketTimeoutException) {
//	    Log.w(this, "socket connection timeout, restart");
	    restart = true;
	} else
	    ;//super.handleException(e);

	if (restart) {
	    try {
		restart();
	    } catch (CSenseException ex) {
		//super.handleException(ex);
	    }
	}
    }
    
    void restart() throws CSenseException {
    	onStop();
    	onStart();
    }
}
