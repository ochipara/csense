package com.baseAndroid.test;

import android.test.AndroidTestCase;
import api.CSense;
import api.CSenseException;
import api.IScheduler;
import base.Log;
import messages.RawMessage;
import messages.TypeInfo;
import components.basic.QueueComponent;
import components.basic.TapComponent;
import components.test.ConsumerComponent;
import components.test.ProducerComponent;

public class SchedulerTest extends AndroidTestCase {
	private final int CONSUMPTION = 10;
	private final String VERSION = "v2";
	// v0: passed 
	// v1: passed
	// v2: failed with synchronization error null when freeing a message on push failure
	private CSense _csense = new CSense(VERSION);
	private IScheduler _scheduler;
	
	public void setUp() throws Exception {
		_scheduler = _csense.newScheduler();
		Log.i("============================================ Test%s Begin ===========================================", VERSION);
	}

	public void tearDown() throws Exception {
		_scheduler = null;
		Log.i("============================================ Test%s End ===========================================", VERSION);
	}
	
	/**
	 * Two active producers push messages at different rates while the consumer never polls.
	 * @throws CSenseException
	 */
	public void testPushProducerConsumer() throws CSenseException {
		ProducerComponent<RawMessage> p1 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, true), 100);
		ProducerComponent<RawMessage> p2 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, true), 250);
		QueueComponent<RawMessage> q1 = new QueueComponent<RawMessage>(10, 1);
		QueueComponent<RawMessage> q2 = new QueueComponent<RawMessage>(10, 1);
		ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(2, false) {
			@Override
			protected boolean isTerminated() {
				return consumption() == CONSUMPTION;
			}
		};
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();		
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
		
		assertEquals( 0, p1.getMessageSkipCount());
		assertEquals(24, p1.getMessagePushTimes());
		assertEquals( 0, p1.getMessageDropCount());
		
		assertEquals( 0, p2.getMessageSkipCount());
		assertEquals(10, p2.getMessagePushTimes());
		assertEquals( 0, p2.getMessageDropCount());
	}
	
	/**
	 * Two active producers push messages at different rates while the consumer also polls.
	 * @throws CSenseException
	 */
	public void testPushProducerPullConsumer() throws CSenseException {
		ProducerComponent<RawMessage> p1 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, true), 100);
		ProducerComponent<RawMessage> p2 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, true), 250);
		QueueComponent<RawMessage> q1 = new QueueComponent<RawMessage>(10, 1, true);
		QueueComponent<RawMessage> q2 = new QueueComponent<RawMessage>(10, 1, true);
		ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(2, true) {
			@Override
			protected boolean isTerminated() {
				return consumption() == CONSUMPTION;
			}
		};
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();		
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
		
		assertEquals( 0, p1.getMessageSkipCount());
		assertEquals(23, p1.getMessagePushTimes());
		assertEquals( 1, p1.getMessageDropCount());
		
		assertEquals( 0, p1.getMessageSkipCount());
		assertEquals(10, p2.getMessagePushTimes());
		assertEquals( 0, p2.getMessageDropCount());
	}
		
	/**
	 * Two passive producers only push messages when the consumer polls.
	 * @throws CSenseException
	 */
	public void testPullProducerPullConsumer() throws CSenseException {
		ProducerComponent<RawMessage> p1 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false));
		ProducerComponent<RawMessage> p2 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false));
		ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(2, true) {
			@Override
			protected boolean isTerminated() {
				return consumption() == CONSUMPTION;
			}
		};
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();		
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
		assertEquals( 3, p1.getMessageDropCount());
		assertEquals(13, p2.getMessagePushTimes());
		assertEquals( 2, p2.getMessageDropCount());
	}
	
	/**
	 * Two passive producers push messages to a common queue which allows the consumer to poll.
	 * @throws CSenseException
	 */
	public void testPushProducerPullConsumerWithMISOQueueInBetween() throws CSenseException {
		ProducerComponent<RawMessage> p1 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false), 100);
		ProducerComponent<RawMessage> p2 = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, true, false), 250);
		QueueComponent<RawMessage> q = new QueueComponent<RawMessage>(15, 2, true);
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