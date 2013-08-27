package base.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uiowa.csense.components.basic.TapComponent;
import edu.uiowa.csense.profiler.Utility;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class SchedulingActiveComponentTest {
    private class TestResult {
	TestResult(long durationInNanoSecs, int queuedMessageCount,
		int messagePushTimes, int messageDropCount,
		int messageSkipCount, int messageConsumptions) {
	    _durationInNanoSecs = durationInNanoSecs;
	    _queuedMessageCount = queuedMessageCount;
	    _messagePushTimes = messagePushTimes;
	    _messageDropCount = messageDropCount;
	    _messageSkipCount = messageSkipCount;
	    _messageConsumptions = messageConsumptions;
	}

	long _durationInNanoSecs;
	int _queuedMessageCount;
	int _messagePushTimes;
	int _messageDropCount;
	int _messageSkipCount;
	int _messageConsumptions;

	double getDurationInSecs() {
	    return _durationInNanoSecs / 1000000000.0;
	}

	int getTotalProducerActionCount() {
	    return _messagePushTimes + _messageDropCount + _messageSkipCount;
	}

	double getObservedMessagePushRate() {
	    return getTotalProducerActionCount() / getDurationInSecs();
	}

	double getMessagePushRatio() {
	    return 1.0 * _messagePushTimes / getTotalProducerActionCount();
	}

	double getMessageSkipRatio() {
	    return 1.0 * _messageSkipCount / getTotalProducerActionCount();
	}

	double getMessageDropRatio() {
	    return 1.0 * _messageDropCount / getTotalProducerActionCount();
	}

	double getMessageConsumptionRate() {
	    return _messageConsumptions / getDurationInSecs();
	}

	// void report() {
	// Log.i("execution duration: %.2fs", getDurationInSecs());
	// Log.i("============================ Queue ===========================");
	// Log.i("queued messages: %d", _queuedMessageCount);
	// Log.i("============================ Producer ===========================");
	// Log.i("message skip count: %d, push times: %d, drop count: %d",
	// _messageSkipCount, _messagePushTimes, _messageDropCount);
	// Log.i("observed message push rate: %.2f msgs/s",
	// getObservedMessagePushRate());
	// Log.i("message push rate: %.2f msgs/s", _messagePushTimes /
	// getDurationInSecs());
	// Log.i("message drop rate: %.2f msgs/s", _messageDropCount /
	// getDurationInSecs());
	// Log.i("message skip rate: %.2f msgs/s", _messageSkipCount /
	// getDurationInSecs());
	// Log.i("push ratio: %.2f%%", getMessagePushRatio());
	// Log.i("skip ratio: %.2f%%", getMessageSkipRatio());
	// Log.i("drop ratio: %.2f%%", getMessageDropRatio());
	// Log.i("============================ Consumer ===========================");
	// Log.i("message consumptions: %d msgs", _messageConsumptions);
	// Log.i("message consumption rate: %.2f msgs/s",
	// getMessageConsumptionRate());
	// Log.i("============================ Difference ===========================");
	// Log.i("rate difference: %.2f msgs/s", getObservedMessagePushRate() -
	// getMessageConsumptionRate());
	// }
    }

    private final int PERIOD = 0;
    private final int CONSUMPTION = 1000000;
    private final String VERSION = "v0";
    // v0: passed
    // v1: passed with InterruptedException
    // v2: Synchronization error null; INIT_MSG_POOL_CAPACITY not applicable
    // since the pool would not grow.
    private CSense _factory = new CSense(VERSION);
    private IScheduler _scheduler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	Log.i(String.format("================================== Test%s Begin =================================", VERSION));
    }

    @After
    public void tearDown() throws Exception {
	Log.i(String.format("================================== Test%s End =================================", VERSION));
    }

    /**
     * One self-threaded active producer pushes messages to a consumer through
     * an intermediate queue.
     * 
     * @throws CSenseException
     */
    public TestResult runTestActivePushProducersConsumer(
	    final int terminatedMessageConsumptions) throws CSenseException {
	_scheduler = _factory
		.newScheduler("runTestActivePushProducersConsumer");
	System.gc();

	ActiveProducerComponent<RawFrame> p = new ActiveProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false), PERIOD);
	QueueComponent<RawFrame> q = new SynchronousQueueComponent<RawFrame>(
		32, 2, true);
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(1,
		true) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == terminatedMessageConsumptions;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));

	try {
	    c.getInputPort("in1");
	    fail("CSenseException is not thrown expectedly");
	} catch (CSenseException e) {
	}

	p.getOutputPort("out").link(q.getInputPort("in0"));
	q.getOutputPort("out").link(c.getInputPort("in0"));
	c.getOutputPort("out").link(tap.getInputPort("in"));

	_scheduler.addComponent(p);
	_scheduler.addComponent(q);
	_scheduler.addComponent(c);
	_scheduler.addComponent(tap);

	long duration = System.nanoTime();
	_scheduler.start();
	_scheduler.join();
	duration = System.nanoTime() - duration;

	return new TestResult(duration, q.size(), p.getMessagePushTimes(),
		p.getMessageDropCount(), p.getMessageSkipCount(),
		c.consumption());
    }

    @Test
    public void testActivePushProducersConsumer() throws CSenseException,
    IOException {
	final int iterations = 10;
	int[] configurations = new int[] { 1000, 5000, 10000, 50000, 100000,
		500000, 1000000 };
	List<List<TestResult>> resultsList = new ArrayList<List<TestResult>>(
		configurations.length);
	double[] observedMessagePushRates = new double[configurations.length];
	double[] messageConsumptionRates = new double[configurations.length];
	double[] messageRateDifferences = new double[configurations.length];
	for (int i = 0; i < configurations.length; i++) {
	    List<TestResult> results = new ArrayList<TestResult>(iterations);
	    resultsList.add(results);
	    int config = configurations[i];
	    double observedMessagePushRate = 0;
	    double messageConsumptionRate = 0;
	    for (int j = 0; j < iterations; j++) {
		TestResult result = runTestActivePushProducersConsumer(config);
		// result.report();
		observedMessagePushRate += result.getObservedMessagePushRate();
		messageConsumptionRate += result.getMessageConsumptionRate();
		results.add(result);
	    }
	    observedMessagePushRate /= iterations;
	    observedMessagePushRates[i] = observedMessagePushRate;
	    messageConsumptionRate /= iterations;
	    messageConsumptionRates[i] = messageConsumptionRate;
	    messageRateDifferences[i] = observedMessagePushRate
		    - messageConsumptionRate;

	    Log.i("=================== Test Configuration: %d messages ===================",
		    config);
	    Log.i("average observed message push rate: %.2f msgs/s",
		    observedMessagePushRates[i]);
	    Log.i("average consumption rate: %.2f msgs/s",
		    messageConsumptionRates[i]);
	    Log.i("average producer consumer rate difference: %.2f msgs/s",
		    observedMessagePushRates[i] - messageConsumptionRates[i]);
	}

	Plotter plotter = new Plotter("Producer Consumer Message Throughput");
	String[] xLabels = new String[configurations.length];
	for (int i = 0; i < xLabels.length; i++)
	    xLabels[i] = String.valueOf(configurations[i]);
	String[] yLabels = new String[] { "", "50000", "100000", "150000",
		"200000", "250000", "300000" };
//	String url = plotter.drawLineChart("Producer",
//		observedMessagePushRates, "Consumer", messageConsumptionRates,
//		"Number of Messages", xLabels, "Messages/s", yLabels);
//	Utility.browse(url);

	// assertEquals(0, p.getMessageSkipCount());
	// assertEquals(CONSUMPTION, c.consumption());
	// assertEquals(CONSUMPTION, p.getMessagePushTimes());
	// assertEquals(0, p.getMessageDropCount());
    }

    /**
     * Two self-threaded active producers push messages to a common consumer
     * through intermediate queues.
     * 
     * @throws CSenseException
     */
    // @Test
    public void testTwoActivePushProducersOneConsumer() throws CSenseException {
	ActiveProducerComponent<RawFrame> p1 = new ActiveProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false), 100);
	ActiveProducerComponent<RawFrame> p2 = new ActiveProducerComponent<RawFrame>(
		new TypeInfo<RawFrame>(RawFrame.class, 4, 32, 1, true,
			false), 250);
	QueueComponent<RawFrame> q1 = new SynchronousQueueComponent<RawFrame>(
		15, 2, true);
	QueueComponent<RawFrame> q2 = new SynchronousQueueComponent<RawFrame>(
		15, 2, true);
	ConsumerComponent<RawFrame> c = new ConsumerComponent<RawFrame>(2,
		true) {
	    @Override
	    protected boolean isTerminated() {
		return consumption() == CONSUMPTION;
	    }
	};
	TapComponent<RawFrame> tap = new TapComponent<RawFrame>();
	assertNotNull(c.getInputPort("in0"));

	try {
	    c.getInputPort("in2");
	    fail("CSenseException is not thrown expectedly");
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
