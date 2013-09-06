package edu.uiowa.csense.runtime.types;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;

public class ByteVector extends Vector<Byte> {
    /**
     * 
     */ 
    private ByteBuffer thisBuffer;
    
    public ByteVector(FramePool pool, TypeInfo type) throws CSenseException {
  	super(pool, type);
  	thisBuffer = buffer;
      }
    
    public final byte[] array() {
	return thisBuffer.array();
    }

    public final int arrayOffset() {
	return thisBuffer.arrayOffset();
    }

    public CharBuffer asCharBuffer() {
	return thisBuffer.asCharBuffer();
    }

    public DoubleBuffer asDoubleBuffer() {
	return thisBuffer.asDoubleBuffer();
    }

    public FloatBuffer asFloatBuffer() {
	return thisBuffer.asFloatBuffer();
    }

    public IntBuffer asIntBuffer() {
	return thisBuffer.asIntBuffer();
    }

    public LongBuffer asLongBuffer() {
	return thisBuffer.asLongBuffer();
    }

    public ByteBuffer asReadOnlyBuffer() {
	return thisBuffer.asReadOnlyBuffer();
    }

    public ShortBuffer asShortBuffer() {
	return thisBuffer.asShortBuffer();
    }

    public final int capacity() {
	return thisBuffer.capacity();
    }

    public final Buffer clear() {
	return thisBuffer.clear();
    }

    public ByteBuffer compact() {
	return thisBuffer.compact();
    }

    public int compareTo(ByteBuffer that) {
	return thisBuffer.compareTo(that);
    }

    public ByteBuffer duplicate() {
	return thisBuffer.duplicate();
    }

    @Override
    public boolean equals(Object ob) {
	return thisBuffer.equals(ob);
    }

    public final Buffer flip() {
	return thisBuffer.flip();
    }

    public byte get() {
	return thisBuffer.get();
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
	return thisBuffer.get(dst, offset, length);
    }

    public ByteBuffer get(byte[] dst) {
	return thisBuffer.get(dst);
    }

    public byte get(int index) {
	return thisBuffer.get(index);
    }

    public char getChar() {
	return thisBuffer.getChar();
    }

    public char getChar(int index) {
	return thisBuffer.getChar(index);
    }

    public double getDouble() {
	return thisBuffer.getDouble();
    }

    public double getDouble(int index) {
	return thisBuffer.getDouble(index);
    }

    public float getFloat() {
	return thisBuffer.getFloat();
    }

    public float getFloat(int index) {
	return thisBuffer.getFloat(index);
    }

    public int getInt() {
	return thisBuffer.getInt();
    }

    public int getInt(int index) {
	return thisBuffer.getInt(index);
    }

    public long getLong() {
	return thisBuffer.getLong();
    }

    public long getLong(int index) {
	return thisBuffer.getLong(index);
    }

    public short getShort() {
	return thisBuffer.getShort();
    }

    public short getShort(int index) {
	return thisBuffer.getShort(index);
    }

    public final boolean hasArray() {
	return thisBuffer.hasArray();
    }

    public final boolean hasRemaining() {
	return thisBuffer.hasRemaining();
    }

    @Override
    public int hashCode() {
	return thisBuffer.hashCode();
    }

    public boolean isDirect() {
	return thisBuffer.isDirect();
    }

    public boolean isReadOnly() {
	return thisBuffer.isReadOnly();
    }

    public final int limit() {
	return thisBuffer.limit();
    }

    public final Buffer limit(int arg0) {
	return thisBuffer.limit(arg0);
    }

    public final Buffer mark() {
	return thisBuffer.mark();
    }

    public final ByteOrder order() {
	return thisBuffer.order();
    }

    public final ByteBuffer order(ByteOrder bo) {
	return thisBuffer.order(bo);
    }

    public final int position() {
	return thisBuffer.position();
    }

    public final Buffer position(int arg0) {
	return thisBuffer.position(arg0);
    }

    public ByteBuffer put(byte b) {
	return thisBuffer.put(b);
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
	return thisBuffer.put(src, offset, length);
    }

    public final ByteBuffer put(byte[] src) {
	return thisBuffer.put(src);
    }

    public ByteBuffer put(ByteBuffer src) {
	return thisBuffer.put(src);
    }

    public ByteBuffer put(int index, byte b) {
	return thisBuffer.put(index, b);
    }

    public ByteBuffer putChar(char value) {
	return thisBuffer.putChar(value);
    }

    public ByteBuffer putChar(int index, char value) {
	return thisBuffer.putChar(index, value);
    }

    public ByteBuffer putDouble(double value) {
	return thisBuffer.putDouble(value);
    }

    public ByteBuffer putDouble(int index, double value) {
	return thisBuffer.putDouble(index, value);
    }

    public ByteBuffer putFloat(float value) {
	return thisBuffer.putFloat(value);
    }

    public ByteBuffer putFloat(int index, float value) {
	return thisBuffer.putFloat(index, value);
    }

    public ByteBuffer putInt(int index, int value) {
	return thisBuffer.putInt(index, value);
    }

    public ByteBuffer putInt(int value) {
	return thisBuffer.putInt(value);
    }

    public ByteBuffer putLong(int index, long value) {
	return thisBuffer.putLong(index, value);
    }

    public ByteBuffer putLong(long value) {
	return thisBuffer.putLong(value);
    }

    public ByteBuffer putShort(int index, short value) {
	return thisBuffer.putShort(index, value);
    }

    public ByteBuffer putShort(short value) {
	return thisBuffer.putShort(value);
    }

    public final int remaining() {
	return thisBuffer.remaining();
    }

    public final Buffer reset() {
	return thisBuffer.reset();
    }

    public final Buffer rewind() {
	return thisBuffer.rewind();
    }

    public ByteBuffer slice() {
	return thisBuffer.slice();
    }

    @Override
    public String toString() {
	return thisBuffer.toString();
    }
}
