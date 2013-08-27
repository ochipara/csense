package components.bluetooth.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import components.bluetooth.BluetoothCommand;
import components.bluetooth.BluetoothCommand.Type;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class BluetoothClientService implements Runnable {    
    private BluetoothListener _listener;
    private BluetoothDevice _device;
    
    private BluetoothSocket _sock;
    private InputStream _in;
    private OutputStream _out;
    private ByteBuffer _response;
    
    private FramePool<BluetoothCommand> _cmdPool;
    private BlockingQueue<BluetoothCommand> _cmds;
    private Thread _thread;
    private boolean _running;

    /**
     * Constructs a client connection service used by both client and server components 
     * with default response buffer size of 64 bytes.
     * @param socket a connected client socket
     * @param channel the channel assigned to the socket which corresponds to an input port
     * @param listener the protocol handler
     */
    public BluetoothClientService(BluetoothSocket socket, BluetoothListener listener) {
	this(socket, 64, listener);
    }

    /**
     * Constructs a client connection service used by both client and server components.
     * @param socket a connected client socket
     * @param channel the channel assigned to the socket which corresponds to an input port
     * @param bufferSize the size of the response buffer in bytes
     * @param listener the protocol handler
     */
    public BluetoothClientService(BluetoothSocket socket, int bufferSize, BluetoothListener listener) {
	_sock = socket;
	try {
	    _in = _sock.getInputStream();
	    _out = _sock.getOutputStream();
	} catch (IOException e) {
	    throw new CSenseRuntimeException("Failed to get input and output streams.", e);
	}

	try {
	    _cmdPool = CSense.getImplementation().newFramePool(new TypeInfo<BluetoothCommand>(BluetoothCommand.class, 1, 64, 1, false, false), 4);
	} catch (CSenseException e) {
	    throw new CSenseRuntimeException(e);
	}
	_cmds = new ArrayBlockingQueue<BluetoothCommand>(4);
	_response = ByteBuffer.allocate(bufferSize);
	_response.order(ByteOrder.LITTLE_ENDIAN);
	_listener = listener;
	_device = _sock.getRemoteDevice();
    }

    /**
     * Construct a client connection service used by both client and server components
     * with default response buffer size of 64 bytes.
     * @param device a remote Bluetooth device to connect
     * @param channel the channel assigned to the socket which corresponds to an input port
     * @param bufferSize the size of the response buffer in bytes
     * @param listener the protocol handler
     */
    protected BluetoothClientService(BluetoothDevice device, BluetoothListener listener) {
	this(device, 64, listener);
    }

    /**
     * Construct a client connection service used by both client and server components.
     * @param device a remote Bluetooth device to connect
     * @param channel the channel assigned to the socket which corresponds to an input port
     * @param bufferSize the size of the response buffer in bytes
     * @param listener the protocol handler
     */
    protected BluetoothClientService(BluetoothDevice device, int bufferSize, BluetoothListener listener) {
	_device = device;
	try {
	    _sock = _device.createRfcommSocketToServiceRecord(Bluetooth.UUID_SPP);
	} catch (IllegalArgumentException e) {
	    throw new CSenseRuntimeException(e);
	} catch (IOException e) {
	    throw new CSenseRuntimeException(e);
	}
	
//	try {
//	    Method m = _device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//	    _sock = (BluetoothSocket) m.invoke(_device, Integer.valueOf(1));    
//	} catch (NoSuchMethodException e) {
//	    throw new CSenseRuntimeException(e);
//	} catch (IllegalAccessException e) {
//	    throw new CSenseRuntimeException(e);
//	} catch (InvocationTargetException e) {
//	    throw new CSenseRuntimeException(e);
//	}	

	try {
	    _cmdPool = CSense.getImplementation().newFramePool(new TypeInfo<BluetoothCommand>(BluetoothCommand.class, 1, 64, 1, false, false), 4);
	} catch (CSenseException e) {
	    throw new CSenseRuntimeException(e);
	}
	_cmds = new ArrayBlockingQueue<BluetoothCommand>(4);
	_response = ByteBuffer.allocate(bufferSize);
	_response.order(ByteOrder.LITTLE_ENDIAN);
	_listener = listener;
    }
    
    /**
     * Returns a Bluetooth command message to send.
     * @return A Bluetooth command message to send
     */
    public BluetoothCommand getCommand() {
	return _cmdPool.get();
    }

    /**
     * Returns the Bluetooth device associated with the client connection service.
     * @return the Bluetooth device associated with the client connection service
     */
    public BluetoothDevice getDevice() {
	return _device;
    }

    /**
     * Returns if the client socket is connected.
     * @return true if the client sokcket is connected or false otherwise
     */
    public boolean isConnected() {
	return _sock == null ? false : _sock.isConnected();
    }

    /**
     * Instructs the client connection service to connect.
     */
    public void connect() {
	BluetoothCommand cmd = getCommand();
	cmd.setType(Type.CONNECT);
	send(cmd);
	return;
    }
    
    /**
     * Instructs the client connection service to disconnect.
     */
    public void disconnect() {
	BluetoothCommand cmd = getCommand();
	cmd.setType(Type.DISCONNECT);
	send(cmd);
	return;
    }

    /**
     * Instructs the client connection service to send data without reading anything back.
     * @param data the data to send
     */
    public void send(ByteBuffer data) {
	send(data, 0);
    }
    
    /**
     * Instructs the client connection service to send data and receive response of the specified size.
     * @param data the data to send
     * @param bytesToRead the size of the response to read in bytes
     */
    public void send(ByteBuffer data, int bytesToRead) {
	BluetoothCommand cmd = getCommand();
	cmd.setType(Type.RAW);
	cmd.bytesToRead(bytesToRead);
	cmd.put(data);
	cmd.flip();
	send(cmd);
	return;
    }
    
    /**
     * Instructs the client connection service to send the Bluetooth command.
     * @param cmd
     */
    public void send(BluetoothCommand cmd) {
	while(true) {
	    try {
		_cmds.put(cmd);
		break;
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	return;
    }
    
    /**
     * Instructs the client connection service to receive response of the specified size.
     * @param cmd
     */
    public void recv(int bytes) {
	BluetoothCommand cmd = _cmdPool.get();
	cmd.setType(Type.READ);
	cmd.bytesToRead(bytes);
	send(cmd);
	return;
    }
    
    /**
     * Starts the client connection service thread.
     */
    public void start() {
	if(_thread == null) {
	    _thread = new CSenseInnerThread(this, _device.getName());
	    _thread.start();
	} else
	    throw new CSenseRuntimeException(new IllegalThreadStateException());
	
    }
    
    /**
     * Returns if the client connection service thread is alive.
     * @return true if the client connection service thread is alive or false otherwise
     */
    public boolean isActive() {
	return _thread == null ? false : _thread.isAlive();
    }

    /**
     * Stops the client connection service thread.
     */
    public void stop() {
	BluetoothCommand cmd = getCommand();
	cmd.setType(Type.STOP);
	send(cmd);
	while(Thread.currentThread() != _thread) {
	    try {
		_thread.join();
		break;
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	return;
    }

    /**
     * The service loop that continues processing the input Bluetooth commands.
     */
    @Override
    public void run() {
	if(_sock.isConnected() && _listener != null) _listener.onClientConnected(this);
	int _retry = 0;
	_running = true;
	while(_running) {
	    BluetoothCommand cmd = null;
	    try {
		cmd = _cmds.take();
	    } catch(InterruptedException ex) {
		ex.printStackTrace();
		continue;
	    }

	    _response.clear();	    
	    switch(cmd.getType()) {
	    case CONNECT:
		try {
		    Log.d(getClass().getSimpleName(), "trying to connect", _device.getName());
		    _sock.connect();
		    _in = _sock.getInputStream();
		    _out = _sock.getOutputStream();
		    Log.i(getClass().getSimpleName(), "connected to", _device.getName());
		    if(_listener != null) _listener.onClientConnected(this);
		} catch(IOException e) {
		    if(_retry++ < 3) {
			Log.w(getClass().getSimpleName(), "failed to connect", _device.getName(), "due to", e.getMessage(), "[retry]");
			connect();
		    } else {
			Log.w(getClass().getSimpleName(), "failed to connect", _device.getName(), "due to", e.getMessage(), "[exit]");
			_running = false;
			break;
		    }
		}
		break;
	    case DISCONNECT:
		if(_listener != null) _listener.onDisconnect(this);
		break;
	    case STOP:
		Log.i(getClass().getSimpleName(), "client service stopped ");
		_running = false;
		break;
	    case READ:
		try {		
//		    Log.d(getClass().getSimpleName(), cmd.bytesToRead(), "bytes to read");
		    int bytes = 0;
		    while(bytes < cmd.bytesToRead()) {
			bytes += _in.read(_response.array(), bytes, cmd.bytesToRead() - bytes);
		    }
		    _response.limit(bytes);
		    _response.rewind();
//		     Log.d(getClass().getSimpleName(), "read ",  _response.remaining(), "bytes of response");
		    if(_listener != null) _listener.onResponse(this, cmd, _response);
		} catch(IOException e) {
		    Log.w(getClass().getSimpleName(), "failed to read Bluetooth socket due to", e, ", quit");
		    _running = false;
		}
		break;
	    case RAW:
		try {
		    cmd.getBuffer().mark();
		    _out.write(cmd.bytes(), cmd.position(), cmd.remaining());
		    cmd.getBuffer().reset();	   						   			
		    if(cmd.bytesToRead() > 0) {				
			int bytes = 0;
//			Log.d(getClass().getSimpleName(), "ready to read response immediately");
			while(bytes < cmd.bytesToRead())
			    bytes += _in.read(_response.array(), bytes, cmd.bytesToRead() - bytes);
			_response.limit(bytes);
			_response.rewind();
//			Log.d(getClass().getSimpleName(), "read", bytes, "bytes of response", ",", cmd.bytesToRead(), "bytes expected", ",", _response.remaining(), "bytes in buffer");
			if(_listener != null) _listener.onResponse(this, cmd, _response);
		    }
		} catch(IOException e) {
		    Log.w(getClass().getSimpleName(), "failed to read/write Bluetooth socket due to", e, ", quit");
		    _running = false;
		}
		break;		
	    }
	    cmd.free();
	}

	try {
	    if(_in != null) _in.close();
	} catch (IOException e) { 
	    Log.d(getClass().getSimpleName(), "failed to close input stream of Bluetooth socket due to", e.getMessage());
	} finally {
	    _in = null;
	}
	
	try {
	    if(_out != null) _out.close();
	} catch (IOException e) { 
	    Log.d(getClass().getSimpleName(), "failed to close output stream of Bluetooth socket due to", e.getMessage());
	} finally {
	    _out = null;
	}
	
	try {
	    _sock.close();
	} catch (IOException e) { 
	    Log.d(getClass().getSimpleName(), "failed to close Bluetooth socket due to", e.getMessage());
	} finally {
	    _sock = null;
	}
    }
}
