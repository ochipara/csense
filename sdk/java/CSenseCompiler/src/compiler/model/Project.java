package compiler.model;

import java.io.File;
import java.util.HashMap;
import api.IComponentC;
import models.android.AndroidManifest;

import org.apache.log4j.*;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.matlab.MatlabComponentC;
import compiler.transformations.collapsematlab.CollapseMatlabGroup;
import compiler.types.BaseTypeC;
import compiler.utils.FileUtils;
import project.Options;
import compiler.utils.Shell;
import project.ProjectConfiguration;
import project.builders.AntBuilder;
import project.builders.ProjectBuilder;
import project.configuration.AndroidTool;
import project.configuration.KeyTool;
import project.configuration.SwigTool;
import project.configuration.ToolkitConfiguration;
import project.includes.AndroidIncludeProcessor;
import project.includes.CSenseIncludeProcessor;
import project.includes.Include;
import project.includes.IncludeProcessor;
import project.includes.JarIncludeProcessor;
import project.resources.ResourceManager;
import project.targets.Target;
import project.targets.TargetProcessor;
import project.targets.android.AndroidTarget;
import project.targets.android.AndroidTargetProcessor;
import project.targets.desktop.DesktopTarget;
import project.targets.desktop.DesktopTargetProcessor;


/**
 * This sets up a project and allows you to deploy the application.
 * 
 * @author Octav Chipara
 * 
 */
public class Project {
    protected File projectDirectory = null;

    // the target processor
    protected Target target = null;
    protected TargetProcessor targetProcessor = null;

    protected static Project project = null;

    // keeps track of the component graph
    protected CSenseGroupC _mainGroup = new CSenseGroupC("Main", true) {
	@Override
	public void instantiate() throws CompilerException {
	    throw new CompilerException("here?");
	}
    };

    // keeps track of the file resources of the project
    protected ResourceManager _resourceManager = null;
    private static boolean _make = true;
    // protected List<File> _jars = new ArrayList<File>();

    protected ToolkitConfiguration toolkitConfiguration;
    protected ProjectConfiguration config;
    protected static boolean _codegen = true;

    // include processors
    protected HashMap<String, IncludeProcessor<? extends Include>> includeProcessors = new HashMap<String, IncludeProcessor<? extends Include>>();
    protected HashMap<String, TargetProcessor> targetProcessors = new HashMap<String, TargetProcessor>();

    private SwigTool swigTool;

    // logger
    protected static Logger logger = Logger.getLogger(Project.class);

    public Project(String projectFile, String targetName) throws CompilerException {
	this(new File(projectFile), targetName, false);
    }
    
    public Project(String projectFile, String targetName, boolean clean) throws CompilerException {
	this(new File(projectFile), targetName, clean);
    }

    public Project(File projectFile, String targetName) throws CompilerException {
	this(projectFile, targetName, false);
    }
    
    public Project(File projectFile, String targetName, boolean clean) throws CompilerException {
	BasicConfigurator.configure();
	logger.setLevel(Level.ALL);
	project = this;

	_resourceManager = new ResourceManager(this, projectDirectory);
	projectDirectory = projectFile.getParentFile();

	// load project components
	config = ProjectConfiguration.load(projectFile);

	// load the toolkit configuration
	toolkitConfiguration = ToolkitConfiguration.loadConfiguration(new File(projectDirectory, "csense.xml"));
	swigTool = (SwigTool) toolkitConfiguration.getTool("swig");

	// determine the target
	target = config.getTarget(targetName);
	if (target == null) {
	    throw new CompilerException("Cannot find target [" + targetName
		    + "]");
	}

	// construct the mapping between includes and processors
	includeProcessors.put("csense", new CSenseIncludeProcessor());
	includeProcessors.put("android", new AndroidIncludeProcessor());
	includeProcessors.put("jar", new JarIncludeProcessor());

	//
	targetProcessors.put(AndroidTarget.PLATFORM,
		new AndroidTargetProcessor());
	targetProcessors.put(DesktopTarget.PLATFORM,
		new DesktopTargetProcessor());

	include();

	//
	targetProcessor = targetProcessors.get(target.getPlatfrom());
	if (targetProcessor == null)
	    throw new CompilerException(
		    "No target processor registered for platform ["
			    + target.getPlatfrom() + "]");
	
//	remove the target directory before compilation in case of inconsistency with Eclipse
//	the resulting project will be built and installed using ant
//	no need to import the generated project	
	if(clean && FileUtils.delete(target.getDirectory()))
	    logger.info("removed target directory: " + target.getDirectory().getAbsolutePath());
	
	targetProcessor.initialize(project, target);
    }

    protected void include() throws CompilerException {
	for (Include include : config.getIncludes()) {
	    String type = include.getType();
	    IncludeProcessor<Include> includeProcessor = (IncludeProcessor<Include>) includeProcessors.get(type);
	    if (includeProcessor == null) {
		throw new CompilerException("Include processor for type ["
			+ type + "] is not registered");
	    }

	    // process the includes
	    includeProcessor.process(this, include);
	}	
	
	// this makes sure that the classes specified in the api are included as resources
	setApi(getApi());
    }

    public ProjectConfiguration getConfiguration() {
	return config;
    }

    /**
     * This informs the compiler about a component the user intends to use. It
     * also does not set a timer interval for the component.
     * 
     * @param name
     * @param cls
     * @throws CompilerException
     */
    public CSenseComponentC addComponent(String name, CSenseComponentC component) throws CompilerException {
	_mainGroup.addComponent(name, component);
	if ((component instanceof MatlabComponentC == false) && (component instanceof CSenseGroupC == false)) {
	    if (component.getComponent() == null) {
		_resourceManager.addClass(component.getQualifiedName());
	    } else {
		_resourceManager.addClass(component.getComponent());
	    }
	}
	return component;
    }

    protected IComponentC getComponent(String name) throws CompilerException {
	return _mainGroup.getComponent(name);
    }

    public void link(String src, String dst) throws CompilerException {
	_mainGroup.link(src, dst);
    }

    /**
     * Creates an connection between the ports of two components.
     * 
     * @param source
     * @param destination
     */
    public void link(OutputPortC source, InputPortC destination) {
	_mainGroup.connect(source, destination);
    }

    public void link(CSenseComponentC source, InputPortC inputPort)
	    throws CompilerException {
	if (source.getNumOutputs() != 1)
	    throw new CompilerException(
		    "Expected a source with a single output.");
	link(source.getOutputPort(0), inputPort);
    }

    public void link(CSenseComponentC source, CSenseComponentC destination)
	    throws CompilerException {
	if (source.getNumOutputs() != 1)
	    throw new CompilerException(
		    "Expected a source with a single output.");
	if (destination.getNumInputs() != 1)
	    throw new CompilerException(
		    "Expected a destination componet with a single input");

	OutputPortC out = source.getOutputPort(0);
	InputPortC in = destination.getInputPort(0);

	link(out, in);
    }

    public void link(OutputPortC outputPort, CSenseComponentC destination)
	    throws CompilerException {
	if (destination.getNumInputs() != 1)
	    throw new CompilerException(
		    "Expected a destination componet with a single input");
	link(outputPort, destination.getInputPort(0));
    }
    
    public void compile() throws CompilerException {
	logger.info("==================== Compilation settings ====================");	    
	logger.info("version: " + getApi());
	logger.info("message pool: " + ProjectConfiguration.messagePool.getSimpleName());
	logger.info("task queue: " + ProjectConfiguration.taskQueue.getSimpleName());
	logger.info("event queue:" +  ProjectConfiguration.eventQueue.getSimpleName());
	logger.info("collapse matlab: " + CollapseMatlabGroup.MIN_GROUP_SIZE);
	logger.info("native-convertors: " + Options.useNativeConversions);
	logger.info("profiling: " + Options.generateProfileCode + " trace file: " + Options.maxTraceFileSize);
	logger.info("==================== Compilation begins ====================");	
	targetProcessor.compile(this, target);
	_resourceManager.deployResources(target);
	logger.info("==================== Compilation ends ====================");	
    }

    public void instantiateComponents() throws CompilerException {
	for (IComponentC component : _mainGroup.getComponents()) {
	    if (component instanceof CSenseGroupC) {
		((CSenseGroupC) component).instantiate();
	    } else
		throw new CompilerException("What component?");
	}
    }

    public File getProjectDirectory() {
	return projectDirectory;
    }

    public static boolean make() {
	return _make;
    }

    public static String getPlatform() {
	Project project = getProject();
	Target target = project.getTarget();
	return target.getPlatfrom();
    }

    public Target getTarget() {
	return target;
    }
    
    public TargetProcessor getTargetProcessor() {
	return targetProcessor;
    }

    public static Project getProject() {
	return Project.project;
    }

    public static void nomake() {
	_make = false;
    }

    public void addSourceDirectory(String file) {
	_resourceManager.addSourceDirectory(new File(file));
    }

    public void addSourceDirectory(File file) {
	_resourceManager.addSourceDirectory(file);
    }

    public void nocodegen() {
	_codegen = false;
    }
    
    public void setApi(String api) throws CompilerException {
	config.setApi(api);
	_resourceManager.addClass(ProjectConfiguration.messagePool);
	_resourceManager.addClass(ProjectConfiguration.taskQueue);
    }

    public String getApi() {
	return config.getApi();
    }

    public ResourceManager getResourceManager() {
	return _resourceManager;
    }

    protected void addIncludeProcessor(String tag,
	    IncludeProcessor<? extends Include> includeProcessor) {
	includeProcessors.put(tag, includeProcessor);
    }

    public void toTap(String component, BaseTypeC type)
	    throws CompilerException {
	_mainGroup.getComponentGraph().toTap(component, type);
    }

    public ToolkitConfiguration getSdkConfig() {
	return toolkitConfiguration;
    }

    public String getName() {
	return config.getName();
    }

    public CSenseGroupC getMainGroup() {
	return _mainGroup;
    }

    public boolean isRunCodegen() {
	return _codegen;
    }

    public void build() throws CompilerException {
	build(false);
    }
    
    public void build(boolean release) throws CompilerException {
	// build to install the resulting application
	KeyTool keyTool = (KeyTool)project.getSdkConfig().getTool("keytool");
	ProjectBuilder builder = new AntBuilder(project, keyTool.getKeystore(), keyTool.getKeystorePassword(), keyTool.getAlias(), keyTool.getAliasPassword());
	builder.uninstall();
	builder.build();
	builder.install();
    }
    
    public void run() throws CompilerException {
	// run the application	
	AndroidManifest manifest = ((AndroidTargetProcessor)project.getTargetProcessor()).getManifest();
	String activity = manifest.getMainActivity().startsWith(".") ? manifest.getMainActivity() : "." + manifest.getMainActivity();
	String path = ((AndroidTool)project.getSdkConfig().getTool(AndroidTool.TOOL_NAME)).getSdkTools().getAbsolutePath();
	Shell.exec(path + "/../platform-tools/adb", "shell", "am", "start", "-n", manifest.getPackage() + "/" + activity);
    }
}