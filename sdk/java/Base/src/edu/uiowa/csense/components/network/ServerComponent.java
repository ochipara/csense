package components.network;

import base.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * This is a non-blocking server that attempts to read two streams of data for
 * every successful connection with a client. The first stream contains an
 * integer (4 bytes) that specifies how many bytes are coming in the second
 * stream. The second stream contains the data that needs to go into the NIO
 * buffer backing a Message object.
 * 
 * @author Austin
 * 
 * @param <T>
 *            - Type of messages this component will create
 */
public class ServerComponent<T extends Message> extends SourceComponent<T> {
    public final Map<String, OutPort<T>> PORTS_OUT = this
	    .<T> setupOutputPorts("out");
    private ServerSocketChannel _channel;
    private boolean _receivedFirstMessage = false;
    private final int emptyReadsBeforeDrop = 50;
    private int numSameReads = 0;
    private int prevReadValue = 0;
    private int _port = 0;

    // the first 4 bytes of every message are placed in this buffer to give us
    // an idea of how big the incoming message is
    ByteBuffer _incomingLengthInBytes;
    int numReadFromChannel = 0;

    private void constructorHelper(int port) {
	_port = port;
	_incomingLengthInBytes = ByteBuffer.allocate(4); // size of an 'int'
	_incomingLengthInBytes.putInt(0);
    }

    /**
     * First Constructor - assumes default port that is set in
     * base.Constants.java
     * 
     * @param outGoingConnections
     */
    public ServerComponent(Class<T> clazz) {
	super(clazz);
	constructorHelper(CSenseOptions.SERVER_PORT);
    }

    /**
     * Second Constructor - the user must provide the port this server will
     * listen to.
     * 
     * @param port
     * @param outGoingConnections
     */
    public ServerComponent(int port) {
	constructorHelper(port);
    }

    @Override
    public void doInput(SelectionKey key) throws IOException, CSenseException {
	if (key.isAcceptable()) {
	    Log.e("accepts a client connection");
	    Socket client = ((ServerSocketChannel) key.channel()).socket()
		    .accept();
	    client.getChannel().configureBlocking(false);
	    getScheduler().registerChannel(client.getChannel(),
		    SelectionKey.OP_READ, this);
	} else if (key.isReadable())
	    getInfoFromSocket(key);
    }

    /**
     * Reads two ByteBuffers from a socket in a row. The first ByteBuffer is 4
     * bytes long and makes up an integer that states how long the second
     * ByteBuffer is. The second ByteBuffer is the byte array that needs is
     * associated with a message. A ByteBuffer with a length greater than zero
     * (0) is placed in a 'NORMAL' message, otherwise it is part of a 'CLOSE'
     * message.
     * 
     * @param key
     */
    private void getInfoFromSocket(SelectionKey key) {
	try {
	    SocketChannel client = (SocketChannel) key.channel();
	    _incomingLengthInBytes.clear();

	    // Get the length of the incoming message
	    numReadFromChannel = 0;
	    numSameReads = 0;
	    prevReadValue = 0;
	    do {
		try {
		    numReadFromChannel += client.read(_incomingLengthInBytes);
		    if (0 > numReadFromChannel) {
			if (emptyReadsBeforeDrop < ++numSameReads) {
			    return;
			}
		    }
		} catch (IOException e) {
		    Log.e("IOException - It's likely that the client closed a connection early, while reading message length.");
		    restart();
		    return;
		}
	    } while (numReadFromChannel < _incomingLengthInBytes.capacity());

	    // Get the size of the bytebuffer we're about to read in
	    _incomingLengthInBytes.flip();
	    int size = _incomingLengthInBytes.getInt();
	    Log.d("incoming message size: %d", size);
	    if (0 < size) {
		if (!_receivedFirstMessage) {
		    setupMessagePool(size);
		    _receivedFirstMessage = true;
		}

		// create a byte buffer that can hold the message we're about to
		// read from the socket
		Message tmpMessage = getNextMessageToWriteInto();
		ByteBuffer buffToWriteInto = tmpMessage.buffer();
		do {
		    try {
			// Utility.errorStatement(ServerComponent.class,
			// "do while 2.");
			numReadFromChannel += client.read(buffToWriteInto);
			if (prevReadValue == numReadFromChannel)
			    numSameReads++;
			if (0 > numReadFromChannel) {
			    if (emptyReadsBeforeDrop < ++numSameReads) {
				drop(tmpMessage);
				return;
			    }
			}

			prevReadValue = numReadFromChannel;
		    } catch (IOException e) {
			Log.d(e.toString());
			// e.printStackTrace();
			client.close();
		    }
		} while (buffToWriteInto.position() < size);

		buffToWriteInto.flip();
		Log.d("received message!");
		getOutputPort("out").push(tmpMessage);
	    }
	} catch (Exception e) {
	    Log.d(e.toString());
	    // e.printStackTrace();
	}
    }

    @Override
    /**
     * Open the socket channel.
     */
    public void initialize() throws CSenseException {
	Log.d("creatING a server socket!");
	try {
	    // Create a non-blocking server socket channel on port SERVER_PORT
	    _channel = ServerSocketChannel.open();
	    _channel.configureBlocking(false);
	    _channel.socket().bind(new InetSocketAddress(_port));
	    Log.d("creatED a server socket!");
	} catch (IOException e) {
	    Log.e(e.toString());
	    // e.printStackTrace();
	}
    }

    @Override
    public void activate() throws CSenseException {
	getScheduler().registerChannel(_channel, SelectionKey.OP_ACCEPT, this);
    }

    @Override
    /**
     * Make sure the listening socket is closed.
     */
    public void cleanup() {
	if (_channel.isOpen()) {
	    try {
		_channel.close();
	    } catch (IOException e) {
		Log.w("failed closing socket... ");
		// e.printStackTrace();
	    }
	}
    }

    @Override
    protected boolean mayPull(OutPort<? extends Message> port) {
	return _channel != null;
    }
}
