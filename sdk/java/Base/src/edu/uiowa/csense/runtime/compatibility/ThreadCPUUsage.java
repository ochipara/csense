/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the COPYING file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package edu.uiowa.csense.runtime.compatibility;

/**
 * Get the user and system CPU time used per-thread via JNI and the system
 * getrusage() call.
 * <p>
 * Synopsis:<br/>
 *   <pre>
 *   ThreadCPUUsage cpuUsage = getCPUUsage();
 *   System.out.println(
 *     "totalUserCPUTime=" + cpuUsage.getTotalUserCPUTime() + ", " +
 *     "totalSystemCPUTime=" + cpuUsage.getTotalSystemCPUTime()
 *   );
 *   </pre>
 * <p>
 * Note that Java needs to be invoked with either a {@code LD_LIBRARY_PATH}
 * environment variable that points to the location of your {@code
 * libThreadCPUUsage.so} file, or you can use {@code -Djava.library.path} on
 * the command line instead.
 * <p>
 * Example: {@code libThreadCPUUsage.so} and {@code threadCPUUsage.jar}
 * installed in {@code /usr/local/jni/lib}.
 * <pre>
 * java -classpath /usr/local/jni/lib/threadCPUUsage.jar -Djava.library.path=/usr/local/jni/lib <YourClassName>
 * </pre>
 * 
 * @author Brian Koehmstedt
 */ 
public class ThreadCPUUsage
{
    private long seconds;
    private long nanoseconds;
    private long processSeconds;
    private long processNanoseconds;
    private long threadSeconds;
    private long threadNanoseconds;
    private long threadUserSeconds;
    private long threadUserMicroseconds;
    private long threadSystemSeconds;
    private long threadSystemMicroseconds;
    private static final ThreadLocal<ThreadCPUUsage> usage = new ThreadLocal<ThreadCPUUsage>() {
	@Override protected ThreadCPUUsage initialValue() {
            return new ThreadCPUUsage();
	}
    };

    private static final native ThreadCPUUsage getusage(ThreadCPUUsage u);

    /**
     * Private constructor.
     */
    private ThreadCPUUsage()
    {
    }

    public long getRealTimeSeconds() {
	return seconds;
    }

    public long getRealTimeNanoseconds() {
	return nanoseconds;
    }

    public long getRealTime() {
	return seconds * 1000000000 + nanoseconds;
    }
    
    public long getProcessTime() {
	return processSeconds * 1000000000 + processNanoseconds;
    }
    
    public long getProcessSeconds() {
	return processSeconds;
    }
    
    public long getProcessNanoseconds() {
	return processNanoseconds;
    }
    
    public long getThreadTime() {
	return threadSeconds * 1000000000 + threadNanoseconds;
    }
    
    public long getThreadSeconds() {
	return threadSeconds;
    }
    
    public long getThreadNanoseconds() {
	return threadNanoseconds;
    }

    public long getThreadUserTime() {
	return threadUserSeconds * 1000000000 + threadUserMicroseconds * 1000;
    }
    
    public long getThreadUserSeconds() {
	return threadUserSeconds;
    }

    public long getThreadUserMicroseconds() {
	return threadUserMicroseconds;
    }

    public long getThreadSystemTime() {
	return threadSystemSeconds * 1000000000 + threadSystemMicroseconds * 1000;
    }
    
    public long getThreadSystemSeconds() {
	return threadSystemSeconds;
    }

    public long getThreadSystemMicroseconds() {
	return threadSystemMicroseconds;
    }

    /**
     * Get an instance of {@code ThreadCPUUsage} which will represent the CPU
     * time used for the thread at time of calling.  This uses JNI to call
     * {@code getrusage()} at the system level.
     *
     * @return A {@code ThreadCPUUsage} object containing user and system CPU
     *         time used for the current thread.
     */
    public static final ThreadCPUUsage getCPUUsage() {
//	try {
//	    Class<?> ManagementFactory = Class.forName("java.lang.management.ManagementFactory");
//	    Class<?> ThreadMXBean = Class.forName("java.lang.management.ThreadMXBean");
//	    try {
//		Object bean = ManagementFactory.getMethod("getThreadMXBean").invoke(null);
//		long cpuTime = (Long)ThreadMXBean.getMethod("getCurrentThreadCpuTime").invoke(bean);
//		long utime = (Long)ThreadMXBean.getMethod("getCurrentThreadUserTime").invoke(bean);
//		long stime = cpuTime - utime;
//		ThreadCPUUsage usage = new ThreadCPUUsage();
//		usage.seconds = cpuTime / 1000000000;
//		usage.nanoseconds = cpuTime % 1000000000;
//		usage.userSeconds = utime / 1000000000;
//		usage.userMicroseconds = (utime - (usage.userSeconds * 1000000000)) / 1000;
//		usage.systemSeconds = stime / 1000000000;
//		usage.systemMicroseconds = (stime - (usage.systemSeconds * 1000000000)) / 1000;
//		return usage;
//	    } catch (Exception e) {
//		throw new CSenseRuntimeException("Failed to get thread CPU usage through java.lang.management.", e);
//	    }
//	} catch (ClassNotFoundException e) {
	    // call the native method
	    return ThreadCPUUsage.getusage(usage.get());
//	}
    }

    // load the JNI shared object
    //  static
    //  {
    //    System.loadLibrary("ThreadCPUUsage");
    //  }
}