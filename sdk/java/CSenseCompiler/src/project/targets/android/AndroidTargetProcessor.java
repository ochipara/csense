package project.targets.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import compatibility.AndroidEnvironment;
import compatibility.AndroidLogger;
import compiler.CompilerException;
import compiler.model.CSenseGroupC;
import compiler.model.Project;
import compiler.utils.ExecuteCommand;
import compiler.utils.JavaCoder;

import project.ProjectConfiguration;
import project.configuration.AndroidTool;
import project.configuration.CompilerTool;
import project.configuration.ToolkitConfiguration;
import project.includes.AndroidInclude;
import project.includes.Include;
import project.resources.FileResource;
import project.resources.ResourceManager;
import project.targets.CompileTargetProcessor;
import project.targets.Target;
import models.android.AndroidManifest;

import api.IComponentC;

public class AndroidTargetProcessor extends CompileTargetProcessor {
    protected final Logger logger = Logger.getLogger("android-target");
    protected final ExecuteCommand create = ExecuteCommand.executeCommand();
    private AndroidManifest manifest;

    public AndroidTargetProcessor() throws CompilerException {
	super();
    }

    @Override
    public void initialize(Project project, Target target)
	    throws CompilerException {
	AndroidTarget androidTarget = (AndroidTarget) target;
	logger.info("Initializing target " + target.getName());
	addResources(project);

	ToolkitConfiguration sdkConfig = project.getSdkConfig();
	AndroidTool androidTool = (AndroidTool) sdkConfig
		.getTool(AndroidTool.TOOL_NAME);
	CompilerTool compilerTool = (CompilerTool) sdkConfig
		.getTool(CompilerTool.TOOL_NAME);

	ResourceManager rm = project.getResourceManager();

	int code;
	String targetId = androidTool.getTarget();
	String cmd = androidTool.getSdkTools().getAbsolutePath()
		+ "/android create project --target " + targetId + " --name "
		+ project.getName() + " --path " + target.getDirectory()
		+ " --activity " + androidTarget.getActivity() + " --package "
		+ androidTarget.getPackage();

	code = create.execute(cmd);
	if (code != 0) {
	    System.err.println(create.getOutputMessage());
	    System.err.println(create.getErrorMessage());
	    throw new CompilerException("Failed to create new project by executing command " + cmd);
	}

	File jniDir = target.getJniDirectory();
	if (jniDir.exists() == false) {
	    if (jniDir.mkdir() == false) {
		throw new CompilerException("Failed to create new project");
	    }
	}

	newEclipseProjectForAndroid(project);

	// add the default activity	
	try {
	    File activity = new File(compilerTool.getSourceDirectory(),
			"EgoDeployActivity.inc");
	    File activityDest = new File(target.getSourceDirectory(),
			"edu/uiowa/csense/CSenseDeployActivity.java");
	    
	    JavaCoder coder = new JavaCoder();
	    coder.code("package edu.uiowa.csense;");
	    coder.code("import " + androidTarget.getPackage() + ".R;");
		
	    BufferedReader br = new BufferedReader(new FileReader(activity));
	    String line = br.readLine();
	    while (line != null)
	    do {		
		coder.append(line + "\n");
		line = br.readLine();
	    } while (line != null);
	    if (activityDest.getParentFile().exists() == false) {
		activityDest.getParentFile().mkdirs();
	    }
	    coder.saveToFile(activityDest);
	    br.close();
	} catch (FileNotFoundException e) {
	    throw new CompilerException(e);
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
	
	// and its layout
	File layout = new File(compilerTool.getSourceDirectory(), "main.xml.inc");
	File layoutTarget = new File(target.getResouceDirectory(), "layout/main.xml");
	FileResource layoutRes = new FileResource(layout);
	rm.addResource(layoutRes);
	layoutRes.deploy(layoutTarget);	
	
	try {
	    // copy the getrusage files
	    File location = androidTool.getRusage();	
	    FileUtils.copyDirectory(location, target.getJniDirectory());
	    
	    File csenseLibLocation = new File(compilerTool.getResourceDirectory(), "csenselib");
	    FileUtils.copyDirectory(csenseLibLocation, target.getJniDirectory());
	} catch (IOException e) {
	    throw new CompilerException(e);
	}	
    }

    @Override
    public void compile(Project project, Target target) throws CompilerException {
	generateManifest(project, (AndroidTarget) target);
	super.compile(project, target);
	ResourceManager rm = project.getResourceManager(); 
	rm.deployResources(target);
	CSenseService.generateCSenseService(project, target);
    }
    
    public AndroidManifest getManifest() {
	return manifest;
    }

    protected void generateManifest(Project project, AndroidTarget target)
	    throws CompilerException {
	CSenseGroupC cgraph = project.getMainGroup();
	ProjectConfiguration conf = project.getConfiguration();	

	AndroidInclude androidInclude = null;
	for (Include include : conf.getIncludes()) {
	    if (AndroidInclude.INCLUDE_NAME.equals(include.getType())) {
		if (androidInclude == null)
		    androidInclude = (AndroidInclude) include;
		else
		    throw new CompilerException(
			    "Cannot include multiple android projects");
	    }
	}

	if (androidInclude == null) {
	    manifest = new AndroidManifest(new File(target.getDirectory(), "AndroidManifest.xml"));
	} else { 
	    manifest = new AndroidManifest(new File(androidInclude.getDirectory(), "AndroidManifest.xml"));
	    System.out.println(target);
	    System.out.printf("manifest main activity: %s/%s\n", manifest.getPackage(), manifest.getMainActivity());
	}
    

	for (IComponentC comp : cgraph.getComponents()) {
	    List<String> permissions = comp.getPermission();
	    for (String permission : permissions) {
		manifest.addPermission(permission);
	    }
	}
	
	
	// String basePackage = "edu.uiowa.";
	// for (Activity activity : _activityManager.getActivities()) {
	// String name = activity.getClassName();
	// if (name.startsWith(basePackage) == false) {
	// throw new
	// CompilerException("Base package of class should be edu.uiowa.");
	// }
	//
	// name = name.substring(basePackage.length() - 1);
	// if (activity.isMain()) {
	// _manifest.newMainActivity(name);
	// } else {
	// _manifest.newActivity(name);
	// }
	//
	// }

	String packageName = target.getPackage();
	String serviceName = "edu.uiowa.csense.CSenseService";
	if (serviceName.subSequence(0, packageName.length()).equals(packageName)) {
	    // always starts with . (??)
	    String shortServiceName = serviceName.substring(packageName.length(), serviceName.length()); 
	    manifest.newService(shortServiceName);
	} else {
	    manifest.newService("edu.uiowa.csense.CSenseService");
	    //throw new CompilerException("Failed to add service");
	}
	manifest.addPermission("android.permission.WAKE_LOCK");
	
	// set the sdk versions
	manifest.setVersion(14, 17, 17);
	manifest.write(new File(target.getDirectory(), "AndroidManifest.xml"));
    }

    private void addResources(Project project) throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	String api = project.getApi();

	if ("v0".equals(api)) {
	    rm.addPackage("base.v0");
	} else if ("v1".equals(api)) {
	    rm.addPackage("base.v1");
	} else if ("v2".equals(api)) {
	    rm.addPackage("base.v2");
	} else if ("v3".equals(api)) {
	    rm.addPackage("base.v2");
	} else {
	    throw new CompilerException("Cannot find api implementation");
	}

	rm.addClass(AndroidEnvironment.class);
	rm.addClass(AndroidLogger.class);
    }

}
