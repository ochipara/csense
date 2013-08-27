package edu.uiowa.csense.components.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * This is the UDPLinkComponent messenger. It is responsible for sending
 * messages to a receiving LinkComponent.
 * 
 * @author Farley Lai
 */
public class UDPSenderComponent<T extends Frame> extends CSenseComponent {
    public InputPort<T> in = new InPort<T>(this, "in", 100);
    public OutputPort<T> out = new OutPort<T>(this, "out", 200);

    private InetSocketAddress _address;
    private DatagramChannel _channel;

    public UDPSenderComponent(String hostname, int port) {
	addPort(in);
	addPort(out);
	_address = new InetSocketAddress(hostname, port);
    }

    @Override
    protected void doInput(InPort<? extends Frame> port) {
	T msg = in.poll();
	try {
	    Log.d("sending UDP message of size " + msg.remaining());
	    _channel.send(msg.getBuffer(), _address);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	out.push(msg);
    }

    @Override
    public void initialize() throws CSenseException {
	try {
	    _channel = DatagramChannel.open();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void cleanup() {
	try {
	    _channel.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected boolean mayPull(OutPort<? extends Frame> port) {
	return getInputPort().pull();
    }
}
