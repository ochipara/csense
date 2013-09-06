package edu.uiowa.csense.components.bluetooth;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

/*
 * This message is used command Bluetooth client services internally within Bluetooth components, not intended to pass through CSense components.
 */
public class BluetoothCommand extends RawFrame {
    public enum Type {
	CONNECT,
	DISCONNECT,
	READ,
	RAW,
	STOP,
    };
    
    private Type _type;
    private int _bytesToRead;
    
    public BluetoothCommand(FramePool<? extends RawFrame> pool, TypeInfo<? extends RawFrame> type) throws CSenseException {
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
