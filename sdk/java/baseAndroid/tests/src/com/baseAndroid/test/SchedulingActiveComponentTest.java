package com.baseAndroid.test;

import android.test.AndroidTestCase;
import api.CSense;
import api.CSenseException;
import api.IScheduler;
import messages.RawMessage;
import messages.TypeInfo;
import base.Log;
import components.basic.QueueComponent;
import components.basic.SynchronousQueueComponent;
import components.basic.TapComponent;
import components.test.ConsumerComponent;
import components.test.ActiveProducerComponent;

public class SchedulingActiveComponentTest extends AndroidTestCase {
	private final int CONSUMPTION = 10;
	private final String VERSION = "v2";
	// v0: passed
	// v1: passed without InterruptedException
	// v2: Synchronization error null; INIT_MSG_POOL_CAPACITY not applicable since the pool would not grow.
	private CSense _factory = new CSense(VERSION);
	private IScheduler _scheduler;

	public void setUp() throws Exception {
		_scheduler = _factory.newScheduler();
		Log.i("============================================ Test%s Begin ===========================================", VERSION);
	}

	public void tearDown() throws Exception {
		_scheduler = null;
		Log.i("============================================ Test%s End ===========================================", VERSION);
	}
	
	/**
	 * One self-threaded active producer pushes messages to a consumer through an intermediate queue.
	 * @throws CSenseException
	 */
	public void testActivePushProducersConsumer() throws CSenseException {
		ActiveProducerComponent<RawMessage> p = new ActiveProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false), 100);
		QueueComponent<RawMessage> q = new SynchronousQueueComponent<RawMessage>(15, 2, true);
		ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(1, true) {
			@Override
			protected boolean isTerminated() {
				return consumption() == CONSUMPTION;
			}
		};
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();		
		assertNotNull(c.getInputPort("in0"));
		
		try {
			c.getInputPort("in1");
		    fail( "CSenseException is not thrown expectedly" );
		} catch (CSenseException e) {
		}
		
		p.getOutputPort("out").link(q.getInputPort("in0"));
		q.getOutputPort("out").link(c.getInputPort("in0"));
		c.getOutputPort("out").link(tap.getInputPort("in"));
		
		_scheduler.addComponent(p);
		_scheduler.addComponent(q);
		_scheduler.addComponent(c);
		_scheduler.addComponent(tap);
		_scheduler.start();
		_scheduler.join();
		
		assertEquals(0, p.getMessageSkipCount());
		assertEquals(10, p.getMessagePushTimes());
		assertEquals(0, p.getMessageDropCount());
	}
	
	/**
	 * Two self-threaded active producers push messages to a common consumer through intermediate queues.
	 * @throws CSenseException
	 */
	public void testTwoActivePushProducersOneConsumer() throws CSenseException {
		ActiveProducerComponent<RawMessage> p1 = new ActiveProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false), 100);
		ActiveProducerComponent<RawMessage> p2 = new ActiveProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false), 250);
		QueueComponent<RawMessage> q1 = new SynchronousQueueComponent<RawMessage>(15, 2, true);
		QueueComponent<RawMessage> q2 = new SynchronousQueueComponent<RawMessage>(15, 2, true);
		ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(2, true) {
			@Override
			protected boolean isTerminated() {
				return consumption() == CONSUMPTION;
			}
		};
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();		
		assertNotNull(c.getInputPort("in0"));
		
		try {
			c.getInputPort("in2");
		    fail( "CSenseException is not thrown expectedly" );
		} catch (CSenseException e) {
		}
		
		p1.getOutputPort("out").link(q1.getInputPort("in0"));
		p2.getOutputPort("out").link(q2.getInputPort("in1"));
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
		
		assertEquals(23, p1.getMessagePushTimes());
		assertEquals(0, p1.getMessageSkipCount());
		assertEquals(0, p1.getMessageDropCount());
		
		assertEquals(10, p2.getMessagePushTimes());
		assertEquals(0, p2.getMessageSkipCount());
		assertEquals(0, p2.getMessageDropCount());
	}
}
