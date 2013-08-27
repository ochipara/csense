package edu.uiowa.csense.profiler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory{
	protected String name;
	protected static ThreadFactory defaultFactory;
	protected static int count = 0;
	
	public NamedThreadFactory(String name) {
		this.name = name;
		NamedThreadFactory.defaultFactory = Executors.defaultThreadFactory();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = defaultFactory.newThread(r);
		t.setName(name + "-" + count);
		count = count + 1;
		return t;
	}
}
