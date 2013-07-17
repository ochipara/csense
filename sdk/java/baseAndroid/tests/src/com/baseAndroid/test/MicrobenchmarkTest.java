package com.baseAndroid.test;

import android.os.Debug;
import android.test.AndroidTestCase;

import api.CSense;
import api.CSenseException;
import api.CSenseRuntimeException;
import api.IInPort;
import api.IMessage;
import api.IMessagePool;
import api.IScheduler;
import base.Log;
import base.Utility;
import messages.RawMessage;
import messages.TypeInfo;
import components.basic.CopyRefComponent;
import components.basic.TapComponent;
import components.test.ConsumerComponent;
import components.test.ProducerComponent;

public class MicrobenchmarkTest extends AndroidTestCase {
	private final int MSGS = 1000;
	private final String VERSION = "v2";
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
		Log.i("============================================ Test%s End ===========================================", VERSION);
		_scheduler = null;
	}

	/**
	 * Components linked in a chain to pass messages are measured in terms of processing delay, 
	 * memory efficiency and energy consumption.
	 * @throws CSenseException
	 */
	public void chainsOfComponents(int branches, int length, final int maxPushTimes) throws CSenseException {
		if(length < 4) throw new CSenseRuntimeException("length has to be at least 4");
		ProducerComponent<RawMessage> p = new ProducerComponent<RawMessage>(new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, false, true), 0) {
			@Override
			protected boolean isTerminated() {
				//if(getMessagePushTimes() % 1000 == 0) Log.i("pushed 1000 times");
				return getMessagePushTimes() == maxPushTimes;
			}
		};
		CopyRefComponent<RawMessage> pivot = new CopyRefComponent<RawMessage>(branches);
		p.getOutputPort("out").link(pivot.getInputPort("in"));
		for(int branch = 0; branch < branches; branch++) {
			CopyRefComponent<RawMessage> prev = pivot;
			CopyRefComponent<RawMessage> ref = prev;
			for(int i = 0; i < length  - 4; i++) {
				ref = new CopyRefComponent<RawMessage>(1);
				prev.getOutputPort(Utility.toString("out", i == 0 ? branch : 0)).link(ref.getInputPort("in"));
				prev = ref;
				 _scheduler.addComponent(ref);
			}
			
			ConsumerComponent<RawMessage> c = new ConsumerComponent<RawMessage>(1, false);
			TapComponent<RawMessage> tap = new TapComponent<RawMessage>();
			ref.getOutputPort(Utility.toString("out", ref == pivot ? branch : 0)).link(c.getInputPort("in0"));
			c.getOutputPort("out").link(tap.getInputPort("in"));
			 _scheduler.addComponent(c);
			 _scheduler.addComponent(tap);
		}
		
		_scheduler.addComponent(p);
		_scheduler.addComponent(pivot);
		//long time = System.currentTimeMillis();
		_scheduler.start();
		_scheduler.join();
		//time = System.currentTimeMillis() - time;
		//Log.d("Scheduler Running Time: %dms", time);
		
		assertEquals(maxPushTimes, p.getMessagePushTimes());
		assertEquals( 0, p.getMessageDropCount());
	}
	
	private void onPush(IMessage m) throws CSenseException {
		IInPort<IMessage> input = null;
		processInput(input, m);
	}
	
	private <T extends IMessage> void processInput(IInPort<IMessage> input, IMessage m) throws CSenseException {
		internalProcessInput(input, m);
	}
	
	private <T extends IMessage> void internalProcessInput(IInPort<IMessage> input, IMessage m) throws CSenseException {
		doInput(m);
	}
	
	private void doInput(IMessage m) throws CSenseException {
		push(m);
	}
	
	private void push(IMessage m) throws CSenseException {
		m.free();
	}
	
	private void baseline(int branches, int length, int msgs) throws CSenseException {
		final int count = msgs * branches * length;
		TypeInfo<RawMessage> type = new TypeInfo<RawMessage>(RawMessage.class, 4, 32, 1, false, true);
		IMessagePool<RawMessage> pool = _csense.<RawMessage>newMessagePool(type, 32);
		for(int i = 0; i < count; i++) {
			IMessage m = pool.get();
			onPush(m);
		}
//		assertEquals(1, pool.capacity());
//		assertEquals(1, pool.size());
//		Log.i("pool capacity %d, size %d", pool.capacity(), pool.size());
	}
	
	private void baseline(int count) {
		if(count-- > 0)
			baseline(count);
	}
	
	public void testRecursiveMethodInvocations10() {
		//Debug.startMethodTracing("testRecursiveMethodInvocations10", 64 * 1024 * 1024);
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 10 * 4);
		//Debug.stopMethodTracing();
	}
	
	public void testRecursiveMethodInvocations30() {
		//Debug.startMethodTracing("testRecursiveMethodInvocations30", 64 * 1024 * 1024);
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 30 * 4);
		//Debug.stopMethodTracing();
	}
	
	public void testRecursiveMethodInvocations50() {
		//Debug.startMethodTracing("testRecursiveMethodInvocations50", 64 * 1024 * 1024);
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 50 * 4);
		//Debug.stopMethodTracing();
	}
	
	public void testRecursiveMethodInvocations75() {
		//Debug.startMethodTracing("testRecursiveMethodInvocations75", 64 * 1024 * 1024);
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 75 * 4);
		//Debug.stopMethodTracing();
	}
	
	public void testAPIInvocations10() throws CSenseException {
		//Debug.startMethodTracing("testAPIInvocations10", 64 * 1024 * 1024);
		baseline(1, 10, MSGS);
		//Debug.stopMethodTracing();
	}
	
	public void testAPIInvocations30() throws CSenseException {
		//Debug.startMethodTracing("testAPIInvocations30", 64 * 1024 * 1024);
		baseline(1, 30, MSGS);
		//Debug.stopMethodTracing();
	}
	
	public void testAPIInvocations50() throws CSenseException {
		//Debug.startMethodTracing("testAPIInvocations50", 64 * 1024 * 1024);
		baseline(1, 50, MSGS);
		//Debug.stopMethodTracing();
	}
	
	public void testAPIInvocations75() throws CSenseException {
		//Debug.startMethodTracing("testAPIInvocations75", 64 * 1024 * 1024);
		baseline(1, 75, MSGS);
		//Debug.stopMethodTracing();
	}

	// IInPort.onPush()
	// CSenseComponent.processInput()
	// CSenseComponentInternal.processInput()
	// CSenseComponent.doInput()
	// IOutPort.push();	

	public void test1ChainsOf70Components() throws CSenseException {
//		Debug.startMethodTracing("test1ChainsOf75Components", 64 * 1024 * 1024);
		chainsOfComponents(1, 70, MSGS);
//		Debug.stopMethodTracing();
	}
	public void test1ChainsOf50Components() throws CSenseException {
//		Debug.startMethodTracing("test1ChainsOf50Components", 64 * 1024 * 1024);
		chainsOfComponents(1, 50, MSGS);
//		Debug.stopMethodTracing();
	}
	
	public void test1ChainsOf30Components() throws CSenseException {
//		Debug.startMethodTracing("test1ChainsOf30Components", 64 * 1024 * 1024);
		chainsOfComponents(1, 30, MSGS);
//		Debug.stopMethodTracing();
	}
	
	
	public void test1ChainsOf10Components() throws CSenseException {
//		Debug.startMethodTracing("test1ChainsOf10Components", 64 * 1024 * 1024);
		chainsOfComponents(1, 10, MSGS);
//		Debug.stopMethodTracing();
	}
		
}
