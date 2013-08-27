package components.bluetooth.android;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.compatibility.Log;

public class BluetoothClient {
    private final BroadcastReceiver _receiver;
    private Bluetooth _bt;
    private BluetoothListener _listener;
    private List<BluetoothClientService> _services;
    private String _prefix;
    private int _max;
    private int _bufferSize;

    /**
     * Constructs a Bluetooth client with necessary information and default response buffer of 64 bytes.
     * @param context the application context to access Android Bluetooth radio
     * @param prefix the prefix of remote Bluetooth devices
     * @param max the maximum number of Bluetooth devices to connect
     * @param listener the protocol handler
     * @throws CSenseException if IOutPort.push() fails
     */
    public BluetoothClient(Context context, String prefix, int max, BluetoothListener listener) {
	this(context, prefix, max, 64, listener);
    }

    /**
     * Constructs a Bluetooth client with necessary information.
     * @param context the application context to access Android Bluetooth radio
     * @param prefix the prefix of remote Bluetooth devices
     * @param max the maximum number of Bluetooth devices to connect
     * @param bufferSize the size of the response buffer in bytes
     * @param listener the protocol handler
     * @throws CSenseException if IOutPort.push() fails
     */
    public BluetoothClient(Context context, String prefix, int max, int bufferSize, BluetoothListener listener) {
	_prefix = prefix;
	_max = max;
	_bufferSize = bufferSize;
	_listener = listener;
	_services = new ArrayList<BluetoothClientService>();
	_receiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
		if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
		    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    if(device.getName().startsWith(_prefix)) {
			if(isDeviceInService(device)) return;
			BluetoothClientService service = new BluetoothClientService(device, _bufferSize, _listener);            	
			_services.add(service);
			Log.i(getClass().getSimpleName(), "add discovered device: ", device.getName(), " at ", device.getAddress());
		    }
		    if(_services.size() == _max) _bt.cancelDiscovery();
		}
	    }
	};
	_bt = new Bluetooth(context, _receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    /**
     * Returns if the client connection of a particular channel is still active.
     * @param channel the channel of the specified client connection service
     * @return true if the client connection service is active, or false otherwise
     */
    public boolean isActive(int channel) {
	return channel >=0 && channel < _services.size() ? _services.get(channel).isActive() : false;
    }

    /**
     * Returns if the client channel is connected.
     * @param channel the channel of the specified client connection service
     * @return true if the client connection service is connected, or false otherwise
     */
    public boolean isConnected(int channel) {
	return channel >=0 && channel < _services.size() ? _services.get(channel).isConnected() : false;
    }

    private boolean isDeviceInService(BluetoothDevice device) {
	boolean found = false;
	for(int i = 0 ; i < _services.size() && !found; i++) {
	    BluetoothClientService service = _services.get(i);
	    if(service.getDevice().getName().equals(device.getName())) found = true;
	}

	return found;
    }
    
    /**
     * Returns the number of connected client connection services.
     * @return the number of connected client connection services
     */
    public int getConnections() {
	int connections = 0;
	for(int i = 0; i < _services.size(); i++)
	    if(_services.get(i).isConnected()) connections++;

	return connections;
    }
    
    /**
     * Sends data to the specified remote Bluetooth device channel.
     * @param ch the specified remote Bluetooth device channel
     * @param data the data to send
     */
    public void send(int ch, ByteBuffer data) {
	_services.get(ch).send(data);
	return;
    }

    /**
     * Finds paired devices first and initiates the discovery if necessary.
     * @return the total number of remote Bluetooth devices found
     */
    public int discover() {
	Set<BluetoothDevice> pairedDevices = _bt.getBondedDevices();
	if (pairedDevices.size() > 0) {
	    for (BluetoothDevice device: pairedDevices) {
		if(device.getName().startsWith(_prefix)) {	    		
		    if(isDeviceInService(device)) continue;
		    _services.add(new BluetoothClientService(device, _bufferSize, _listener));
		    Log.i(getClass().getSimpleName(), "add paired device:", device.getName(), "at", device.getAddress());
		}
	    }
	}

	if(_services.size() >= _max) return _services.size();
	_bt.startDiscovery();
	return _services.size();
    }

    /**
     * Starts client connection services to connect.
     */
    public void start() {
	_bt.cancelDiscovery();
	Log.i(getClass().getSimpleName(), "start Bluetooth client services");
	for(int i = 0; i < _services.size(); i++) {
	    _services.get(i).start();
	    _services.get(i).connect();
	}
    }

    /**
     * Stops all the client connection services.
     */
    public void stop() {
	while(!_services.isEmpty()) {
	    BluetoothClientService service = _services.get(0);
	    if(service.isConnected()) service.disconnect();
	    if(service.isActive()) service.stop();
	    _services.remove(0);
	}
	_bt.cancelDiscovery();
	_bt.disable();
    }
}
