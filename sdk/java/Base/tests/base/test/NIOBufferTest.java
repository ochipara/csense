package base.test;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NIOBufferTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testFloatBuffer() {
	ByteBuffer bb = ByteBuffer.allocateDirect(16);
	FloatBuffer fb = bb.asFloatBuffer();
	assertEquals(16, bb.remaining());
	assertEquals( 4, fb.remaining());
	fb.put(1.5f);
	fb.put(1.5f);
	fb.put(1.5f);
	fb.put(1.5f);
	assertEquals(0, bb.position());
	assertEquals(4, fb.position());
	assertEquals(0, fb.remaining());
	
	fb.flip();
	assertEquals(0, fb.position());
	assertEquals(4, fb.limit());
	assertEquals(4, fb.remaining());

	assertEquals( 0, bb.position());
	assertEquals(16, bb.limit());
	assertEquals(16, bb.remaining());

    }
}
