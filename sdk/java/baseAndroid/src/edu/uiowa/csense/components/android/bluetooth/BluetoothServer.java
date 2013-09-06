package edu.uiowa.csense.components.android.bluetooth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import android.bluetooth.BluetoothServerSocket;

public class BluetoothServer implements Runnable {
    private Bluetooth _bt;
    private BluetoothListener _listener;  
    private List<BluetoothClientService> _clients = new ArrayList<BluetoothClientService>();
    private String _name;
    private UUID _uuid;
    private Thread _thread;
    private boolean _running;
            
    /**
     * Constructs a Bluetooth device server with necessary information and a protocol handler.
     * @param name the device name
     * @param uuid the UUID of the Bluetooth communication
     * @param listener the protocol handler
     */
    public BluetoothServer(String name, UUID uuid, BluetoothListener listener) {
	_bt = new Bluetooth();
	_name = name;
	_uuid = uuid;
	_listener = listener;
    }

    public String getName() {
	return _name;
    }
    
    /**
     * Sends data to the specified remote Bluetooth device channel.
     * @param ch the specified remote Bluetooth device channel
     * @param data the data to send
     */
    public void send(int ch, ByteBuffer data) {
	_clients.get(ch).send(data);
	return;
    }
     
    /**
     * Starts the server thread to accept client connections.
     */
    public void start() {
	if(_thread == null) {
	    _thread = new CSenseInnerThread(this, _name);
	    _thread.start();
	} else
	    throw new CSenseRuntimeException(new IllegalThreadStateException());
	
    }
    
    /**
     * Returns if the server thread is alive.
     * @return ture if the server thread is alive, or false otherwise.
     */
    public boolean isActive() {
	return _thread == null ? false : _thread.isAlive();
    }
    
    /**
     * Stops the server thread.
     */
    public void stop() {
	_running = false;
	while(Thread.currentThread() != _thread) {
	    try {
		_thread.join();
		break;
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }
    
    /**
     * The server thread loop that continues accepting incoming client connections.
     */
    @Override
    public void run() {
	_bt.enable();
	BluetoothServerSocket _sock = null;
	try {
	    _sock = _bt.listen(_name, _uuid);
	} catch (IOException e) {
	    throw new CSenseRuntimeException(e);
	}
	_running = true;
	while(_running) {
	    try {
		BluetoothClientService client = new BluetoothClientService(_sock.accept(), _clients.size(), _listener);
		_clients.add(client); 
		client.start();
	    } catch (IOException e) {
		e.printStackTrace();
		_running = false;
	    }
	}

	while(!_clients.isEmpty()) {
	    BluetoothClientService client = _clients.get(0);
	    client.stop();
	    _clients.remove(0);
	}
	
	try {
	    if(_sock != null) _sock.close();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    _thread = null;
	    _bt.disable();	    
	}
    }
}
