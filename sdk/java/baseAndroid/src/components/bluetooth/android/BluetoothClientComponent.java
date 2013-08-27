package components.bluetooth.android;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Task;
import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import android.content.Context;

public abstract class BluetoothClientComponent<T1 extends RawFrame, T2 extends RawFrame> extends CSenseSource<T2> implements BluetoothListener {
    private abstract class RunnableTimerEvent extends TimerEvent implements Runnable {}
    
    protected List<InputPort<T1>> _ins;
    protected List<OutputPort<T2>> _outs;
    private List<BluetoothClientService> _channels;
    private BluetoothClient _client;
    private int _max;
    private int _devices;
    private boolean _fatal;

    /**
     * A timer task that checks at most 3 times if sufficient Bluetooth devices are discovered.
     */
    private RunnableTimerEvent _discoveryTimerTask = new RunnableTimerEvent() {
	private int _retry;
	
	@Override
	public void run() {
	    _devices = _client.discover();
	    if(_devices < _max) {
		if(_retry < 3) {
		    info(_devices, "motes discovered, wait for the remaining", _max - _devices, "devices");
		    getOwner().getScheduler().schedule(getOwner(), this, 10, TimeUnit.SECONDS);
		    _retry++;
		    return;
		} else
		    info("only", _devices, "/", _max, "motes are discovered, start to connect");		
	    }

	    _client.start();
	    getOwner().getScheduler().schedule(getOwner(), _connectionTimerTask, 5, TimeUnit.SECONDS);
	}
    };

    /**
     * A timer task that checks all the client connections are determined.
     */
    private RunnableTimerEvent _connectionTimerTask = new RunnableTimerEvent() {
	boolean _ready;	
	@Override
	public void run() {
	    if(!_ready) {
		info(_client.getConnections(), "/", _devices, "Bluetooth devices are connected");
		int inactive = 0;
		for(int i = 0; i < _devices; i++)
		    if(!_client.isActive(i)) inactive++;
		
		if(_client.getConnections() + inactive == _devices) {
		    if(_client.getConnections() > 0) {
			_ready = true;
			getOwner().getScheduler().schedule(getOwner(), _aliveTimerTask, 5, TimeUnit.SECONDS);
		    } else
			info("None of the Bluetooth devices are available.");
		} else {
		    info("Bluetooth devices are not ready yet, wait for 5 more secs");
		    getOwner().getScheduler().schedule(getOwner(), this, 5, TimeUnit.SECONDS);
		}
	    }			
	}
    };
    
    /**
     * A timer task that checks if there's something wrong with those Bluetooth device communications.
     */
    private RunnableTimerEvent _aliveTimerTask = new RunnableTimerEvent() {
	@Override
	public void run() {
	    if(fatal()) {
		try {
//		    restart();
		    onStop();
		    onStart();
		} catch (CSenseException e) {
		    throw new CSenseRuntimeException("failed to restart", e);
		}
	    } else
		getOwner().getScheduler().schedule(getOwner(), this, 5, TimeUnit.SECONDS);			
	}
    };
        
    /**
     * Constructs a Bluetooth client component with necessary information.
     * @param typeInfo the output message type
     * @param context the application context to access Android Bluetooth radio
     * @param prefix the prefix of remote Bluetooth devices
     * @param max the maximum number of Bluetooth devices to connect
     * @param bufferSize the size of the response buffer in bytes
     * @throws CSenseException if IOutPort.push() failes
     */
    public BluetoothClientComponent(TypeInfo<T2> typeInfo, Context context, String prefix, int max, int bufferSize) throws CSenseException {
	super(typeInfo);
	_max = max;
	_client = new BluetoothClient(context, prefix, max, bufferSize, this);	
	_ins = new ArrayList<InputPort<T1>>(max);
	_outs = new ArrayList<OutputPort<T2>>(max);
	_channels = new ArrayList<BluetoothClientService>(max);
	for(int i = 0; i < max; i++) {
	    InputPort<T1> in = newInputPort(this, "in" + i);
	    OutputPort<T2> out = newOutputPort(this, "out" + i);
	    _ins.add(in);
	    _outs.add(out);
	}
	newOutputPort(this, "tap");
    }

    /**
     * Register a client connection service as a channel.
     * @return the channel number
     */
    public int registerChannel(BluetoothClientService service) {
	if(_channels.add(service)) return _channels.indexOf(service);
	else throw new CSenseRuntimeException("failed to register a new channel");
    }
    
    /**
     * Returns the channel number of the client connection service.
     * @return the channel number or -1 if the client connection service is not registered
     */
    public int getChannel(BluetoothClientService service) {
	return _channels.indexOf(service);
    }
    
    /**
     * Returns the number of connected Bluetooth devices.
     * @return the number of connected Bluetooth devices
     */
    public int getConnections() {
	return _client.getConnections();
    }
    
    /**
     * Returns the maximum number of Bluetooth devices to connect.
     * @return the maximum number of Bluetooth devices to connect
     */
    public int getMaxConnections() {
	return _max;
    }
    
    /**
     * Returns true if there's something wrong with the communication that cannot be recovered.
     * @return if there's something wrong with the communication that cannot be recovered
     */
    protected boolean fatal() {
	return _fatal;
    }

    /**
     * Set if there's something wrong with the communication that cannot be recovered
     * @param fatal true if there's something wrong with the communication that cannot be recovered and false otherwise
     */
    protected void fatal(boolean fatal) {
	_fatal = fatal;
    }

//    FIXME Component restart seems not supported yet.
//    void restart() throws CSenseException {
//	onStop();
//	onStart();
//    }

    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();
    }
    
    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	_fatal = false;
	getScheduler().schedule(this, _discoveryTimerTask, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStop() throws CSenseException {
	getScheduler().cancel(_discoveryTimerTask);
	getScheduler().cancel(_connectionTimerTask);
	getScheduler().cancel(_aliveTimerTask);
	_client.stop();
	super.onStop();
    }

    /**
     * Tells the client to send the data in the input message to remote Bluetooth devices of their particular channels.
     */
    @Override
    public void onInput() throws CSenseException {
	for(int i = 0; i < _ins.size(); i++) {
	    InputPort<T1> p = _ins.get(i);
	    if(p.hasFrame()) {
		T1 msg = p.getFrame();
		_client.send(i, msg.getBuffer());
	    }
	}
    }
    
    @Override
    public void doEvent(Task t) throws CSenseException {
	if(t == _aliveTimerTask) _aliveTimerTask.run();
	if(t == _connectionTimerTask) _connectionTimerTask.run();
	if(t == _discoveryTimerTask) _discoveryTimerTask.run();
    }
}
