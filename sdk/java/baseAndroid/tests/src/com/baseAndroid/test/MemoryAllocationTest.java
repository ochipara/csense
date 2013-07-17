package com.baseAndroid.test;

import base.Log;
import messages.RawMessage;
import messages.TypeInfo;

import components.basic.TapComponent;
import components.test.ProducerComponent;

import android.test.AndroidTestCase;
import api.CSense;
import api.CSenseException;
import api.IScheduler;

public class MemoryAllocationTest extends AndroidTestCase {
//	private final int MSGS = 10000;
	private final String VERSION = "v0";
	// v0: passed with GC messages
	// v1: passed
	// v2: passed
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
	
	private void scheduleActiveProducer(final int maxPushTimes) throws CSenseException {
		ProducerComponent<RawMessage> p = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, false, true), 0) {
			@Override
			protected boolean isTerminated() {
				return getMessagePushTimes() == maxPushTimes;
			}
		};
		
		TapComponent<RawMessage> tap = new TapComponent<RawMessage>();
		p.getOutputPort("out").link(tap.getInputPort("in"));
		_scheduler.addComponent(p);
		_scheduler.addComponent(tap);
		_scheduler.start();
		_scheduler.join();		 
	}

	public void testActiveScheduledProducer() throws CSenseException {
		scheduleActiveProducer(100000);
	}
}
