package base.benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.Statistics;
import base.StatisticsLogger;
import base.Utility;
import base.benchmark.ChainBenchmark;

import api.CSenseException;

public class GraphGenerator {
	private static final int IDX_MIN_BOX = 0;
	private static final int IDX_25Q_BOX = 1;
	private static final int IDX_75Q_BOX = 2;
	private static final int IDX_MAX_BOX = 3;
	private static final int IDX_MEDIAN_BOX = 4;
	private static final int IDX_MEAN_BOX = 5;
	private static final int IDX_CI_BOX = 6;
	private static final int SIZE_DATA_BOX = 7;
	
	private boolean _android;
	private String _platform;
	
	public GraphGenerator() {
		try {
			Class.forName("android.util.Log");
			_android = true;
		} catch(ClassNotFoundException e) {
		}
	    _platform = _android ? "Android" : "Desktop";
	}

	private static String getScenarioConfiguration(int timer, int workers, int sourceBurst, int queueCapacity, long sourceDelay, long workerDelay) {
		return timer + "-" + workers + "-" + sourceBurst + "-" + queueCapacity + "-" + sourceDelay + "-" + workerDelay;
	}
	
	private static String getStatisticsPrefix(String _scenario, int invocation, int timer, int workers, int sourceBurst, int queueCapacity, long sourceDelay, long workerDelay) {
		return ChainBenchmark.class.getSimpleName() + "." + _scenario + "." + invocation + "." + getScenarioConfiguration(timer, workers, sourceBurst, queueCapacity, sourceDelay, workerDelay);
	}

	private double computeCI(int invocation, double mean, List<Double> data) {
		final double[] T_TABLE = new double[] {
				Double.NaN, 12.706, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306, 2.262, 
				2.228, 2.201, 2.179, 2.160, 2.145, 2.131, 2.120, 2.110, 2.101, 2.093, 
				2.086, 2.080, 2.074, 2.069, 2.064, 2.060, 2.056, 2.052, 2.048, 2.045,
				2.042,			
		};
		int dof = invocation - 1;
		double sigma = 0;
		for(Double d: data) sigma += Math.pow(d - mean, 2);
		sigma = Math.sqrt(sigma / data.size());
//		System.out.printf("invocation: %d, sigma: %.3f, mean: %.3f\n", invocation, sigma, mean);
		return sigma / Math.sqrt(invocation) * T_TABLE[dof];
	}
	
	private void computeStatistics(int idx, int invocation, int totalReps, 
			double throughput, List<Double> throughputData, double[][] throughputs, 
			double latency, List<Double> latencyData, double[][] latencies, 
			long duration, List<Double> durationData, double[][] durations,
			int gc, List<Double> gcData, double[][] gcs) {	
		final int size = throughputData.size();
		int idx25Q = (int)Math.round(25.0 / 100 * size) - 1;
		int idx75Q = (int)Math.round(75.0 / 100 * size) - 1;
		idx25Q = (idx25Q == -1 && size == 1) ? size - 1 : idx25Q;
		idx75Q = idx75Q == size ? size - 1 : idx75Q;
    	Collections.sort(throughputData);
    	throughputs[IDX_MIN_BOX][idx] = throughputData.get(0);
    	throughputs[IDX_25Q_BOX][idx] = throughputData.get(idx25Q);
    	throughputs[IDX_75Q_BOX][idx] = throughputData.get(idx75Q);
    	throughputs[IDX_MAX_BOX][idx] = throughputData.get(throughputData.size()-1);
    	if(size % 2 == 0) {
    		int left = size / 2 - 1;
    		int right = size / 2;	    		
    		throughputs[IDX_MEDIAN_BOX][idx] = (throughputData.get(left) + throughputData.get(right)) / 2;
    	} else
    		throughputs[IDX_MEDIAN_BOX][idx] = throughputData.get(size / 2);	    	
    	throughputs[IDX_MEAN_BOX][idx] = throughput / totalReps;
    	throughputs[IDX_CI_BOX][idx] = computeCI(invocation, throughputs[IDX_MEAN_BOX][idx], throughputData);
//    	System.out.printf("throughput mean: %.3f, ci: %.3f\n", throughputs[IDX_MEAN_BOX][idx], throughputs[IDX_CI_BOX][idx]);
    	
    	Collections.sort(latencyData);
    	latencies[IDX_MIN_BOX][idx] = latencyData.get(0) / 1000;
    	latencies[IDX_25Q_BOX][idx] = latencyData.get(idx25Q) / 1000;
    	latencies[IDX_75Q_BOX][idx] = latencyData.get(idx75Q) / 1000;
    	latencies[IDX_MAX_BOX][idx] = latencyData.get(latencyData.size()-1) / 1000;
    	if(size % 2 == 0) {
    		int left = size / 2 - 1;
    		int right = size / 2;	    		
    		latencies[IDX_MEDIAN_BOX][idx] = (latencyData.get(left) + latencyData.get(right)) / 2 / 1000;
    	} else
    		latencies[IDX_MEDIAN_BOX][idx] = latencyData.get(size / 2) / 1000;	    	
    	latencies[IDX_MEAN_BOX][idx] = latency / totalReps / 1000;
    	latencies[IDX_CI_BOX][idx] = computeCI(invocation, latencies[IDX_MEAN_BOX][idx] * 1000, latencyData) / 1000;
//    	System.out.printf("[%d messages] latency min: %.3f, 25Q: %.3f, 75Q: %.3f, max: %.3f, meadian: %.3f, mean: %.3f\n", messages[idx], latencies[IDX_MIN_BOX][idx], latencies[IDX_25Q_BOX][idx], latencies[IDX_75Q_BOX][idx], latencies[IDX_MAX_BOX][idx], latencies[IDX_MEDIAN_BOX][idx], latencies[IDX_MEAN_BOX][idx]);
    	
    	Collections.sort(durationData);
    	durations[IDX_MIN_BOX][idx] = durationData.get(0) / 1000000000.0;
    	durations[IDX_25Q_BOX][idx] = durationData.get(idx25Q) / 1000000000.0;
    	durations[IDX_75Q_BOX][idx] = durationData.get(idx75Q) / 1000000000.0;
    	durations[IDX_MAX_BOX][idx] = durationData.get(durationData.size()-1) / 1000000000.0;
    	if(size % 2 == 0) {
    		int left = size / 2 - 1;
    		int right = size / 2;	    		
    		durations[IDX_MEDIAN_BOX][idx] = (durationData.get(left) + durationData.get(right)) / 2  / 1000000000.0;
    	} else
    		durations[IDX_MEDIAN_BOX][idx] = durationData.get(size / 2) / 1000000000.0;	    	
    	durations[IDX_MEAN_BOX][idx] = duration / (totalReps * 1000000000.0);	
    	durations[IDX_CI_BOX][idx] = computeCI(invocation, durations[IDX_MEAN_BOX][idx] * 1000000000, durationData) / 1000000000.0;
//    	System.out.printf("[%d messages] duration min: %.3f, 25Q: %.3f, 75Q: %.3f, max: %.3f, meadian: %.3f, mean: %.3f\n", messages[idx], durations[IDX_MIN_BOX][idx], durations[IDX_25Q_BOX][idx], durations[IDX_75Q_BOX][idx], durations[IDX_MAX_BOX][idx], durations[IDX_MEDIAN_BOX][idx], durations[IDX_MEAN_BOX][idx]);
//    	System.out.printf("%d invocations have %d reps totally for %d messages in %.5f secs\n", invocation, totalReps, messages[idx], durations[IDX_MEAN_BOX][idx]);
    	
    	Collections.sort(gcData);
    	gcs[IDX_MIN_BOX][idx] = gcData.get(0);
    	gcs[IDX_25Q_BOX][idx] = gcData.get(idx25Q);
    	gcs[IDX_75Q_BOX][idx] = gcData.get(idx75Q);
    	gcs[IDX_MAX_BOX][idx] = gcData.get(gcData.size()-1);
    	if(size % 2 == 0) {
    		int left = size / 2 - 1;
    		int right = size / 2;	    		
    		gcs[IDX_MEDIAN_BOX][idx] = (gcData.get(left) + gcData.get(right)) / 2;
    	} else
    		gcs[IDX_MEDIAN_BOX][idx] = gcData.get(size / 2);	    	
    	gcs[IDX_MEAN_BOX][idx] = gc / totalReps;	
    	gcs[IDX_CI_BOX][idx] = computeCI(invocation, gcs[IDX_MEAN_BOX][idx], gcData); 
	}
	
	/**
	 * [throughput | latency | duration] vs. number of messages
	 * @param stats
	 * @param android
	 * @throws CSenseException 
	 */
	public void generateGraphSet1(BenchmarkConfiguration config) throws CSenseException {
		Integer[] timers = config.getTimer();
		int workers = 1;
		int sourceBurst = 1;
		int queueCapacity = 10;
		long sourceDelay = 0;
		long workerDelay = 0;		
		double[][] throughputs = new double[SIZE_DATA_BOX][timers.length];
	    double[][] latencies = new double[SIZE_DATA_BOX][timers.length];
		double[][] durations = new double[SIZE_DATA_BOX][timers.length];
		double[][] gcs = new double[SIZE_DATA_BOX][timers.length];
	    String[] xLabels = new String[timers.length];
	    Statistics _stats = new Statistics();
		_stats.loadXML(config.getStatisticsPath());
		for(int i = 0; i < timers.length; i++) {
	    	xLabels[i] = String.valueOf(timers[i]);
	    	int invocation = 0;
	    	int reps = 0;
	    	int totalReps = 0;
	    	double throughput = 0;
	    	double latency = 0;
	    	long duration = 0;
	    	int gc = 0;
	    	List<Double> throughputData = new ArrayList<Double>();
			List<Double> latencyData = new ArrayList<Double>();
			List<Double> durationData = new ArrayList<Double>();
			List<Double> gcData = new ArrayList<Double>();
	    	while(true) {
	    		String prefix = getStatisticsPrefix("timeChainOfWorkers", invocation, timers[i], workers, sourceBurst, queueCapacity, sourceDelay, workerDelay);			    	    
				_stats.setPrefix(prefix);
				if(_stats.get("benchmark") == null) break;
				reps = _stats.getInt("reps");
				totalReps += reps;			
				throughput += _stats.getDouble("throughput") * reps;
				latency += _stats.getDouble("latency") * reps;
				duration += _stats.getLong("duration");	
				gc += _stats.getInt("gc");	
				throughputData.add(_stats.getDouble("throughput"));
				latencyData.add(_stats.getDouble("latency"));
				durationData.add((double)_stats.getLong("duration") / reps);
				gcData.add((double)_stats.getInt("gc"));
				invocation++;
	    	}
	    	
	    	if(invocation == 0) {
	    		System.out.printf("[generateGraphSet1] return with invocation %d for prefix %s\n", invocation, _stats.getPrefix());
	    		return;
	    	}
	    	
	    	computeStatistics(i, invocation, totalReps, throughput, throughputData, throughputs, latency, latencyData, latencies, duration, durationData, durations, gc, gcData, gcs);	    	
		}
		
		Plotter plotter1 = new Plotter(config.getTitle() + ": Message Throughput on " + _platform);
		Plotter plotter2 = new Plotter(config.getTitle() + ": Message Latency on " + _platform);
//		Plotter plotter3 = new Plotter("Chain Benchmark Duration on " + _platform);
	    String url1 = plotter1.drawBoxChart(1, throughputs, "Timers(s)", xLabels, "Throughput(msgs/s)", 0);
	    String url2 = plotter2.drawBoxChart(1, latencies, "Timer(s)", xLabels, "Latency(us)", 2);
//	    String url3 = plotter3.drawBoxChart(1, durations, "Timer(s)", xLabels, "Duration(s)",5);
	    System.out.printf("[Throughput vs Timer]\n%s\n", url1);
	    System.out.printf("[Latency vs. Timer]\n%s\n", url2);	
//	    System.out.printf("[Duration vs. Timer]\n%s\n", url3);	    
	    if(!_android) {
	    	try {
				Utility.browse(url1);
				Utility.browse(url2);
//				Utility.browse(url3);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
	    }
	}
	
	/**
	 * [throughput | latency | duration] vs. source delay
	 * @param stats
	 * @param android
	 * @throws CSenseException 
	 */
	public void generateGraphSet2(BenchmarkConfiguration config) throws CSenseException {
		int timer = config.getTimer()[0];
		Long[] sourceDelays = config.getSourceDelay();
		int workers = 1;
		int sourceBurst = 1;
		int queueCapacity = 10;
		long workerDelay = 0;		
		double[][] throughputs = new double[SIZE_DATA_BOX][sourceDelays.length];
	    double[][] latencies = new double[SIZE_DATA_BOX][sourceDelays.length];
		double[][] durations = new double[SIZE_DATA_BOX][sourceDelays.length];	
		double[][] gcs = new double[SIZE_DATA_BOX][sourceDelays.length];
	    String[] xLabels = new String[sourceDelays.length];
	    Statistics _stats = new Statistics();
		_stats.loadXML(config.getStatisticsPath());
		for(int i = 0; i < sourceDelays.length; i++) {
	    	xLabels[i] = String.valueOf(sourceDelays[i]);
	    	int invocation = 0;
	    	int reps = 0;
	    	int totalReps = 0;
	    	double throughput = 0;
	    	double latency = 0;
	    	long duration = 0;
	    	int gc = 0;
	    	List<Double> throughputData = new ArrayList<Double>();
			List<Double> latencyData = new ArrayList<Double>();
			List<Double> durationData = new ArrayList<Double>();
			List<Double> gcData = new ArrayList<Double>();
	    	while(true) {
	    		String prefix = getStatisticsPrefix("timeChainOfWorkers", invocation, timer, workers, sourceBurst, queueCapacity, sourceDelays[i], workerDelay);			    	    
				_stats.setPrefix(prefix);
				if(_stats.get("benchmark") == null) break;
				reps = _stats.getInt("reps");
				totalReps += reps;
				throughput += _stats.getDouble("throughput") * reps;
				latency += _stats.getDouble("latency") * reps;
				duration += _stats.getLong("duration");
				gc += _stats.getInt("gc");
				throughputData.add(_stats.getDouble("throughput"));
				latencyData.add(_stats.getDouble("latency"));
				durationData.add((double)_stats.getLong("duration") / reps);
				gcData.add((double)_stats.getInt("gc"));
				invocation++;
	    	}
	    	
	    	if(invocation == 0) {
	    		System.out.printf("[generateGraphSet2] return with invocation %d for prefix %s\n", invocation, _stats.getPrefix());
	    		return;
	    	}
	    	computeStatistics(i, invocation, totalReps, throughput, throughputData, throughputs, latency, latencyData, latencies, duration, durationData, durations, gc, gcData, gcs);	    	
		}
		
		Plotter plotter1 = new Plotter(config.getTitle() + ": Message Throughput on " + _platform);
		Plotter plotter2 = new Plotter(config.getTitle() + ": Message Latency on " + _platform);
//		Plotter plotter3 = new Plotter("Chain Benchmark Duration on " + _platform);
		Plotter plotter4 = new Plotter(config.getTitle() + ": GC on " + _platform);
	    String url1 = plotter1.drawBoxChart(1, throughputs, "Source Delay(ms)", xLabels, "Throughput(msgs/s)", 0);
	    String url2 = plotter2.drawBoxChart(1, latencies, "Source Delay(ms)", xLabels, "Latency(us)", 2);
//	    String url3 = plotter3.drawBoxChart(1, durations, "Source Delay(ms)", xLabels, "Duration(s)", 2);
	    String url4 = plotter4.drawBoxChart(1, gcs, "Source Delay(ms)", xLabels, "GC", 0);
	    System.out.printf("[Throughput vs Source Delay]\n%s\n", url1);
	    System.out.printf("[Latency vs. Source Delay]\n%s\n", url2);	
//	    System.out.printf("[Duration vs. Source Delay]\n%s\n", url3);
	    System.out.printf("[GC vs. Source Delay]\n%s\n", url4);	    
	    if(!_android) {
	    	try {
				Utility.browse(url1);
				Utility.browse(url2);
//				Utility.browse(url3);
				Utility.browse(url4);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
	    }
	}
	
	/**
	 * [throughput | latency | duration] vs. queue capacity
	 * @param stats
	 * @param android
	 * @throws CSenseException 
	 */
	public void generateGraphSet3(BenchmarkConfiguration config) throws CSenseException {
		int timer = config.getTimer()[0];
		Integer[] queueCapacities = config.getQueueCapacity();
		int workers = 1;
		int sourceBurst = 1;
		long sourceDelay = 0;
		long workerDelay = 0;		
		double[][] throughputs = new double[SIZE_DATA_BOX][queueCapacities.length];
	    double[][] latencies = new double[SIZE_DATA_BOX][queueCapacities.length];
		double[][] durations = new double[SIZE_DATA_BOX][queueCapacities.length];	
		double[][] gcs = new double[SIZE_DATA_BOX][queueCapacities.length];		
	    String[] xLabels = new String[queueCapacities.length];
	    Statistics _stats = new Statistics();
		_stats.loadXML(config.getStatisticsPath());
		for(int i = 0; i < queueCapacities.length; i++) {
	    	xLabels[i] = String.valueOf(queueCapacities[i]);
	    	int invocation = 0;
	    	int reps = 0;
	    	int totalReps = 0;
	    	double throughput = 0;
	    	double latency = 0;
	    	long duration = 0;
	    	int gc = 0;
	    	List<Double> throughputData = new ArrayList<Double>();
			List<Double> latencyData = new ArrayList<Double>();
			List<Double> durationData = new ArrayList<Double>();
			List<Double> gcData = new ArrayList<Double>();
	    	while(true) {
	    		String prefix = getStatisticsPrefix("timeChainOfWorkers", invocation, timer, workers, sourceBurst, queueCapacities[i], sourceDelay, workerDelay);			    	    
				_stats.setPrefix(prefix);
				if(_stats.get("benchmark") == null) break;
				reps = _stats.getInt("reps");
				totalReps += reps;
				throughput += _stats.getDouble("throughput") * reps;
				latency += _stats.getDouble("latency") * reps;
				duration += _stats.getLong("duration");
				gc += _stats.getInt("gc");
				throughputData.add(_stats.getDouble("throughput"));
				latencyData.add(_stats.getDouble("latency"));
				durationData.add((double)_stats.getLong("duration") / reps);
				gcData.add((double)_stats.getInt("gc"));
				invocation++;
	    	}
	    	
	    	if(invocation == 0) {
	    		System.out.printf("[generateGraphSet3] return with invocation %d for prefix %s\n", invocation, _stats.getPrefix());
	    		return;
	    	}
	    	computeStatistics(i, invocation, totalReps, throughput, throughputData, throughputs, latency, latencyData, latencies, duration, durationData, durations, gc, gcData, gcs);	    	
		}
		
		Plotter plotter1 = new Plotter(config.getTitle() + ": Message Throughput on " + _platform);
		Plotter plotter2 = new Plotter(config.getTitle() + ": Message Latency on " + _platform);
//		Plotter plotter3 = new Plotter("Chain Benchmark Duration on " + _platform);
	    String url1 = plotter1.drawBoxChart(1, throughputs, "Queue Capacity", xLabels, "Throughput(msgs/s)", 0);
	    String url2 = plotter2.drawBoxChart(1, latencies, "Queue Capacity", xLabels, "Latency(us)", 2);
//	    String url3 = plotter3.drawBoxChart(1, durations, "Queue Capacity", xLabels, "Duration(s)", 2);
	    System.out.printf("[Throughput vs Queue Capacity]\n%s\n", url1);
	    System.out.printf("[Latency vs. Queue Capacity]\n%s\n", url2);	
//	    System.out.printf("[Duration vs. Queue Capacity]\n%s\n", url3);	    
	    if(!_android) {
	    	try {
				Utility.browse(url1);
				Utility.browse(url2);
//				Utility.browse(url3);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
	    }
	}
	
	public void generateGCTraceGraph(BenchmarkConfiguration config) throws CSenseException {
		int duration = config.getTimer()[0];
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("gc.log"));
		} catch (FileNotFoundException e) {
			throw new CSenseException("gc.log is not found", e);
		}
		
		String line = null;
		List<Double> timestamps = new ArrayList<Double>();
		try {
			while((line = reader.readLine()) != null) {
				if(line.indexOf("GC") != -1) {
					int idx = line.indexOf(":");
					double timestamp = Double.parseDouble(line.substring(0, idx));
					if(timestamp > duration) break;
					timestamps.add(timestamp);
				}				
			}
			reader.close();
		} catch (IOException e) {
			throw new CSenseException("failed to access gc.log", e);
		}

		double[] data = new double[timestamps.size() ];
		for(int i = 0; i < data.length; i++) data[i] = timestamps.get(i);
		Plotter plotter = new Plotter(config.getTitle() + ": GC Messages on " + _platform);
	    String url = plotter.drawTimePulseTraceChart(data, duration, "Time(s)", "GC");
	    System.out.println("GCTraceGraph: " + url);
	    if(!_android) {
	    	try {
				Utility.browse(url);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
	    }
	}
	
	public void generateQueueSizeTraceGraph(BenchmarkConfiguration config) throws CSenseException {
		StatisticsLogger logger = new StatisticsLogger(config.getStatisticsPath());
		long total = logger.size();
		final int MAX_SAMPLES = 127;
		int batchSize = (int)((total-1) / (MAX_SAMPLES-1));
		int remaining = (int)(total - batchSize * (MAX_SAMPLES-1));
		int samples = 1 + (int)((total-1) / batchSize) + (remaining > 0 ? 1 : 0);
		double[] data;
		if(total <= batchSize) {
			samples = (int)total;
			ByteBuffer buf = ByteBuffer.allocate(samples);
			logger.read(buf);
			data = buf.asDoubleBuffer().array();
		} else {	
			data = new double[samples];
			ByteBuffer buf = ByteBuffer.allocate(batchSize);
			for(int i = 0; i < data.length; i++) {
				if(i == 0) {
					buf.limit(1);
					logger.read(buf);
					buf.flip();
					data[0] = buf.get();
				} else {				
					int count = logger.read(buf);
					buf.flip();
					double sum = 0;
//					System.out.printf("total: %d, batchSize: %d, down samples: %d, batch: %d, count: %d, base: %d\n", 
//							total, batchSize, samples, i, count, base);
					for(int j = 0; j < count; j++) sum += buf.get(j);
					data[i] = Math.round(sum / count);
				}
				buf.clear();
			}
		}
		
		Plotter plotter = new Plotter(config.getTitle() + ": Queue Size Variation on " + _platform);
	    String url = plotter.drawTraceChart(data, total, 0, "Iterations", "Queue Size");
	    if(!_android) {
	    	try {
				Utility.browse(url);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
	    }
	}
}
