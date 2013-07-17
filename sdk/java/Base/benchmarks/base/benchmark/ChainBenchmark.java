package base.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.BasicConfigurator;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import api.CSense;
import api.CSenseException;
import api.IComponent;
import api.IScheduler;
import base.Statistics;
import base.StatisticsLogger;
import components.basic.SyncQueue;
import components.basic.TapComponent;
import components.test.BenchmarkMessage;
import components.test.BenchmarkSource;
import components.test.BenchmarkStats;
import components.test.BenchmarkWorker;

public class ChainBenchmark extends SimpleBenchmark {
    private class TestResult {
	long _durationInNanoSecs;
	double _throughput;
	double _latency;

	private TestResult(long durationInNanoSecs, double throughput, double latency) {
	    _throughput = throughput;
	    _latency = latency;
	    _durationInNanoSecs = durationInNanoSecs;
	}

	long getDuration() {
	    return _durationInNanoSecs;
	}

	double getDurationInSecs() {
	    return _durationInNanoSecs / 1000000000.0;
	}

	double getMessageThroughput() {
	    return _throughput;
	}

	double getAverageMessageLatency() {
	    return _latency;
	}
    }

    private static final String STATS_CHAINBENCHMARK = "stats-ChainBenchmark.xml";
    private static final String STATS_CHAINBECHMARK_QUEUE = "stats-ChainBenchmark-queue.raw";
    private static final String VERSION = "v2";
    private static final CSense csense = new CSense(VERSION);
    private StatisticsLogger _logger;
    private TestResult _result;
    private String _scenario;
    private int _reps;

    private String getScenarioConfiguration() {
	return Timer + "-" + Workers + "-" + SourceBurst + "-" + QueueCapacity + "-" + SourceDelay + "-" + WorkerDelay;
    }

    private String getStatisticsPrefix(int invocation) {
	return getClass().getSimpleName() + "." + _scenario + "." + invocation + "." + getScenarioConfiguration();
    }

    @Override 
    protected void setUp() throws Exception {
	BasicConfigurator.configure();
	new File(STATS_CHAINBECHMARK_QUEUE).delete();
	_logger = new StatisticsLogger(STATS_CHAINBECHMARK_QUEUE);
    }

    @Override
    protected void tearDown() throws Exception {
	int invocation = 0;
	Statistics stats = new Statistics(getStatisticsPrefix(invocation));
	try {
	    stats.loadXML(STATS_CHAINBENCHMARK);
	    for(invocation = 0;; invocation++) {
		stats.setPrefix(getStatisticsPrefix(invocation));				
		if(stats.get("benchmark") == null) break;
	    }
	} catch(CSenseException e) {
	}

	stats.set("benchmark", getClass().getSimpleName());
	stats.set("scenario", _scenario);
	stats.set("reps", _reps);
	stats.set("messages", Timer);
	stats.set("workers", Workers);
	stats.set("source.burst", SourceBurst);
	stats.set("source.delay", SourceDelay);
	stats.set("worker.delay", WorkerDelay);
	stats.set("queue.capacity", QueueCapacity);		
	stats.set("duration", _result.getDuration());
	stats.set("throughput", _result.getMessageThroughput());
	stats.set("latency", _result.getAverageMessageLatency());

	BufferedReader reader = new BufferedReader(new FileReader("gc.log"));
	String line = null;
	int gc = 0;
	while((line = reader.readLine()) != null)
	    if(line.indexOf("GC") != -1) gc++;

	reader.close();
	stats.set("gc", gc);
	stats.saveXML(STATS_CHAINBENCHMARK);	
    }

    private void prepare(List<IScheduler> schedulers, BenchmarkSource source, SyncQueue<BenchmarkMessage> que, BenchmarkStats stats, final int reps) throws CSenseException {
	_reps = reps;
	schedulers.add(csense.newScheduler("domain0"));		
	IComponent prev = source;
	for (int i = 0; i < Workers; i++) {
	    SyncQueue<BenchmarkMessage> queue = Workers > 1 ? new SyncQueue<BenchmarkMessage>(QueueCapacity) : que;
	    BenchmarkWorker worker = new BenchmarkWorker(0);
	    queue.setName("queue" + i);
	    worker.setName("worker" + i);
	    prev.getOutputPort("out").link(queue.getInputPort("dataIn"));
	    queue.getOutputPort("dataOut").link(worker.getInputPort("in"));

	    IScheduler scheduler = csense.newScheduler("domain" + (i+1));
	    scheduler.addComponent(queue);
	    scheduler.addComponent(worker);
	    schedulers.add(scheduler);
	    prev = worker;
	}

	TapComponent<BenchmarkMessage> tap = new TapComponent<BenchmarkMessage>();
	stats.setName("stats");
	tap.setName("tap");

	prev.getOutputPort("out").link(stats.getInputPort("in"));		
	stats.getOutputPort("out").link(tap.getInputPort("in"));	

	schedulers.get(0).addComponent(source);
	IScheduler scheduler = schedulers.get(schedulers.size() - 1);
	scheduler.addComponent(stats);
	scheduler.addComponent(tap);
    }

    public void timeChainOfWorkers(final int reps) throws CSenseException, InterruptedException {
	_scenario = "timeChainOfWorkers";
	List<IScheduler> schedulers = new ArrayList<IScheduler>(1 + Workers);
	BenchmarkSource source = new BenchmarkSource(SourceDelay, SourceBurst);
	source.setName("source");
//	source.enableMessageLogging(5, 1250000, source.getName() + ".crt");		
	BenchmarkStats stats = new BenchmarkStats();
	SyncQueue<BenchmarkMessage> queue = Workers > 1 
		? new SyncQueue<BenchmarkMessage>(QueueCapacity)
		: new SyncQueue<BenchmarkMessage>(QueueCapacity) {
	    @Override
	    public void log() throws CSenseException {
		_logger.log((byte)_queue.size());
	    }
	};

	prepare(schedulers, source, queue, stats, reps);
	long nano = System.nanoTime();
	for(int m = 0; m < schedulers.size(); m++) schedulers.get(m).start();
	Thread.sleep(Timer * 1000);
	for(int m = schedulers.size() - 1; m >= 0; m--) schedulers.get(m).stop();
	long duration = System.nanoTime() - nano;
	source.debugMessageRoutes(duration);
	_result = new TestResult(duration, stats.getMessageThroughput(), stats.getAverageMessageLatency());
    }

    @Param({"5"}) private static int Timer;
    @Param({"1"}) private static int Workers;
    @Param({"1"}) private static int SourceBurst;
    @Param({"10"}) private static int QueueCapacity;
    @Param({"0"}) private static long SourceDelay;
    @Param({"0"}) private static long WorkerDelay;
    public static void main(String[] args) throws CSenseException {
	BasicConfigurator.configure();
	GraphGenerator generator = new GraphGenerator();;

	// long time variations of throughput and latency				
	BenchmarkConfiguration config1 = new BenchmarkConfiguration("Chain Benchmark", "ChainOfWorkers", 8, true, STATS_CHAINBENCHMARK);
	config1.defineTimers(4);

	// source delay
	BenchmarkConfiguration config2 = new BenchmarkConfiguration("Chain Benchmark", "ChainOfWorkers", 8, true, STATS_CHAINBENCHMARK);
	config2.defineTimers(4);
	config2.defineSourceDelay(10L,20L,30L,40L,50L);

	// queue capacity
	BenchmarkConfiguration config3 = new BenchmarkConfiguration("Chain Benchmark", "ChainOfWorkers", 8, true, STATS_CHAINBENCHMARK);
	config3.defineTimers(4);
	config3.defineQueueCapacity(4,8,12,24,32);

	// queue size and gc trace
	BenchmarkConfiguration config4 = new BenchmarkConfiguration("Chain Benchmark", "ChainOfWorkers", 1, true, STATS_CHAINBECHMARK_QUEUE);
	config4.defineTimers(8);

	List<BenchmarkConfiguration> configs = new ArrayList<BenchmarkConfiguration>();
//	configs.add(config1);
//	configs.add(config2);
//	configs.add(config3);
	configs.add(config4);		
	for(BenchmarkConfiguration config: configs) {
	    config.run(ChainBenchmark.class);
	    if(config == config1) generator.generateGraphSet1(config1);
	    else if(config == config2) generator.generateGraphSet2(config2);
	    else if(config == config3) generator.generateGraphSet3(config3);
	    else if(config == config4) {
		generator.generateQueueSizeTraceGraph(config4);
		generator.generateGCTraceGraph(config4);
	    }
	}
    }
}
