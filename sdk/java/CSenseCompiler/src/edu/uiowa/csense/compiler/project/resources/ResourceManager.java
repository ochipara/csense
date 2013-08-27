package edu.uiowa.csense.compiler.project.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.targets.Target;


public class ResourceManager {
    protected final File LIBS = new File("../Base/libs/");
    protected final List<File> sourceDirs = new ArrayList<File>();
    protected final List<Resource> resources = new LinkedList<Resource>();
    protected static Logger logger = Logger.getLogger(ResourceManager.class);

    public ResourceManager() throws CompilerException {
    }

    public void addPackage(String packageName) throws CompilerException {
	String fn = packageName.replace('.', '/');
	List<File> dirs = locateResources(fn);
	if (dirs.size() == 0) {
	    throw new CompilerException("Package " + packageName + " not found in resources");
	}

	for (File dir : dirs) {
	    for (File f : dir.listFiles()) {
		if ((f.isFile()) && (f.getName().endsWith(".java"))) {
		    String className = FilenameUtils.getBaseName(f.getName());
		    addClass(packageName + "." + className);
		}
	    }
	}	
    }

    public Resource addFile(File file) throws CompilerException {
	FileResource r = new FileResource(file);
	resources.add(r);

	return r;
    }
    
    protected List<File> locateResources(String fileName) {
	List<File> locations = new LinkedList<File>();
	for (File dir : sourceDirs) {
	    File localFile = new File(dir, fileName);

	    if (localFile.exists()) {
		locations.add(localFile);
	    }
	}

	return locations;
    }
    protected File locateResource(String fileName) throws CompilerException {
	List<File> locations = locateResources(fileName);

	if (locations.size() == 0) {
	    logger.error("Could not locate resource [" + fileName + "]: "
		    + fileName);
	    for (File dir : sourceDirs) {
		logger.error("source :" + dir);
	    }
	    throw new CompilerException("Could not locate resource ["  + fileName + "]: " + fileName);
	} else if (locations.size() > 1) {	    
	    // giving preference to toolkit locations
	    String prefix = ToolkitConfiguration.getSdkPath() +  "/sdk/java/";
	    for (File f : locations) {		
		if (f.getAbsolutePath().startsWith(prefix)) {
		    return f;
		}
	    }
	    
	    logger.error("Too many locations:");
	    for (File dir : locations) {		
		logger.error("location " + dir);
	    }
	    logger.error("Source directories:");
	    for (File dir : sourceDirs) {
		logger.error("src: " + dir);
	    }
	    throw new CompilerException("Too many locations [" + fileName + "]");
	} else {
	    return locations.get(0);
	}
    }

    public void addSourceDirectory(File dir) {
	sourceDirs.add(dir);
    }
    
    public void removeSourceDirectory(File dir) {
	sourceDirs.remove(dir);
    }

    public Resource addClass(Class<?> cls) throws CompilerException {
	String className = cls.getName();
	return addClass(className);
    }

    public Resource addClass(String className) throws CompilerException {
	String fileName = className.replace('.', '/') + ".java";
	String packageName;

	if (className.lastIndexOf('.') > 0)
	    packageName = className.substring(0, className.lastIndexOf('.'));
	else
	    packageName = "";

	File localFile = locateResource(fileName);

	ClassResource r = new ClassResource(className, packageName, localFile,
		fileName);
	resources.add(r);
	return r;
    }

    public Resource addResource(Resource resource) {
	resources.add(resource);
	return resource;
    }

    public List<Resource> getResources() {
	return resources;
    }

    public void addJar(File jar) throws CompilerException {
	resources.add(new JarResource(jar));
    }

    public void deployResources(Target target) throws CompilerException {
	for (Resource r : resources)
	    r.deploy(target);
    }
}
