package components.bluetooth;

import messages.RawMessage;
import messages.TypeInfo;
import api.CSenseException;
import api.IMessagePool;

/*
 * This message is used command Bluetooth client services internally within Bluetooth components, not intended to pass through CSense components.
 */
public class BluetoothCommand extends RawMessage {
    public enum Type {
	CONNECT,
	DISCONNECT,
	READ,
	RAW,
	STOP,
    };
    
    private Type _type;
    private int _bytesToRead;
    
    public BluetoothCommand(IMessagePool<? extends RawMessage> pool, TypeInfo<? extends RawMessage> type) throws CSenseException {
	super(pool, type);
    }
    
    public void setType(Type type) {
	_type = type;
    }
    
    public Type getType() {
	return _type;
    }
    
    /**
     * Set the size of the response to read.
     * @param bytes The size of the response to read in bytes.
     */
    public void bytesToRead(int bytes) {
	_bytesToRead = bytes;
    }
    
    /**
     * Get the expected size in bytes of the response to read.
     * @return The size of the response to read in bytes.
     */
    public int bytesToRead() {
	return _bytesToRead;
    }
    
    /**
     * Clear the message buffer as well as the bytes to read field.
     */
    @Override
    public BluetoothCommand clear() {
	super.clear();
	bytesToRead(0);
	return this;
    }
}
