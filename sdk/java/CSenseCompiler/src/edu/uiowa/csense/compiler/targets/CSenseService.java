package edu.uiowa.csense.compiler.targets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.configuration.Version;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainCoder;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.project.resources.ClassResource;
import edu.uiowa.csense.compiler.project.resources.Resource;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.compiler.utils.JavaCoder;


/**
 * Generates the CSenseService.java for Android targets
 * 
 * @author ochipara
 *
 */
public class CSenseService {

    private static boolean matlab;

    /**
     * Creates the CSenseService.java text file that is placed inside of the
     * egoDeploy project.
     * 
     * @throws CompilerException
     */
    public static void generateCSenseService(Project project, Target target) throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	JavaCoder coder = new JavaCoder();
	Version version = project.getSdkConfig().getVersion(project.getApi(), target.getPlatform());
	if (version == null) {
	    throw new CompilerException("Could not find api " + 
		    project.getApi() + " for platform " + target.getPlatform());
	}

	matlab = false;
	for (IComponentC component : project.getMainGroup().getComponents()) {
	    if (component instanceof MatlabComponentC) {
		matlab = true;
		break;
	    }
	}

	// add imports
	coder.code("package edu.uiowa.csense;");
	coder.code("import java.util.Set;");
	
	for (String p : version.getPackages()) {
	    coder.code("import " + p + ".*;");
	}

	// extract the import from the classes to be deployed
	Set<String> packages = new HashSet<String>();
	for (Resource resource : rm.getResources()) {
	    if (resource instanceof ClassResource) {
		ClassResource cls = (ClassResource) resource;
		packages.add(cls.getPackage());
	    }
	}

	for (String p : packages) {
	    coder.code("import " + p + ".*;");
	}

	coder.newline();
	coder.code("import android.app.Service;");
	coder.code("import android.os.Binder;");
	coder.code("import android.os.Bundle;");
	coder.code("import android.content.Intent;");
	coder.code("import android.os.IBinder;");
	coder.code("import android.widget.Toast;");

	coder.code("import java.io.IOException;");
	coder.code("import java.util.List;");
	coder.newline();

	// read the start of the CSenseService
	try {
	    coder.code("public class CSenseService extends Service {");
	    coder.code("private static final String TAG = \"csense-service\";");

	    coder.newline();
	    coder.code("static {");
	    coder.code("System.out.println(\"attempting to load native support\");");
	    coder.code("System.loadLibrary(\"csense-native\");");
	    coder.code("System.out.println(\"native support loaded\");");
	    if (matlab) {
		coder.code("CSenseLib.CSenseLib_initialize();");
	    }
	    coder.code("}");
	    coder.newline();

	    // create the binder
	    coder.code("public class CSenseServiceBinder extends Binder {");
	    coder.code("public CSenseService getService() {");
	    coder.code("return CSenseService.this;");
	    coder.code("}");
	    coder.code("}");
	    coder.code("private final IBinder binder = new CSenseServiceBinder();");

	    MatlabComponentC.generate_makefile(target);
	    MatlabComponentC.run_make(project, target);

	    // field definitions
	    DomainManager domainManager = DomainManager.domainManager();
	    for (Domain domain : domainManager.domains()) {
		coder.code("private IScheduler " + domain.schedulerName() + ";");
	    }
	    coder.code("private CSenseToolkit csense = new CSenseAndroidToolkit(this);");	    
	    coder.code("private boolean failedInit = false;");
	    coder.newline();

	    coder.code("public CSenseService() throws CSenseException {");
	    coder.code("csense.setComponentClass(" + version.getComponent() + ".class);");
	    coder.code("csense.setSourceClass(" + version.getSource() + ".class);");
	    coder.code("csense.setMessagePoolClass(" + version.getMessagePool() + ".class);");
	    coder.code("csense.setSchedulerClass(" + version.getScheduler() + ".class);");
	    coder.code("csense.setTaskQueueClass(" + version.getTaskQueue() + ".class);");
	    coder.code("csense.setEventQueueClass(" + version.getEventQueue() + ".class);");
	    coder.code("csense.setIdleLockClass(" + version.getIdleLock() + ".class);");

	    coder.code("}");

	    generateOnCreate(coder);
	    generateOnStartCommand(coder, domainManager);
	    generateOnDestroy(coder, domainManager);
	    generateOnBind(coder);

	    coder.code("private void InitializeSystem() throws CSenseException {");
	    DomainCoder.codeDomains(project.getMainGroup(), coder);

	    if (Options.generateProfileCode) {
		coder.newline();
		coder.comment("generating profiling code");
		coder.code("Debug.startTracing(Environment.environment.getExternalStorageDirectory(), " + Options.maxTraceFileSize + " * 1024 * 1024);");
	    }
	    coder.code("}\n");
	    coder.code("}\n");
	    coder.saveToFile(new File(target.getDirectory(), "src/edu/uiowa/csense/CSenseService.java"));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    private static void generateOnBind(JavaCoder coder) {
	coder.annotation("@Override");
	coder.code("public IBinder onBind(Intent intent) {");
	coder.code("return binder;");
	coder.code("}");
    }

    private static void generateOnStartCommand(JavaCoder coder, DomainManager domainManager) {
	coder.annotation("@Override");
	coder.code("public int onStartCommand(Intent intent, int flags, int startId) {");
	coder.code("if (failedInit == true) return START_NOT_STICKY;");
	coder.newline();

	coder.comment("read the cpu power profile");
	coder.code("try {");
	coder.code("List<CPUPower> cpuPower = CPUPowerProfiler.readerPower();");
	coder.code("for (int i = 0; i < cpuPower.size(); i++) {");
	coder.code("Log.i(\"profile\", cpuPower.get(i).toString());");
	coder.code("}");
	coder.code("} catch (IOException e1) {");
	coder.code("e1.printStackTrace();");
	coder.code("}");


	coder.comment("putting extras in the workspace");
	coder.code("Bundle extra = intent.getExtras();");
	coder.code("if (extra != null) {");
	coder.code("Set<String> keys = intent.getExtras().keySet();");
	coder.code("for (String key : keys ) {");
	coder.code("Object val = extra.get(key);");
	coder.code("Log.i(TAG, \"workspace: \" + key + \"=\" + val);");
	coder.code("Workspace.getWorkspace().setValue(key, val);");
	coder.code("}");
	coder.code("}");

	coder.code("Boolean autostart = (Boolean) Workspace.getWorkspace().getValue(\"AUTOSTART_SCHEDULERS\");");
	coder.code("if ((autostart == null) || (autostart)) {");
	for (Domain domain : domainManager.domains()) {
	    coder.code("if(" + domain.schedulerName() + " != null && !"
		    + domain.schedulerName() + ".isActive()) {");
	    coder.code("Log.i(TAG, \"starting " + domain.schedulerName()
		    + "\");");
	    coder.code(domain.schedulerName() + ".start();");
	    coder.code("}");
	}
	coder.code("}");
	coder.code("return START_REDELIVER_INTENT;");
	coder.code("}");
	coder.newline();
    }

    private static void generateOnDestroy(JavaCoder coder, DomainManager domainManager) {
	coder.annotation("@Override");
	coder.code("public void onDestroy() {");	
	for (Domain domain : domainManager.domains()) {
	    coder.code("if(" + domain.schedulerName() + " != null && "
		    + domain.schedulerName() + ".isActive()) {");
	    coder.code(domain.schedulerName() + ".stop();");
	    coder.code("Log.i(TAG, \"stopping " + domain.schedulerName()
		    + "\");");
	    if (matlab) {
		coder.code("CSenseLib.CSenseLib_terminate();");
	    }
	    coder.code("}");
	}

	if (Options.generateProfileCode) coder.code("Debug.stopTracing();");
	coder.code("Toast.makeText(this, \"CSenseService destroyed...\", Toast.LENGTH_SHORT).show();");
	coder.code("super.onDestroy();");

	if (Options.trepnProfiler != null) {
	    coder.newline();
	    coder.comment("End the trepn profiler --- only works for the qualcomm devices");

	    coder.code("Intent stopProfiling = new Intent(\"com.quicinc.trepn.stop_profiling\");"); 
	    coder.code("sendBroadcast(stopProfiling);");
	}

	coder.comment("read the cpu power profile");
	coder.code("try {");
	coder.code("List<CPUPower> cpuPower = CPUPowerProfiler.readerPower();");
	coder.code("for (int i = 0; i < cpuPower.size(); i++) {");
	coder.code("Log.i(\"profile\", cpuPower.get(i).toString());");
	coder.code("}");
	coder.code("} catch (IOException e1) {");
	coder.code("e1.printStackTrace();");
	coder.code("}");

	coder.code("}");
	coder.newline();
    }

    private static void generateOnCreate(JavaCoder coder) {
	coder.annotation("@Override");
	coder.code("public void onCreate() {");
	coder.code("super.onCreate();");
	if (Options.trepnProfiler != null) {
	    coder.newline();
	    coder.comment("Start the trepn profiler --- only works for the qualcomm devices");
	    coder.code("Intent startProfiling = new Intent(\"com.quicinc.trepn.start_profiling\");"); 
	    coder.code("startProfiling.putExtra(\"com.quicinc.trepn.database_file\", \"" + Options.trepnProfiler  + "\");");
	    coder.code("sendBroadcast(startProfiling);");
	}

	//coder.code("((CSenseToolkitAndroidImpl) CSense.getImplementation()).setContext(this);");
	coder.code("try {");
	coder.code("InitializeSystem();");
	coder.code("Toast.makeText(this, \"CSenseService created...\", Toast.LENGTH_SHORT).show();");
	coder.code("} catch (CSenseException e) {");
	coder.code("failedInit = true;");
	coder.code("e.printStackTrace();");
	coder.code("Toast.makeText(this, \"CSenseService WAS NOT CREATED...\", Toast.LENGTH_SHORT).show();");
	coder.code("}");
	coder.code("}");
	coder.newline();
    }
}
