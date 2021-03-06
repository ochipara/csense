package base.benchmark;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uiowa.csense.components.basic.CopyRefComponent;
import edu.uiowa.csense.components.basic.TapComponent;
import edu.uiowa.csense.profiler.Utility;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.CSenseRuntimeException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class MicroBenchmark {
	private final int MSGS = 10000;
	private final String VERSION = "v2";
	// v0: passed
	// v1: passed
	// v2: suffers from Synchronization Error when a message gets freed  on push failure.
	private CSense _csense = new CSense("v2");
	private IScheduler _scheduler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_scheduler = _csense.newScheduler("MicrobenchmarkTest");
		Log.i("test", "============================================ Test Begin ===========================================", VERSION);
	}

	@After
	public void tearDown() throws Exception {
		_scheduler = null;
		System.gc();
		Log.i("test", "============================================ Test End ===========================================", VERSION);
	}

	/**
	 * Components linked in one or more chains to pass IMessages are measured in terms of processing delay, 
	 * memory efficiency and energy consumption.
	 * @throws CSenseException
	 */
	public void chainsOfComponents(int branches, int length, final int maxPushTimes) throws CSenseException {
		if(length < 4) throw new CSenseRuntimeException("length has to be at least 4");
		ProducerComponent<RawFrame> p = new ProducerComponent<RawFrame>(new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true, true), 0) {
			@Override
			protected boolean isTerminated() {
				//if(getIMessagePushTimes() % 1000 == 0) Log.i("pushed 1000 times");
				return getMessagePushTimes() == maxPushTimes;
			}
		};
		CopyRefComponent<RawFrame> pivot = new CopyRefComponent<RawFrame>(branches);
		p.getOutputPort("out").link(pivot.getInputPort("in"));
		for(int branch = 0; branch < branches; branch++) {
			CopyRefComponent<RawFrame> prev = pivot;
			CopyRefComponent<RawFrame> ref = prev;
			for(int i = 0; i < length  - 4; i++) {
				ref = new CopyRefComponent<RawFrame>(1);
				prev.getOutputPort(Utility.toString("out", i == 0 ? branch : 0)).link(ref.getInputPort("in"));
				prev = ref;
				 _scheduler.addComponent(ref);
			}
			
			ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(1, false);
			TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
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
		
	private void onPush(Frame m) throws CSenseException {
		InputPort<Frame> input = null;
		processInput(input, m);
	}
	
	private <T extends Frame> void processInput(InputPort<Frame> input, Frame m) throws CSenseException {
		internalProcessInput(input, m);
	}
	
	private <T extends Frame> void internalProcessInput(InputPort<Frame> input, Frame m) throws CSenseException {
		doInput(m);
	}
	
	private void doInput(Frame m) throws CSenseException {
		push(m);
	}
	
	private void push(Frame m) throws CSenseException {
		m.free();
	}
	
	private void baseline(int branches, int length, int msgs) throws CSenseException {
		final int count = msgs * branches * length;
		TypeInfo<RawFrame> type = new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, false, true);
		FramePool<RawFrame> pool = _csense.<RawFrame>newFramePool(type, 32);
		for(int i = 0; i < count; i++) {
			RawFrame m = pool.get();
			assertNotNull(m);
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
	
	@Test
	public void testRecursiveMethodInvocations10() {
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 10 * 4);
	}
	
	@Test
	public void testRecursiveMethodInvocations100() {
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 100 * 4);
	}
	
	@Test
	public void testRecursiveMethodInvocations1000() {
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 1000 * 4);
	}
	
	@Test
	public void testRecursiveMethodInvocations10000() {
		for(int i = 0; i < MSGS; i++)
			baseline(1 * 10000 * 4);
	}
	
	@Test
	public void testAPIInvocations10() throws CSenseException {
		baseline(1, 10, MSGS);
	}
	
	@Test
	public void testAPIInvocations100() throws CSenseException {
		baseline(1, 100, MSGS);
	}
	
	@Test
	public void testAPIInvocations1000() throws CSenseException {
		baseline(1, 1000, MSGS);
	}

	// IInPort.onPush()
	// CSenseComponent.processInput()
	// CSenseComponentInternal.processInput()
	// CSenseComponent.doInput()
	// IOutPort.push();	
	@Test
	public void test1ChainsOf10Components() throws CSenseException {
		chainsOfComponents(1, 10, MSGS);
	}
	
	@Test
	public void test1ChainsOf100Components() throws CSenseException {
		chainsOfComponents(1, 100, MSGS);
	}
	
	
	@Test
	public void test1ChainsOf1000Components() throws CSenseException {
		chainsOfComponents(1, 1000, MSGS);
	}
}
