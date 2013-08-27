package components.bluetooth.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.uiowa.csense.runtime.compatibility.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class Bluetooth {
    public  static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public  static final UUID UUID_RFCOMM = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");
    private static BluetoothAdapter _adapter;
    private static List<Bluetooth> _enables;
    private static List<Bluetooth> _discoveries;
    private static boolean _enabled;
    private static boolean _discoverying;
    static {
	_adapter = BluetoothAdapter.getDefaultAdapter();
	_enabled = _adapter != null && _adapter.isEnabled();
	_discoverying = _adapter != null && _adapter.isDiscovering();
	_enables = new ArrayList<Bluetooth>();
	_discoveries = new ArrayList<Bluetooth>();
    }
    
    private Context _context;
    private BroadcastReceiver _receiver;
    private IntentFilter _filter;
    
    public Bluetooth() {
	this(null, null, null);
    }
    
    public Bluetooth(Context context, BroadcastReceiver receiver, IntentFilter filter) {
	_context = context;
	_receiver = receiver;
	_filter = filter;
	enable();
	if(_discoverying) {
	    Log.w(getClass().getSimpleName(), "discovery already started, cancel it in case");
	    if(isAvailable()) _adapter.cancelDiscovery();
	}
    }
    
    public boolean isAvailable() {
	return _adapter != null;
    }
    
    public boolean isEnabled() {
	return isAvailable() && _adapter.isEnabled();
    }
    
    public Set<BluetoothDevice> getBondedDevices() {
	return isAvailable() ? _adapter.getBondedDevices() : null;
    }
    
    public boolean isDiscovering() {
	return isAvailable() && _adapter.isDiscovering();
    }

    public synchronized boolean enable() {
	if(!isAvailable()) return false;
	if(_enables.contains(this)) return true;
	boolean ret = _adapter.enable();
	if(ret) _enables.add(this);
	return ret;
    }
    
    public synchronized boolean disable() {
	if(_enables.contains(this)) _enables.remove(this);
	Log.d("Bluetooth", String.format("%senabled initially, %d enables remain", _enabled ? "" : "not ", _enables.size()));
	return _enables.isEmpty() && !_enabled ? _adapter.disable() : false; 
    }
    
    public synchronized boolean startDiscovery() {
	if(_adapter == null) return false;
	if(_discoveries.contains(this)) return true;
	boolean ret = _adapter.startDiscovery();
	if(ret) {
	    Log.d("Bluetooth", "start discovery...");
	    _discoveries.add(this);
	    _context.registerReceiver(_receiver, _filter);
	}
	return ret;
    }
    
    public synchronized boolean cancelDiscovery() {
	if(_discoveries.contains(this)) {
	    _discoveries.remove(this);
	    _context.unregisterReceiver(_receiver);
	    Log.d("Bluetooth", String.format("%sdiscoverying initially, %d discoveries remain", _discoverying ? "" : "not ", _discoveries.size()));
	}
	
	if(_discoveries.isEmpty()) {
	    _adapter.cancelDiscovery();
	    Log.d("Bluetooth", "cancel discovery");
	    return true;
	} else
	    return false;
    }
    
    public BluetoothServerSocket listen(String name, UUID uuid) throws IOException {
	return !isAvailable() ? null : _adapter.listenUsingRfcommWithServiceRecord(name, uuid);
    }
}
