package messages.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class MessagePoolTest {
    CSense _csense = new CSense("v2");
    ArrayList<FramePool<? extends Frame>> _pools = new ArrayList<FramePool<? extends Frame>>(
	    3);

    @Before
    public void setUp() throws Exception {
	_pools.add(_csense.newFramePool(TypeInfo.newDoubleVector(8192), 0));
	_pools.add(_csense.newFramePool(TypeInfo.newByteVector(8192), 0));
	_pools.add(_csense.newFramePool(new TypeInfo<RawFrame>(
		RawFrame.class, 4, 32, 1, true, false), 0));
    }

    @After
    public void tearDown() throws Exception {
	_pools = null;
    }

    @Test
    public void testMessagePoolClassOfTIntBoolean() {
	assertEquals(3, _pools.size());
	for (FramePool<? extends Frame> pool : _pools)
	    assertNotNull(pool);
    }

    @Test
    public void testInitialSize() {
	// for(IMessagePool<? extends IMessage> pool: _pools)
	// assertEquals(0, pool.size());
    }

    @Test
    public void testInitialCapacity() {
	// for(IMessagePool<? extends IMessage> pool: _pools)
	// assertEquals(0, pool.capacity());
    }

    @Test
    public void testIsEmpty() {
	// for(MessagePool<? extends IMessage> pool: _pools)
	// assertTrue(pool.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTake() throws CSenseException {
	for (FramePool<? extends Frame> pool : _pools) {
	    Frame msg = ((FramePool<? extends Frame>) pool).get();
	    assertNotNull(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(0, pool.size());
	    // assertTrue(pool.isEmpty());
	}
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOffer() throws CSenseException {
	for (FramePool<? extends Frame> pool : _pools) {
	    Frame msg = ((FramePool<Frame>) pool).get();
	    assertNotNull(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(0, pool.size());
	    // assertTrue(pool.isEmpty());

	    ((FramePool<Frame>) pool).put(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(1, pool.size());
	    // assertFalse(pool.isEmpty());
	}
    }

}
