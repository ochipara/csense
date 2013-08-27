package edu.uiowa.csense.compiler.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import edu.uiowa.csense.compiler.CompilerException;

/**
 * The class manages the configuration of the CSense toolkit.
 * You can add a series of tools to the configuration of the toolkit.
 * Currently supported tools includes:
 * 	- android-tools
 * 	- swig
 * 	- lp-solve
 * 	- csense compiler
 * 	- keytool
 * 
 * These tools are used to create the CSense project
 * 
 * @author ochipara
 *
 */

@XmlRootElement(name = "csense")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolkitConfiguration {
    public static final String CSENSE_SDK_ENV = "CSENSE_SDK";
    @XmlElementWrapper
    @XmlElement(name="tool")
    List<Tool> tools = new LinkedList<Tool>();
    
    @XmlElementWrapper
    @XmlElement(name="version")
    List<Version> versions = new LinkedList<Version>();
    
    private void addTool(Tool tool) {
	tools.add(tool);
    }
    
    private void addVersion(Version version) {
	versions.add(version);
    }

    public static void print(ToolkitConfiguration config) throws JAXBException {
	JAXBContext context = JAXBContext
		.newInstance(ToolkitConfiguration.class);
	Marshaller m = context.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	m.marshal(config, System.out);
    }

    public static ToolkitConfiguration loadConfiguration(File file)
	    throws CompilerException {
	JAXBContext context;
	try {
	    context = JAXBContext.newInstance(ToolkitConfiguration.class);
	    Unmarshaller u = context.createUnmarshaller();
	    ToolkitConfiguration config = (ToolkitConfiguration) u.unmarshal(file);
	    return config;
	} catch (JAXBException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    /**
     * Automatically configures the toolkit. 
     * We will try to load the configuration file 
     * 	  - from the current project directory
     * 	  - from the toolkit configuration
     * 
     * @return the toolkit configuration
     * @throws CompilerException 
     */
    public static ToolkitConfiguration autodetectSDK(){
	File sdkDir = getSdkPath();
	String conf = "conf/csense.xml";
	
	File confFile = new File(sdkDir, conf);
	try {	    
	    ToolkitConfiguration sdkConfiguration = ToolkitConfiguration.loadConfiguration(confFile);
	    return sdkConfiguration;
	} catch (CompilerException e){
	    System.err.println("Failed to load toolkit configuration " + confFile);
	    return null;
	}		
    }
    
    public static File autodetectProject() throws CompilerException {
	File cwd = new File(System.getProperty("user.dir"));
	File d = cwd.getAbsoluteFile();
	while (d.isDirectory()) {
	    File[] files= d.listFiles();
	    for (File f : files) {
		if (f.getName() == "csense.xml") {
		    return d;
		}
	    }
	    d = d.getParentFile();
	}
	
	throw new CompilerException("Failed to autodetect directory starting from " + cwd);
    }

    public static File getSdkPath() {
	String val = System.getenv(CSENSE_SDK_ENV);
	if (val == null) {
	    throw new RuntimeException("SKD not configurated properly. "
	    	+ "You must set the CSENSE_SDK in your environment");
	}
	File dir = new File(val);
	if (dir.exists() == false) {
	    throw new RuntimeException("SKD not configurated properly. "
		    	+ "CSENSE_SDK is set in your environment but it does not exist");	    
	}
	
	if (dir.isDirectory() == false) {
	    throw new RuntimeException("SKD not configurated properly. "
		    	+ "CSENSE_SDK is set in your environment but is not a directory");
	}
	
	return dir;
    }

    public static void main(String[] args) throws JAXBException {
	ToolkitConfiguration sdk = new ToolkitConfiguration();

	AndroidTool androidTool = new AndroidTool();
	androidTool.ndkBuild = new File(
		"/Users/ochipara/android-sdks/android-ndk/ndk-build");
	androidTool.sdkTools = new File("/Users/ochipara/android-sdks/tools");
	androidTool.setTarget("android-15");
	androidTool.rusage = new File(ToolkitConfiguration.getSdkPath(), "sdk/java/baseAndroid/jni/getrusage");
	sdk.addTool(androidTool);

	SwigTool swigTool = new SwigTool();
	swigTool.swig = new File("/opt/local/bin/swig");
	sdk.addTool(swigTool);
		
	CompilerTool compilerTool = new CompilerTool();
	compilerTool.setDirectory(new File(ToolkitConfiguration.getSdkPath(), "sdk/java/CSenseCompiler/"));
	sdk.addTool(compilerTool);
	
	LpSolveTool lpTool = new LpSolveTool();
	lpTool.lpsolve = new File("/Users/ochipara/Working/tools/lp_solve/lp_solve");
	sdk.addTool(lpTool);

	Version v4 = new Version();
	v4.version = "v4";
	v4.platform = "android";
	v4.addPackage("edu.uiowa.csense.runtime.v4");
	
	v4.component = "edu.uiowa.csense.runtime.v4.CSenseComponent";
	v4.source = "edu.uiowa.csense.runtime.v4.CSenseSource";
	v4.messagePool = "edu.uiowa.csense.runtime.v4.MessagePoolAtomic";
	v4.scheduler = "edu.uiowa.csense.runtime.v4.CSenseScheduler";
	v4.taskQueue = "edu.uiowa.csense.runtime.v4.CBQTaskManager";
	v4.eventQueue = "edu.uiowa.csense.runtime.v4.CPQTimerEventManager";
	v4.idleLock = "edu.uiowa.csense.runtime.v4.AndroidIdleLock";
	
	sdk.addVersion(v4);
	
	ToolkitConfiguration.print(sdk);
    }

    public Tool getTool(String name) {
	for (Tool tool : tools) {
	    if (name.equals(tool.getName()))
		return tool;
	}
	return null;
    }

    /**
     * Returns the configuration for the (version, target) tuple
     * @param version
     * @param platform
     * @return
     */
    public Version getVersion(String version, String platform) {
	for (Version v : versions) {
	    if (v.getVersion().equals(version) && v.getPlatform().equals(platform)) {
		return v;
	    }
	}
	
	return null;
    }


}
