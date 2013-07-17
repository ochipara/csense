package components.basic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

public class MemoryInitialize {
    public static DoubleBuffer zeros(int r, int c) {
	ByteBuffer bytes = ByteBuffer.allocateDirect(r * c * 8);
	bytes.order(ByteOrder.LITTLE_ENDIAN);
	DoubleBuffer samples = bytes.asDoubleBuffer();
	for (int i = 0; i < r * c; i++)
	    samples.put(i, 0.0);

	return samples;
    }

    public static DoubleBuffer ones(int r, int c) {
	ByteBuffer bytes = ByteBuffer.allocateDirect(r * c * 8);
	bytes.order(ByteOrder.LITTLE_ENDIAN);
	DoubleBuffer samples = bytes.asDoubleBuffer();
	for (int i = 0; i < r * c; i++)
	    samples.put(i, 1.0);

	return samples;
    }
}
