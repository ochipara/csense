package profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CPUPowerProfiler {    
    public static final File basePath = new File("/sys/devices/system/cpu/"); 

    public static String readString(File f) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(f));
	String line = reader.readLine();	
	reader.close();
	return line;	
    }

    public static Long readLong(File f) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(f));
	Long line = Long.parseLong(reader.readLine());	
	reader.close();
	return line;
    }

    private static Boolean readBoolean(File f) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(f));
	Boolean line = Boolean.parseBoolean(reader.readLine());	
	reader.close();
	return line;
    }

    public static List<CPUPower> readerPower() throws IOException {
	List<CPUPower> states = new ArrayList<CPUPower>();

	File[] files = basePath.listFiles();
	for (int i = 0; i < files.length; i++) {
	    File cpuFile = files[i];
	    String cpuName = cpuFile.getName();
	    if (cpuName.startsWith("cpu")) {
		cpuName = cpuName.substring(3, cpuName.length());
		try {
		    int core = Integer.parseInt(cpuName);

		    if (cpuFile.isDirectory()) {
			File state0 = new File(cpuFile, "cpuidle/state0");
			File state1 = new File(cpuFile, "cpuidle/state1");
			File state2 = new File(cpuFile, "cpuidle/state2");
			File state3 = new File(cpuFile, "cpuidle/state3");

			states.add(readStats(core, state0));
			states.add(readStats(core, state1));
			states.add(readStats(core, state2));
			states.add(readStats(core, state3));
		    }
		} catch (NumberFormatException e) {
		    // ignore, this is a folder we are not interested in
		}
	    }
	}

	return states;
    }

    private static CPUPower readStats(int core, File stateDir) throws IOException {
	CPUPower cpuState = new CPUPower(core);

	String desc = readString(new File(stateDir, "desc"));
	Long latency = readLong(new File(stateDir, "latency"));
	String name = readString(new File(stateDir, "name"));
	Long power = readLong(new File(stateDir, "power"));
	Long time = readLong(new File(stateDir, "time"));
	Long usage = readLong(new File(stateDir, "usage"));

	cpuState.setDescription(desc);
	cpuState.setLatency(latency);
	cpuState.setName(name);
	cpuState.setPower(power);
	cpuState.setTime(time);
	cpuState.setUsage(usage);

	return cpuState;
    }
}
