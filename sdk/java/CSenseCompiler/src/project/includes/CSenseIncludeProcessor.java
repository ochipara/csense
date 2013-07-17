package project.includes;

import java.awt.Event;
import java.io.File;

import messages.ReadOnlyMessageException;
import messages.TypeInfo;
import messages.fixed.Vector;

import compatibility.Environment;
import compatibility.EnvironmentInterface;
import compatibility.Log;
import compatibility.LogFile;
import compatibility.SystemInfo;
import compatibility.ThreadCPUUsage;
import compatibility.Utils;
import compiler.CompilerException;
import compiler.model.Project;
import components.basic.CopyRefComponent;
import components.basic.MemoryInitialize;
import components.basic.MemorySource;
import components.basic.Merge;
import components.basic.Slice;
import components.basic.SyncQueue;
import components.basic.TapComponent;
import profile.CPUPower;
import profile.CPUPowerProfiler;
import project.resources.ResourceManager;
import api.CSenseRuntimeException;
import base.CSenseFormatter;
import base.Debug;
import base.NamedThreadFactory;
import base.Route;
import base.RouteEntry;
import base.RouteUsage;
import base.RoutingTable;
import base.Utility;

public class CSenseIncludeProcessor implements IncludeProcessor<CSenseInclude> {
    @Override
    public void process(Project project, CSenseInclude csense)
	    throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	for (File f : csense.getSources()) {
	    rm.addSourceDirectory(f);
	}

	// list of resources to be deployed by default
	rm.addPackage("api");
	rm.addPackage("api.concurrent");
	rm.addPackage("messages");
	rm.addPackage("messages.fixed");
	//rm.addPackage("compatibility");
	
	rm.addClass(Environment.class);
	rm.addClass(EnvironmentInterface.class);
	rm.addClass(Log.class);
	rm.addClass(LogFile.class);
	rm.addClass(ThreadCPUUsage.class);
	rm.addClass(Utils.class);
	
	rm.addClass(Utility.class);
	rm.addClass(SyncQueue.class);
	rm.addClass(CopyRefComponent.class);
	rm.addClass(Merge.class);
	rm.addClass(TapComponent.class);
	rm.addClass(MemorySource.class);
	rm.addClass(MemoryInitialize.class);
	rm.addClass(Slice.class);

	rm.addClass(Route.class);
	rm.addClass(RoutingTable.class);
	rm.addClass(NamedThreadFactory.class);
	rm.addClass(RouteUsage.class);

	rm.addClass(ReadOnlyMessageException.class);
	rm.addClass(Vector.class);
	rm.addClass(CSenseRuntimeException.class);
	rm.addClass(CSenseFormatter.class);
	rm.addClass(TypeInfo.class);
	
	// implementation
	rm.addPackage("base.concurrent");
	rm.addPackage("base.workspace");

	rm.addClass(EnvironmentInterface.class);
	rm.addClass(Environment.class);
	rm.addClass(Log.class);
	rm.addClass(ThreadCPUUsage.class);
	//rm.addClass(CSenseLib.class);
	rm.addClass(SystemInfo.class);
	rm.addClass(Utils.class);
	rm.addClass(LogFile.class);
	rm.addClass(Debug.class);
	rm.addClass(RouteEntry.class);
	
	// profiler
	rm.addClass(CPUPowerProfiler.class);
	rm.addClass(CPUPower.class);
    }
}
