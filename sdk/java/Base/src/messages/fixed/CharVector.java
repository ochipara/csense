package messages.fixed;

import java.nio.CharBuffer;

import api.CSenseException;
import api.IMessagePool;

import messages.ReadOnlyMessageException;
import messages.TypeInfo;

public class CharVector extends Vector<Character> {
    /**
     * 
     */
    private CharBuffer _charBuffer;
    private TypeInfo<? extends CharVector> _type;

    public CharVector(IMessagePool pool, TypeInfo<? extends CharVector> type)
	    throws CSenseException {
	super(pool, type);
	_charBuffer = buffer().asCharBuffer();
	_type = type;
    }

    @Override
    public TypeInfo<? extends CharVector> getTypeInfo() {
	return _type;
    }

    @Override
    public void slice(int lower, int upper) {
	_charBuffer.position(lower);
	_charBuffer.limit(upper);
	_charBuffer = _charBuffer.slice();
    }

    @Override
    public Character get() {
	return _charBuffer.get();
    }

    @Override
    public void put(Character val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_charBuffer.put(val);
    }

    @Override
    public Character get(int index) {
	return _charBuffer.get(index);
    }

    public String getString() {
	StringBuffer sb = new StringBuffer();
	//	for (int i = 0; i < _charBuffer.limit(); i++) {
	//	    char ch = _charBuffer.get();
	//	    sb.append(ch);
	//	}

	while(_charBuffer.hasRemaining()) {
	    sb.append(_charBuffer.get());
	}
	return sb.toString();
    }

    public void putString(String str) {
	_charBuffer.put(str.toCharArray());	
    }

    @Override
    public void put(int index, Character val) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_charBuffer.put(index, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < _charBuffer.limit(); i++) {
	    char d = _charBuffer.get(i);
	    sb.append(d + " ");
	}

	return sb.toString();
    }

    @Override
    public String debugValues() {
	return displayValues();
    }

    public void put(char[] bytes) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_charBuffer.put(bytes);
    }

    public void put(String str) {
	if (isReadOnly())
	    throw new ReadOnlyMessageException();
	_charBuffer.put(str.toCharArray());
    }

    public void get(char[] payload) {
	_charBuffer.get(payload);
    }

    @Override
    public Vector<Character> flip() {
	_charBuffer.flip();
	return this;
    }

    @Override
    public Vector<Character> clear() {
	_charBuffer.clear();
	return this;
    }

    @Override
    public int capacity() {
	return _charBuffer.capacity();
    }

    @Override
    public int position() {
	return _charBuffer.position();
    }

    @Override
    public int limit() {
	return _charBuffer.limit();
    }

    @Override
    public Vector<Character> position(int position) {
	_charBuffer.position(position);
	return this;
    }

    @Override
    public int remaining() {
	return _charBuffer.remaining();
    }

    @Override
    public void initialize() {
	super.initialize();
	_charBuffer.clear();
    }
}
