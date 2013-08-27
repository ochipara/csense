package components.bluetooth.android;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public abstract class BluetoothServerComponent<T1 extends RawFrame, T2 extends RawFrame> extends CSenseSource<T2> implements BluetoothListener {     
    private BluetoothServer _server;
    private List<InputPort<T1>> _ins;
    private List<OutputPort<T2>> _outs;
    
    /**
     * Constructs a Bluetooth server component with necessary information that uses the default SPP Bluetooth communication
     * and accepts only only one client connection.
     * @param typeInfo the output message type
     * @param name the Bluetooth device name
     * @throws CSenseException if creating the output ports fails
     */
    public BluetoothServerComponent(TypeInfo<T2> type, String name) throws CSenseException {
	this(type, name, Bluetooth.UUID_SPP, 1);
    }
    
    /**
     * Constructs a Bluetooth server component with necessary information that uses the default SPP Bluetooth communication.
     * @param typeInfo the output message type
     * @param name the Bluetooth device name
     * @param channels the maximum number of client connections to accept
     * @throws CSenseException if creating the output ports fails
     */
    public BluetoothServerComponent(TypeInfo<T2> type, String name, int channels) throws CSenseException {
	this(type, name, Bluetooth.UUID_SPP, channels);
    }
    
    /**
     * Constructs a Bluetooth server component with necessary information.
     * @param typeInfo the output message type
     * @param name the Bluetooth device name
     * @param uuid the UUID of the Bluetooth communication
     * @param channels the maximum number of client connections to accept
     * @throws CSenseException if creating the output ports fails
     */
    public BluetoothServerComponent(TypeInfo<T2> type, String name, UUID uuid, int channels) throws CSenseException {
	super(type);
	_server = new BluetoothServer(name, uuid, this);
	_ins = new ArrayList<InputPort<T1>>(channels);
	_outs = new ArrayList<OutputPort<T2>>(channels);
	for(int i = 0; i < channels; i++) {
	    InputPort<T1> in = newInputPort(this, "in" + i);
	    OutputPort<T2> out = newOutputPort(this, "out" + i);
	    _ins.add(in);
	    _outs.add(out);
	}
	newOutputPort(this, "tap");
    }

    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();
    }

    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	_server.start();
    }

    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	_server.stop();
    }
    
    @Override
    public void onInput() throws CSenseException {
	for(int i = 0; i < _ins.size(); i++) {
	    InputPort<T1> p = _ins.get(i);
	    if(p.hasFrame()) {
		T1 msg = p.getFrame();
		_server.send(i, msg.getBuffer());
	    }
	}
    }
}
