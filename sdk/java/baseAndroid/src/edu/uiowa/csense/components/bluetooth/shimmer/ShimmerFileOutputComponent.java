package edu.uiowa.csense.components.bluetooth.shimmer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.uiowa.csense.components.storage.FileOutputComponent;
import edu.uiowa.csense.runtime.api.CSenseException;

public class ShimmerFileOutputComponent extends FileOutputComponent<ShimmerSensorData> {
    static private final int BYTES_PER_SAMPLE = 2 + 3 * 2;
    static private final double SAMPLING_RATE = 51.20;
    private ByteBuffer _buffer = ByteBuffer.allocate(getHeaderSize());

    public ShimmerFileOutputComponent(int spaceLimitInPercentage, 
	    int fileSizeLimitInSamples, String path, String extension) throws CSenseException {
	super(spaceLimitInPercentage, fileSizeLimitInSamples, path, extension);
	_buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public ShimmerFileOutputComponent(long spaceLimitInBytes,
	    int fileSizeLimitInSamples, String path, String extension) throws CSenseException {
	super(spaceLimitInBytes, fileSizeLimitInSamples, path, extension);
	_buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    protected String getPrefix(ShimmerSensorData msg) {
	return msg.getMoteName() + "-" + msg.getSensorName();
    }

    @Override
    protected int getHeaderSize() {
	return 8;
    }

    @Override
    protected long getFileSizeInBytes(int samples) {
	return samples < 0 ? 0 : getHeaderSize() + samples * BYTES_PER_SAMPLE;
    }

    @Override
    protected int writeHeader(ShimmerSensorData msg) throws IOException {
	_buffer.putLong(msg.getTimeStamp());
	_buffer.flip();
	int bytes = getOutputChannel().write(_buffer);
	_buffer.clear();
	return bytes;
    }

    @Override
    protected void advanceTimestamp(ShimmerSensorData msg, int bytes) {
	int samples = bytes / BYTES_PER_SAMPLE;
	long ms = Math.round(1000.0 / SAMPLING_RATE * samples);
	msg.setTimeStamp(msg.getTimeStamp() + ms);
    }
}
