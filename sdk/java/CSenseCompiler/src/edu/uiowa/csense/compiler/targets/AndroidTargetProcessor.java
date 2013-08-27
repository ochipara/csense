package edu.uiowa.csense.compiler.targets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.AndroidInclude;
import edu.uiowa.csense.compiler.configuration.AndroidTool;
import edu.uiowa.csense.compiler.configuration.CompilerTool;
import edu.uiowa.csense.compiler.configuration.Include;
import edu.uiowa.csense.compiler.configuration.ProjectConfiguration;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.configuration.Version;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.models.android.AndroidManifest;
import edu.uiowa.csense.compiler.project.resources.FileResource;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.compiler.utils.ExecuteCommand;
import edu.uiowa.csense.compiler.utils.JavaCoder;

public class AndroidTargetProcessor extends CompileTargetProcessor {
    protected final Logger logger = Logger.getLogger("android-target");
    protected final ExecuteCommand create = ExecuteCommand.executeCommand();
    private AndroidManifest manifest;

    public AndroidTargetProcessor() throws CompilerException {
	super();
    }

    @Override
    public void initialize(Project project, Target target) throws CompilerException {
	AndroidTarget androidTarget = (AndroidTarget) target;
	logger.info("Initializing target " + target.getName());
	//addResources(project);

	ToolkitConfiguration sdkConfig = project.getSdkConfig();
	AndroidTool androidTool = (AndroidTool) sdkConfig
		.getTool(AndroidTool.TOOL_NAME);
	CompilerTool compilerTool = (CompilerTool) sdkConfig
		.getTool(CompilerTool.TOOL_NAME);

	//ResourceManager rm = project.getResourceManager();

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

	newEclipseProjectForAndroid(project, target);

	// add the default activity	
	try {
	    File activity = new File(compilerTool.getSourceDirectory(),
		    "EgoDeployActivity.inc");

	    String fileName = androidTarget.getPackage().replace('.', '/');
	    File activityDest = new File(target.getSourceDirectory(),
		    new File(fileName, "CSenseDeployActivity.java").getPath());

	    JavaCoder coder = new JavaCoder();
	    coder.code("package " + androidTarget.getPackage() + ";");
	    coder.code("import edu.uiowa.csense.CSenseService;");
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
	//rm.addResource(layoutRes);
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


	deployToolkit(sdkConfig, project, target);
    }

    private void deployToolkit(ToolkitConfiguration sdkConfig, Project project, Target target) throws CompilerException {
	String api = project.getApi();
	String platform = target.getPlatform();
	
	// deploy the appropriate toolkit version files
	Version version = sdkConfig.getVersion(api, platform);
	if (version == null) {
	    throw new CompilerException("Failed to find version api:" + api + " platform:" + platform);
	}
	ResourceManager rm = project.getResourceManager();
	for (String p : version.getPackages()) {
	    rm.addPackage(p);
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
	    manifest.addPermission("android.permission.WAKE_LOCK");
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
}
