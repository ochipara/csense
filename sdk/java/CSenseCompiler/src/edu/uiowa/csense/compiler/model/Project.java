package edu.uiowa.csense.compiler.model;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.*;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.AndroidIncludeProcessor;
import edu.uiowa.csense.compiler.configuration.AndroidTool;
import edu.uiowa.csense.compiler.configuration.CSenseIncludeProcessor;
import edu.uiowa.csense.compiler.configuration.Include;
import edu.uiowa.csense.compiler.configuration.IncludeProcessor;
import edu.uiowa.csense.compiler.configuration.JarIncludeProcessor;
import edu.uiowa.csense.compiler.configuration.KeyTool;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.configuration.ProjectConfiguration;
import edu.uiowa.csense.compiler.configuration.SwigTool;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.configuration.Version;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.models.android.AndroidManifest;
import edu.uiowa.csense.compiler.project.builders.AntBuilder;
import edu.uiowa.csense.compiler.project.builders.ProjectBuilder;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.compiler.targets.AndroidTarget;
import edu.uiowa.csense.compiler.targets.AndroidTargetProcessor;
import edu.uiowa.csense.compiler.targets.DesktopTarget;
import edu.uiowa.csense.compiler.targets.DesktopTargetProcessor;
import edu.uiowa.csense.compiler.targets.Target;
import edu.uiowa.csense.compiler.targets.TargetProcessor;
import edu.uiowa.csense.compiler.transformations.ToolkitDevirtualize;
import edu.uiowa.csense.compiler.transformations.collapsematlab.CollapseMatlabGroup;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.utils.Shell;


/**
 * This sets up a project and allows you to deploy the application.
 * 
 * @author Octav Chipara
 * 
 */
public class Project {
    protected File projectDirectory = null;
    protected File projectXml = null;

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

    protected ToolkitConfiguration toolkitConfiguration;
    protected ProjectConfiguration config;
    protected static boolean _codegen = true;

    // include processors
    protected HashMap<String, IncludeProcessor<? extends Include>> includeProcessors = new HashMap<String, IncludeProcessor<? extends Include>>();
    protected HashMap<String, TargetProcessor> targetProcessors = new HashMap<String, TargetProcessor>();

    private SwigTool swigTool;

    // logger
    protected static Logger logger = Logger.getLogger(Project.class);
    
    
    //options
    public static boolean REFRESH_TOOLKIT = true;
    
    public Project(File projectDir, String projectName, String targetName) throws CompilerException {
	BasicConfigurator.configure();
	logger.setLevel(Level.ALL);
	project = this;

	_resourceManager = new ResourceManager();
	projectDirectory = projectDir; 	
	projectXml = new File(projectDirectory, "project.xml");
	
	// load project components
	config = ProjectConfiguration.load(projectXml);

	// load the toolkit configuration
	toolkitConfiguration = ToolkitConfiguration.autodetectSDK();
	swigTool = (SwigTool) toolkitConfiguration.getTool("swig");

	// determine the target
	target = config.getTarget(targetName);
	if (target == null) {
	    throw new CompilerException("Cannot find target [" + targetName + "]");
	}

	// construct the mapping between includes and processors
	includeProcessors.put("csense", new CSenseIncludeProcessor());
	includeProcessors.put("android", new AndroidIncludeProcessor());
	includeProcessors.put("jar", new JarIncludeProcessor());

	// add the deployment targets
	targetProcessors.put(AndroidTarget.PLATFORM, new AndroidTargetProcessor());
	targetProcessors.put(DesktopTarget.PLATFORM, new DesktopTargetProcessor());

	include();

	//
	targetProcessor = targetProcessors.get(target.getPlatform());
	if (targetProcessor == null)
	    throw new CompilerException(
		    "No target processor registered for platform ["
			    + target.getPlatform() + "]");
		
	targetProcessor.initialize(project, target);
    }


    /**
     * Creates the various files necessary to initialize the working directory 
     * 
     * @throws CompilerException
     */
    public void initializeTarget() throws CompilerException {
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
	String api = getApi();
	String platform = getPlatform();
	Version v = toolkitConfiguration.getVersion(api, platform);
	if (v == null) {
	    throw new CompilerException("version not found");
	}
	
	logger.info("==================== Compilation settings ====================");	    
	logger.info("version: " + api + " platform: " + platform);
	logger.info("message pool: " + v.getMessagePool());
	logger.info("task queue: " + v.getTaskQueue());
	logger.info("event queue:" +  v.getEventQueue());
	logger.info("collapse matlab: " + CollapseMatlabGroup.MIN_GROUP_SIZE);
	logger.info("native-convertors: " + Options.useNativeConversions);
	logger.info("profiling: " + Options.generateProfileCode + " trace file: " + Options.maxTraceFileSize);
	logger.info("==================== Compilation begins ====================");	
	targetProcessor.compile(this, target);
	_resourceManager.deployResources(target);
	logger.info("==================== Devirtualizing toolkit ====================");		
	ToolkitDevirtualize.devirtualize(target, v);
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
	return target.getPlatform();
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