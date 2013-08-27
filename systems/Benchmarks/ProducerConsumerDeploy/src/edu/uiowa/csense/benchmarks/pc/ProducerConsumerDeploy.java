package edu.uiowa.csense.benchmarks.pc;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.configuration.ProjectConfiguration;
import edu.uiowa.csense.compiler.matlab.MatlabOptions;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.runtime.v4.MessagePoolAtomic;
import edu.uiowa.csense.runtime.v4.MessagePoolBlockingQueue;
import edu.uiowa.csense.runtime.v4.NoMessagePool;

public class ProducerConsumerDeploy {
    static int samples = -1;
    static int numWorkers = -1;
    static long delay = -1;
    static long burst = 1;
    static long produce = 1;
    static Class poolClass = null;
    static String version = "v4";

    protected static void processArguments(String[] args) {
	StringBuffer sb = new StringBuffer("args");
	for (int i = 0; i < args.length; i++) sb.append(args[i] + " ");
	System.out.println(sb.toString());
	for (int i = 0; i < args.length; i++) {
	    if ("-samples".equals(args[i])) {
		i = i + 1;
		samples = Integer.parseInt(args[i]);
	    } else if ("-workers".equals(args[i])) {
		i = i + 1;
		numWorkers = Integer.parseInt(args[i]);
	    } else if ("-delay".equals(args[i])) {
		i = i + 1;
		delay = Long.parseLong(args[i]);
	    } else if ("-produce".equals(args[i])) {
		i = i + 1;
		produce = Long.parseLong(args[i]);
	    } else if ("-pool".equals(args[i])) {
		i = i + 1;
		String pool = args[i];
		if ("queue".equals(pool)) {
		    poolClass = MessagePoolBlockingQueue.class;
		} else if ("none".equals(pool)) {
		    poolClass = NoMessagePool.class;
		} else if ("atomic-queue".equals(pool)) {
		    poolClass = MessagePoolAtomic.class;
		} else if ("nolocking".equals(pool)) {
		    version = "v3";
		    poolClass = MessagePoolAtomic.class;
		}
	    } else throw new IllegalArgumentException("Invalid argument " + args[i]);
	}
	
	
	if (samples <= 0) throw new IllegalArgumentException("samples not set");
	if (numWorkers <= 0) throw new IllegalArgumentException("workers not set");
	if (delay < 0) throw new IllegalArgumentException("workers not set");
	if (produce < 0) throw new IllegalAccessError("prodcue not set");
	if (poolClass == null) throw new IllegalAccessError("must specify pool");
    }
    
    public static void main(String[] args) throws CompilerException {
	processArguments(args);
	
	File projectDirectory = new File(System.getProperty("user.dir"));
	Project project = new Project(projectDirectory, "ProducerConsumer", "default");
	project.addSourceDirectory(new File(projectDirectory, "bin/classes"));
	project.setApi(version);
	
	System.out.println("samples: " + samples);
	System.out.println("workers: " + numWorkers);
	System.out.println("delay: " + delay);
	System.out.println("produce: " + produce);
//	System.out.println("pool: " + ProjectConfiguration.messagePool.getSimpleName());
//	System.out.println("task: " + ProjectConfiguration.taskQueue.getSimpleName());
	System.out.println("==============================================================");
	
	FrameTypeC shorts = TypeInfoC.newShortVector(samples);
	
	project.addComponent("source", new BenchmarkSourceC(shorts, produce, burst));
	String prevWorker = "source";
	String workerName = null;
	for (int w = 0; w < numWorkers; w++) {
	    workerName = "worker" + w;
	    project.addComponent(workerName, new BenchmarkWorkerC(shorts, delay));
	    project.link(prevWorker, workerName);
	    prevWorker = workerName;
	}
	project.toTap(workerName,shorts);

//	Options.trepnProfiler = poolClass.getSimpleName() + "-p" + produce + "-d" + delay;
//	ProjectConfiguration.messagePool = poolClass;
	Options.generateProfileCode = false;
	MatlabOptions.printPostResults = false;
	MatlabOptions.printPreResults = false;	
	
	project.compile();
    }
}
