package edu.uiowa.csense.profiler;

import java.util.Formatter;

public class CPUPower {
    /**
     * See  http://www.mjmwired.net/kernel/Documentation/cpuidle/sysfs.txt for a complete
     * description of the information stored in this class
     * 
     */
    protected int core;				// core
    protected String name;
    protected String description;
    protected boolean disable;			
    protected long latency;			// time to exist this state
    protected long power;			// power consumed in this state microseconds
    protected long time;			// time spent in this state in microseconds
    protected long usage;			// number of times entered this state

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter();

    @Override
    public String toString() {
	long nano = System.nanoTime();
	long seconds = nano / 1000000000;
	long microsecs = (nano - (seconds * 1000000000)) / 1000;  
	String s = "time: " + seconds + "." + microsecs + " core: " + core + " [" + name + "] latency: " + latency + " power: " + power + " time: " + time + " usage: " + usage;
	return s;
    }

    public CPUPower(int core) {
	this.core = core;
    }

    public int getCore() {
	return core;
    }

    public void setCore(int core) {
	this.core = core;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public boolean isDisable() {
	return disable;
    }

    public void setDisable(boolean disable) {
	this.disable = disable;
    }

    public long getLatency() {
	return latency;
    }

    public void setLatency(long latency) {
	this.latency = latency;
    }

    public long getPower() {
	return power;
    }

    public void setPower(long power) {
	this.power = power;
    }

    public long getTime() {
	return time;
    }

    public void setTime(long time) {
	this.time = time;
    }

    public long getUsage() {
	return usage;
    }

    public void setUsage(long usage) {
	this.usage = usage;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
