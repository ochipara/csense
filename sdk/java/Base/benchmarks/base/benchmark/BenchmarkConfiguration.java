package base.benchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import edu.uiowa.csense.profiler.Utility;

public class BenchmarkConfiguration {
	private int _trials;
	private boolean _gc;

	private String _statsPath;
	private String _title;
	private String _benchmark;
	private Integer[] _timer;
	private Integer[] _workers;
	private Integer[] _sourceBurst;
	private Integer[] _queueCapacity;
	private Long[] _sourceDelay;
	private Long[] _workerDelay;	
	
	public BenchmarkConfiguration(String title, String benchmark, int trials, boolean gc) {
		this(title, benchmark, trials, gc, null);
	}
	
	public BenchmarkConfiguration(String title, String benchmark, int trials, boolean gc, String statsPath) {
		_title = title;
		_trials = trials;
		_gc = gc;
		_benchmark = benchmark;
		_statsPath = statsPath;
	}
	
	public String getStatisticsPath() {
		return _statsPath;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public Integer[] getTimer() {
		return _timer;
	}
	
	public Integer[] getWorkers() {
		return _workers;
	}
	
	public Integer[] getQueueCapacity() {
		return _queueCapacity;
	}
	
	public Integer[] getSourceBurst() {
		return _sourceBurst;
	}
	
	public Long[] getSourceDelay() {
		return _sourceDelay;
	}
	
	public Long[] getWorkerBurst() {
		return _workerDelay;
	}
	
	public void defineTimers(Integer...timers) {
		_timer = timers;
	}
	
	public void defineWorkers(Integer...workers) {
		_workers = workers;
	}
	
	public void defineSourceBurst(Integer...burst) {
		_sourceBurst = burst;
	}
	
	public void defineQueueCapacity(Integer...capacities) {
		_queueCapacity = capacities;
	}
	
	public void defineSourceDelay(Long...delays) {
		_sourceDelay = delays;
	}
	
	public void defineWorkerDelay(Long...delays) {
		_workerDelay = delays;
	}
	
	public String[] build() {
		List<String> args = new ArrayList<String>();
		args.add("--debug-reps");
		args.add("1");
		args.add("--trials");
		args.add(String.valueOf(_trials));
		if(_gc) args.add("--captureVmLog");
		if(_benchmark != null) args.add("-Dbenchmark=" + _benchmark);
		if(_timer != null) args.add("-DTimer=" + Utility.join(_timer));
		if(_workers != null) args.add("-DWorkers=" + Utility.join(_workers));
		if(_sourceBurst != null) args.add("-DSourceBurst=" + Utility.join(_sourceBurst));
		if(_queueCapacity != null) args.add("-DQueueCapacity=" + Utility.join(_queueCapacity));
		if(_sourceDelay != null) args.add("-DSourceDelay=" + Utility.join(_sourceDelay));
		if(_workerDelay != null) args.add("-DWorkerDelay=" + Utility.join(_workerDelay));
		return args.toArray(new String[args.size()]);
	}

	public <T extends SimpleBenchmark> void run(Class<T> benchmark) {
		if(_statsPath != null) new File(_statsPath).delete();
		Runner.main(benchmark, build());
	}
}
