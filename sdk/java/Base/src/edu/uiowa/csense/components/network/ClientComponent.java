package components.network;

import base.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * Sends messages over a network via TCP. It drops messages when a successful
 * connection with a receiving (network) component doesn't exist. When the TCP
 * connection is severed, this component tries to re-establish a successful
 * connection.
 */
public class ClientComponent<T extends Message> extends CSenseComponent {
    public final Map<String, InPort<T>> PORTS_IN = this
	    .<T> setupInputPorts("in");
    public final Map<String, OutPort<T>> PORTS_OUT = this
	    .<T> setupOutputPorts("out");

    // Channel used to talk to server.
    SocketChannel _server;
    java.net.InetSocketAddress _address;

    // Array responsible for pointing to the two ByteBuffers that need to be
    // sent in a row
    // the first ByteBuffer contains the (int) number of bytes the second
    // ByteBuffer occupies
    // the second ByteBuffer can be though of as the payload (data we care
    // about).
    ByteBuffer[] _buffersToSend = new ByteBuffer[2];

    /**
     * Constructor helper function (remove redundancy for overloaded
     * constructors).
     * 
     * @param serverIPAddress
     * @param outGoingConnections
     */
    private void ctorSetup(String serverIPAddress, int port) {
	_buffersToSend[0] = ByteBuffer.allocate(4);
	_address = new InetSocketAddress(serverIPAddress, port);
    }

    public ClientComponent() {
	// the first ByteBuffer contains data for an Integer that is always 4
	// bytes long.
	ctorSetup(CSenseOptions.SERVER_IP, CSenseOptions.SERVER_PORT);
    }

    public ClientComponent(String serverIPAddress, int port) {
	ctorSetup(serverIPAddress, port);
    }

    /**
     * Attempt to establish a connection with a server component that exists at
     * the specified IP address. TODO - this will likely have to change so we
     * scan the network for an available IP
     */
    private void createSocket() {
	try {
	    Log.d("Connecting...");
	    if (null == _server) {
		// Create client SocketChannel
		_server = SocketChannel.open();
	    }

	    // Forge connection to host
	    _server.connect(_address);
	    Log.d("attempt connection");
	} catch (IOException e) {
	    Log.d("Error creating socket. " + e.toString());
	    // e.printStackTrace();
	}
    }

    /**
     * Send the NIO buffer in msg over the network via TCP. First it sends the
     * size (number of bytes in the buffer) and then it sends the actually sends
     * the buffer.
     * 
     * @param msg
     *            TODO
     */
    public void sendMessageOverNetwork(Message msg) {
	try {
	    Log.d("try push...");
	    if (true == _server.isConnectionPending()) {
		if (true == _server.finishConnect()) {
		    Log.d("finished connection");
		} else {
		    Log.d("Could NOT finish connecting");
		    Log.d("", 2, 4);
		    Log.d(this, "", 3);
		    restart();
		    return;
		}
	    }

	    // get the ByteBuffer we're about to send over the network
	    _buffersToSend[1] = msg.buffer();
	    _buffersToSend[1].position(0);

	    // optimization - this ensures we only send 'interesting' bytes
	    // (bytes with values) in the ByteBuffer
	    int length = msg.buffer().limit();
	    _buffersToSend[0].clear();
	    _buffersToSend[0].putInt(length);
	    _buffersToSend[0].position(0);

	    // this while guarantees that the full message is sent before
	    // proceeding
	    int count = 0;
	    int bytesWritten = 0;
	    do {
		try {
		    Log.d("length: " + length, 4);

		    bytesWritten += _server.write(_buffersToSend);
		} catch (java.nio.channels.ClosedChannelException e) {
		    Log.e("ClosedChannelException");
		    restart();
		    return;
		} catch (IOException e) {
		    Log.e("Failed to write to server. Try to reconnect.");
		    // e.printStackTrace();
		    restart();
		    return;
		}

		if (count > 1) {
		    Log.i("not sent in 1 shot. Written: " + bytesWritten
			    + ", out of: " + length + ", count: " + count);
		}
		count++;
	    } while (_buffersToSend[0].hasRemaining() || bytesWritten < length); // part
										 // of
										 // the
										 // optimization
										 // above
										 // the
										 // do-while

	} catch (IOException e) {
	    Log.e("Error pushing message. " + e.toString());
	    // e.printStackTrace();
	} catch (Exception e) {
	    Log.e("Error pushing message. " + e.toString());
	    // e.printStackTrace();
	}
	Log.d("done push", 3);
    }

    @Override
    public void initialize() throws CSenseException {
	createSocket();
    }

    @Override
    public void cleanup() {
	try {
	    _server.close();
	    _server = null;
	} catch (IOException e) {
	    Log.e("Error closing socket. " + e.toString());
	}
    }

    @Override
    public void doInput(InPort<? extends Message> port) throws CSenseException {
	Message msg = port.poll();
	sendMessageOverNetwork(msg);
	getOutputPort("out").push(msg);
    }

    @Override
    protected boolean mayPull(OutPort<? extends Message> port) {
	return getInputPort().pull();
    }
}
