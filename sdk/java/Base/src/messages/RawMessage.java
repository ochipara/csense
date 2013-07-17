package messages;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IMessage;
import api.IMessagePool;
import api.Message;

public class RawMessage extends Message {
    /**
     * 
     */
    protected final ByteBuffer _buffer;
    protected final Map<Integer, List<Message>> viewMap = new HashMap<Integer, List<Message>>(); 
    protected boolean _readOnly;
    protected long _timestamp;

    // multiple threads may be accessing this, so there is actually a bug
    private final TypeInfo<? extends RawMessage> _type;

    @Override
    public void initialize() {
	_buffer.clear();
	super.initialize();
    }

    /**
     * TODO: check if direct types are a problem on Sony Xperia
     * 
     * @param pool
     * @param type
     * @throws CSenseException
     */
    public RawMessage(IMessagePool<? extends RawMessage> pool, TypeInfo<? extends RawMessage> type) throws CSenseException {
	super(pool, type);
	_timestamp = System.currentTimeMillis();
	_type = type;

	_buffer = _type.isDirect() ? ByteBuffer.allocateDirect(_type.getNumBytes()) : ByteBuffer.allocate(_type.getNumBytes());
	_buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public RawMessage(IMessagePool<? extends Message> pool, TypeInfo<? extends RawMessage> type, IMessage parent, ByteBuffer buffer) throws CSenseException {	
	super(pool, type, parent);
	
	if (buffer == null) throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR);
	
	_buffer = buffer;
	_type = type;
    }

    /**
     * Returns the underlying ByteBuffer instance.
     * 
     * @return byte array of information passed between components.
     */
    public ByteBuffer buffer() {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	return _buffer;
    }

    public void resetTimestamp() {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_timestamp = System.currentTimeMillis();
    }

    public void setTimeStamp(long timestamp) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_timestamp = timestamp;
    }

    public long getTimeStamp() {
	return _timestamp;
    }

    public boolean isReadOnly() {
	return _readOnly;
    }

    public Message setReadOnly(boolean readOnly) {
	_readOnly = readOnly;
	return this;
    }

    // the interfaces from the nioBuffer
    public int position() {
	return _buffer.position();
    }

    public int limit() {
	return _buffer.limit();
    }

    public RawMessage limit(int limit) {
	_buffer.limit(limit);
	return this;
    }

    public int capacity() {
	return _buffer.capacity();
    }

    public int remaining() {
	return _buffer.remaining();
    }

    public boolean hasRemaining() {
	return _buffer.hasRemaining();
    }

    public Message flip() {
	_buffer.flip();
	return this;
    }

    public Message rewind() {
	_buffer.rewind();
	return this;
    }

    public Message clear() {
	_buffer.clear();
	return this;
    }

    public Message position(int pos) {
	_buffer.position(pos);
	return this;
    }

    final public boolean isDirect() {
	return _buffer.isDirect();
    }

    public byte[] bytes() {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	return _buffer.array();
    }

    @Override
    public String toString() {
	return "C: " + capacity() + " P:" + position() + " L:" + limit();
    }

    public void put(ByteBuffer bytes) {
	_buffer.put(bytes);

    }
    
    public void put(byte[] bytes) {
	_buffer.put(bytes);

    }

    public int getInt() {
    	return _buffer.getInt();
    }
    
    public int getInt(int index) {
	return _buffer.getInt(index);
    }
    
    public void putInt(int value) {
	_buffer.putInt(value);
    }
    
    public void putInt(int idx, int value) {
	_buffer.putInt(idx, value);
    }
    
    public float getFloat() {
    	return _buffer.getFloat();
    }
    
    public float getFloat(int idx) {
    	return _buffer.getFloat(idx);
    }
    
    public void putFloat(float value) {
	_buffer.putFloat(value);
    }
    
    public void putFloat(int idx, float value) {
	_buffer.putFloat(idx, value);
    }

    public short getShort() {
	return _buffer.getShort();
    }
    
    public short getShort(int index) {
	return _buffer.getShort(index);
    }
    
    public void putShort(int index, short value) {
	_buffer.putShort(index, value);
    }
    
    public double getDouble() {
    	return _buffer.getDouble();
    }

    public void putDouble(double value) {
	_buffer.putDouble(value);
    }

    public void putDouble(int index, double value) {
	_buffer.putDouble(index, value);
    }
    
    public void putByte(byte b) {
	_buffer.put(b);
    }

    public byte getByte() {
	return _buffer.get();
    }

    public void putShort(short s) {
	_buffer.putShort(s);
    }

    @Override
    public List<Message> split(CSenseComponent component, int numFrames) throws CSenseException {
	if (_parent != null) {
	    return _parent.split(component, numFrames);
	}
	
	List<Message> views = null; 
	if (viewMap.containsKey(numFrames) == false) {
	    try {
		views = new ArrayList<Message>(numFrames);

		int start = 0;
		int step = _buffer.capacity() / numFrames;
		for (int i = 0; i < numFrames; i++) {
		    _buffer.position(start);
		    _buffer.limit(start + step);		
		    ByteBuffer bb = _buffer.slice();
		    //Message msg = (Message) CSense.getImplementation().newMessage(_pool, _type);


		    Constructor c = _type.getJavaType().getDeclaredConstructor(IMessagePool.class, TypeInfo.class, IMessage.class, ByteBuffer.class);
		    Message msg = (Message) c.newInstance(_pool, _type, this, bb);
		    //msg.setParent(this); 
		    //RawMessage raw = new RawMessage(_pool, _type, bb);
		    views.add(msg);
		    start = start + step;
		}
		viewMap.put(numFrames, views);
	    } catch (NoSuchMethodException e) {		 
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (InstantiationException e) {
		e.printStackTrace();
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
	    } catch (InvocationTargetException e) {	
		e.printStackTrace();
	    }
	} else {
	    views = viewMap.get(numFrames);
	}
	
	
//	incrementReference(numFrames);	
	return views;
    }
}
