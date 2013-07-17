package project;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import base.concurrent.ABQTaskManager;
import base.concurrent.APQTimerEventManager;
import base.concurrent.CBQTaskManager;
import base.concurrent.CPQTimerEventManager;
import base.v2.MessagePoolAtomic;
import base.v2.MessagePoolBlockingQueue;

import compiler.CompilerException;
import project.includes.AndroidInclude;
import project.includes.CSenseInclude;
import project.includes.Include;
import project.includes.JarInclude;
import project.targets.Target;
import project.targets.android.AndroidTarget;


@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfiguration {
    // internal components
    public static Class<?> messagePool = null;
    public static Class<?> taskQueue = null;
    public static Class<?> eventQueue = null;

    public ProjectConfiguration() {
    }

    public ProjectConfiguration(String name) {
	this.name = name;
    }
    public static ProjectConfiguration load(File f) throws CompilerException {
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
	JAXBContext context = JAXBContext .newInstance(ProjectConfiguration.class);
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
	
	if ("v2".equals(api)) {
	    messagePool = MessagePoolBlockingQueue.class;
	    taskQueue = ABQTaskManager.class;	
	    eventQueue = APQTimerEventManager.class;
	} else if ("v3".equals(api)) {
	    messagePool = MessagePoolAtomic.class;	    
	    taskQueue = CBQTaskManager.class;
	    eventQueue = CPQTimerEventManager.class;
	} else {
	    throw new IllegalArgumentException("Unknown api version " + api);
	}		
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

	AndroidTarget target = new AndroidTarget("audiology-android",
		"AudiologyUI", "edu.uiowa.csense.audiology", "gen/AudiologyApp");
	proj.addTarget(target);
	proj.print();
    }

    
}
