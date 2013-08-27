package base.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uiowa.csense.components.basic.TapComponent;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class SchedulerTest {
    private final int CONSUMPTION = 10;
    private final String VERSION = "v2";
    // v0: passed
    // v1: passed
    // v2: failed with Synchronization error null when freeing a message on push
    // failure
    private CSense _csense = new CSense(VERSION);
    private IScheduler _scheduler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	_scheduler = _csense.newScheduler("SchedulerTest");
	Log.i(String.format("================================== Test%s Begin =================================", VERSION));
    }

    @After
    public void tearDown() throws Exception {
	_scheduler = null;
	Log.i(String.format("================================== Test%s End =================================", VERSION));
    }

    /**
     * Two active producers push messages at different rates while the consumer
     * never polls.
     * 
     * @throws CSenseException
     */
    @Test
    public void testPushProducerConsumer() throws CSenseException {
	ProducerComponent<RawFrame> p1 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true, true),
		100);
	ProducerComponent<RawFrame> p2 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true, true),
		250);
	QueueComponent<RawFrame> q1 = new QueueComponent<RawFrame>(10, 1);
	QueueComponent<RawFrame> q2 = new QueueComponent<RawFrame>(10, 1);
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(2,
		false) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == CONSUMPTION;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));
	assertNotNull(c.getInputPort("in1"));

	p1.getOutputPort("out").link(q1.getInputPort("in0"));
	p2.getOutputPort("out").link(q2.getInputPort("in0"));
	q1.getOutputPort("out").link(c.getInputPort("in0"));
	q2.getOutputPort("out").link(c.getInputPort("in1"));
	c.getOutputPort("out").link(tap.getInputPort("in"));

	_scheduler.addComponent(p1);
	_scheduler.addComponent(p2);
	_scheduler.addComponent(q1);
	_scheduler.addComponent(q2);
	_scheduler.addComponent(c);
	_scheduler.addComponent(tap);
	_scheduler.start();
	_scheduler.join();

	assertEquals(0, p1.getMessageSkipCount());
	assertEquals(24, p1.getMessagePushTimes());
	assertEquals(0, p1.getMessageDropCount());

	assertEquals(0, p2.getMessageSkipCount());
	assertEquals(10, p2.getMessagePushTimes());
	assertEquals(0, p2.getMessageDropCount());
    }

    /**
     * Two active producers push messages at different rates while the consumer
     * also polls.
     * 
     * @throws CSenseException
     */
    @Test
    public void testPushProducerPullConsumer() throws CSenseException {
	ProducerComponent<RawFrame> p1 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true, true),
		100);
	ProducerComponent<RawFrame> p2 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true, true),
		250);
	QueueComponent<RawFrame> q1 = new QueueComponent<RawFrame>(10, 1,
		true);
	QueueComponent<RawFrame> q2 = new QueueComponent<RawFrame>(10, 1,
		true);
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(2,
		true) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == CONSUMPTION;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));
	assertNotNull(c.getInputPort("in1"));

	p1.getOutputPort("out").link(q1.getInputPort("in0"));
	p2.getOutputPort("out").link(q2.getInputPort("in0"));
	q1.getOutputPort("out").link(c.getInputPort("in0"));
	q2.getOutputPort("out").link(c.getInputPort("in1"));
	c.getOutputPort("out").link(tap.getInputPort("in"));

	_scheduler.addComponent(p1);
	_scheduler.addComponent(p2);
	_scheduler.addComponent(q1);
	_scheduler.addComponent(q2);
	_scheduler.addComponent(c);
	_scheduler.addComponent(tap);
	_scheduler.start();
	_scheduler.join();

	assertEquals(0, p1.getMessageSkipCount());
	assertEquals(23, p1.getMessagePushTimes());
	assertEquals(1, p1.getMessageDropCount());

	assertEquals(0, p1.getMessageSkipCount());
	assertEquals(10, p2.getMessagePushTimes());
	assertEquals(0, p2.getMessageDropCount());
    }

    /**
     * Two passive producers only push messages when the consumer polls.
     * 
     * @throws CSenseException
     */
    @Test
    public void testPullProducerPullConsumer() throws CSenseException {
	ProducerComponent<RawFrame> p1 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false));
	ProducerComponent<RawFrame> p2 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false));
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(2,
		true) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == CONSUMPTION;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));
	assertNotNull(c.getInputPort("in1"));

	p1.getOutputPort("out").link(c.getInputPort("in0"));
	p2.getOutputPort("out").link(c.getInputPort("in1"));
	c.getOutputPort("out").link(tap.getInputPort("in"));

	_scheduler.addComponent(p1);
	_scheduler.addComponent(p2);
	_scheduler.addComponent(c);
	_scheduler.addComponent(tap);
	_scheduler.start();
	_scheduler.join();

	assertEquals(12, p1.getMessagePushTimes());
	assertEquals(3, p1.getMessageDropCount());
	assertEquals(13, p2.getMessagePushTimes());
	assertEquals(2, p2.getMessageDropCount());
    }

    /**
     * Two passive producers push messages to a common queue which allows the
     * consumer to poll.
     * 
     * @throws CSenseException
     */
    @Test
    public void testPushProducerPullConsumerWithMISOQueueInBetween()
	    throws CSenseException {
	ProducerComponent<RawFrame> p1 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false), 100);
	ProducerComponent<RawFrame> p2 = new ProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false), 250);
	QueueComponent<RawFrame> q = new QueueComponent<RawFrame>(15, 2,
		true);
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(1,
		true) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == CONSUMPTION;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));

	try {
	    c.getInputPort("in1");
	    fail("CSenseException is not thrown expectedly");
	} catch (CSenseException e) {
	}

	p1.getOutputPort("out").link(q.getInputPort("in0"));
	p2.getOutputPort("out").link(q.getInputPort("in1"));
	q.getOutputPort("out").link(c.getInputPort("in0"));
	c.getOutputPort("out").link(tap.getInputPort("in"));

	_scheduler.addComponent(p1);
	_scheduler.addComponent(p2);
	_scheduler.addComponent(q);
	_scheduler.addComponent(c);
	_scheduler.addComponent(tap);
	_scheduler.start();
	_scheduler.join();

	assertEquals(7, p1.getMessagePushTimes());
	assertEquals(0, p1.getMessageDropCount());
	assertEquals(3, p2.getMessagePushTimes());
	assertEquals(0, p2.getMessageDropCount());
    }
}
