package edu.uiowa.csense.profiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;

public class StatisticsLogger {
	private final String _filename;
	private FileChannel _channel;	
	private ByteBuffer _buf;
	
	public StatisticsLogger(String filename) {
		_filename = filename;
		try {
			_channel = new RandomAccessFile(_filename, "rw").getChannel();
		} catch (FileNotFoundException e) {
			throw new CSenseRuntimeException(_filename + "is not found.");
		}
		
		_buf = ByteBuffer.allocate(8192);
	}
	
	public long size() throws CSenseException {
		try {
			return _channel.size();
		} catch (IOException e) {
			throw new CSenseException("failed to get the file size", e);
		}
	}

	private void sync() throws CSenseException {
		_buf.flip();
		while(_buf.hasRemaining()) {
			try {
				_channel.write(_buf);
			} catch (IOException e) {
				throw new CSenseException("failed to write to " + _filename, e);
			}
		}
		_buf.clear();
	}
	
	public synchronized void seek(long pos) throws CSenseException {
		sync();
		try {
			_channel.position(pos);
		} catch (IOException e) {
			throw new CSenseException("failed to seek " + _filename + " at position " + pos, e);
		}
	}
	
	private double[] downsample(double[] points, int size) {
		int samples = points.length / size;
		if(points.length <= samples) return points;	
		double[] data = new double[points.length / samples];
		for(int i = 0; i < data.length; i++) { 
			if(i == 0) data[0] = points[0];
			else {				
				double sum = 0;
				int base = (i-1) * samples + 1;
				for(int j = 0; j < samples; j++) sum += points[base + j];
				data[i] = Math.round(sum / samples);
			}
		}
		return data;
	}
	
	public synchronized int read(ByteBuffer buf) throws CSenseException {
		sync();
		try {
			return _channel.read(buf);
		} catch (IOException e) {
			throw new CSenseException("failed to read bytes from " + _filename, e);
		}
	}
	
	public synchronized void log(byte b) throws CSenseException {
		if(!_buf.hasRemaining()) sync();
		try {
			_buf.put(b);
		} catch(IllegalArgumentException e) {
			System.out.printf("buffer pos: %d, limit: %d\n", _buf.position(), _buf.limit());
			System.exit(-1);
		}
	}
	
	public synchronized void log(int i) throws CSenseException {
		if(!_buf.hasRemaining()) sync();
		_buf.putInt(i);
	}
	
	public synchronized void log(long l) throws CSenseException {
		if(!_buf.hasRemaining()) sync();
		_buf.putLong(l);
	}
	
	public synchronized void log(double d) throws CSenseException {
		if(!_buf.hasRemaining()) sync();
		_buf.putDouble(d);
	}
}
