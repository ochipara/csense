package messages.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import messages.RawMessage;
import messages.TypeInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import api.CSense;
import api.CSenseException;
import api.IMessage;
import api.IMessagePool;
import api.Message;

public class MessagePoolTest {
    CSense _csense = new CSense("v2");
    ArrayList<IMessagePool<? extends Message>> _pools = new ArrayList<IMessagePool<? extends Message>>(
	    3);

    @Before
    public void setUp() throws Exception {
	_pools.add(_csense.newMessagePool(TypeInfo.newDoubleVector(8192), 0));
	_pools.add(_csense.newMessagePool(TypeInfo.newByteVector(8192), 0));
	_pools.add(_csense.newMessagePool(new TypeInfo<RawMessage>(
		RawMessage.class, 4, 32, 1, true, false), 0));
    }

    @After
    public void tearDown() throws Exception {
	_pools = null;
    }

    @Test
    public void testMessagePoolClassOfTIntBoolean() {
	assertEquals(3, _pools.size());
	for (IMessagePool<? extends IMessage> pool : _pools)
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
	for (IMessagePool<? extends IMessage> pool : _pools) {
	    Message msg = ((IMessagePool<? extends Message>) pool).get();
	    assertNotNull(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(0, pool.size());
	    // assertTrue(pool.isEmpty());
	}
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOffer() throws CSenseException {
	for (IMessagePool<? extends IMessage> pool : _pools) {
	    Message msg = ((IMessagePool<Message>) pool).get();
	    assertNotNull(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(0, pool.size());
	    // assertTrue(pool.isEmpty());

	    ((IMessagePool<Message>) pool).put(msg);
	    // assertEquals(1, pool.capacity());
	    // assertEquals(1, pool.size());
	    // assertFalse(pool.isEmpty());
	}
    }

}
