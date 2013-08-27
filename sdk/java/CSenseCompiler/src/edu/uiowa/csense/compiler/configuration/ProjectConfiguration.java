package edu.uiowa.csense.compiler.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.targets.AndroidTarget;
import edu.uiowa.csense.compiler.targets.Target;


@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfiguration {
    public ProjectConfiguration() {
    }

    public ProjectConfiguration(String name) {
	this.name = name;
    }

    public static void validateProjectDirectory(File dir) throws CompilerException {
	String[] files = dir.list();
	for (String f : files) {
	    if ("project.xml".equals(f)) {
		return;
	    }
	}
	throw new CompilerException("Could not find project.xml in directory " + dir);
    }

    public static ProjectConfiguration load(File f) throws CompilerException {
	validateProjectDirectory(f.getParentFile());
	JAXBContext context;
	try {
	    context = JAXBContext.newInstance(ProjectConfiguration.class);
	    Unmarshaller u = context.createUnmarshaller();
	    ProjectConfiguration config = (ProjectConfiguration) u.unmarshal(f);
	    config.setApi(config.api);
	    return config;
	} catch (JAXBException e) {
	    throw new CompilerException(e);
	}
    }

    public void addTarget(Target target) {
	targets.add(target);
    }

    public void addInclude(Include include) {
	includes.add(include);
    }

    public void print() throws JAXBException {
	JAXBContext context = JAXBContext.newInstance(ProjectConfiguration.class);
	Marshaller m = context.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	m.marshal(this, System.out);
    }

    public void save(File f) throws JAXBException {
	JAXBContext context = JAXBContext.newInstance(ProjectConfiguration.class);
	Marshaller m = context.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	m.marshal(this, f);
    }

    @XmlAttribute(required = true)
    protected String name;

    @XmlElementWrapper(name = "includes", required = true)
    @XmlElement(name = "include")
    List<Include> includes = new LinkedList<Include>();

    @XmlElementWrapper(name = "targets", required = true)
    @XmlElement(name = "target")
    List<Target> targets = new LinkedList<Target>();

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @XmlAttribute(required = true)
    String api;

    public String getApi() {
	return api;
    }

    public void setApi(String api) throws CompilerException {
	this.api = api;
    }

    public List<Include> getIncludes() {
	return includes;
    }

    public List<Target> getTargets() {
	return targets;
    }

    public Target getTarget(String targetName) {
	for (Target target : targets) {
	    if (targetName.equals(target.getName())) {
		return target;
	    }
	}
	return null;
    }

    /**
     * Automatically configures the android project configuration
     * 
     * @param projectName
     * @param targetName
     * @param packageName
     * @param projectDir
     * @return
     * @throws CompilerException
     */
    public static ProjectConfiguration defaultAndroidConfiguration(String projectName, 
	    String targetName, String packageName, File projectDir) throws CompilerException {
	ProjectConfiguration proj = new ProjectConfiguration(projectName);
	proj.setApi("v4");

	File sdkPath = ToolkitConfiguration.getSdkPath();
	
	CSenseInclude csense = new CSenseInclude();
	csense.addSource(new File(sdkPath, "sdk/java/Base/src/"));
	csense.addSource(new File(sdkPath, "sdk/java/baseAndroid/src/"));
	proj.addInclude(csense);

	JarInclude jars = new JarInclude();
	jars.addJar(new File(sdkPath, "lib/guava-13.0.1.jar"));
	jars.addJar(new File(sdkPath, "lib/apache-mime4j-core-0.7.2.jar"));
	jars.addJar(new File(sdkPath, "lib/httpclient-4.2.1.jar"));
	jars.addJar(new File(sdkPath, "lib//httpcore-4.2.2.jar"));
	jars.addJar(new File(sdkPath, "lib/httpmime-4.2.1.jar"));
	jars.addJar(new File(sdkPath, "lib/jmatio.jar"));
	proj.addInclude(jars);

	AndroidTarget target = new AndroidTarget(targetName, "CSenseDeployActivity", packageName, projectDir);
	proj.addTarget(target);

	return proj;
    }

    public static void main(String[] args) throws JAXBException,
    CompilerException {
	ProjectConfiguration proj = new ProjectConfiguration("AudiologyApp");

	CSenseInclude csense = new CSenseInclude();
	csense.addSource("/Users/ochipara/Working/CSense/svn/trunk/src/Base/src/");
	csense.addSource("/Users/ochipara/Working/CSense/svn/trunk/src/baseAndroid/src/");
	csense.addSource("/Users/ochipara/Working/CSense/svn/trunk/csense/systems/Audiology/code/java/AudiologyDeploy/src/");
	proj.addInclude(csense);

	AndroidInclude include = new AndroidInclude("code/java/AudiologyUI");
	proj.addInclude(include);

	JarInclude jars = new JarInclude();
	jars.addJar("/Users/ochipara/Working/lib/java/guava-13.0.1.jar");
	jars.addJar("/Users/ochipara/Working/lib/java/android-networking/apache-mime4j-core-0.7.2.jar");
	jars.addJar("/Users/ochipara/Working/lib/java/android-networking/httpclient-4.2.1.jar");
	jars.addJar("/Users/ochipara/Working/lib/java/android-networking/httpcore-4.2.2.jar");
	jars.addJar("/Users/ochipara/Working/lib/java/android-networking/httpmime-4.2.1.jar");
	proj.addInclude(jars);

	AndroidTarget target = new AndroidTarget("audiology-android","AudiologyUI", "edu.uiowa.csense.audiology", new File("gen/AudiologyApp"));
	proj.addTarget(target);
	proj.print();
    }


}
