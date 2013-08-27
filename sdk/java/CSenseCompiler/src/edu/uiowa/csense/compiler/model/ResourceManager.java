package edu.uiowa.csense.compiler.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.utils.ExecuteCommand;



public class ResourceManager {
    protected final File LIBS = new File("../Base/libs/");
    protected final List<File> sourceDirs = new ArrayList<File>();
    // protected final List<File> resourceDirs = new ArrayList<File>();
    protected final HashMap<String, Resource> resources = new HashMap<String, Resource>();
    private static final ExecuteCommand copy = ExecuteCommand.executeCommand();
    private final Project _project;

    public ResourceManager(Project project, File baseDir)
	    throws CompilerException {
	_project = project;
	// directories where to lookup for componets to be deployed
	sourceDirs.add(new File(baseDir, "Base/src"));
	sourceDirs.add(new File(baseDir, "Base/tests"));
	sourceDirs.add(new File(baseDir, "baseAndroid/src"));
    }

    public Resource addResource(String componentName, String packageName,
	    File localFile, File deployFile) {
	Resource r = new Resource(componentName, packageName, localFile,
		deployFile);
	resources.put(componentName, r);
	return r;
    }

    public void addSourceDirectory(File dir) {
	sourceDirs.add(dir);
    }

    private int cp(File src, File dest) throws CompilerException {
	if (!src.isDirectory())
	    throw new CompilerException(src + " is not a directory");

	if (!dest.exists() && copy.execute("mkdir -p " + dest) != 0)
	    throw new CompilerException("failed to executing "
		    + copy.getLastCommand());

	String[] cmds = new String[] { "/bin/bash", "-c",
		"cp -fR " + src + "/* " + dest };
	if (copy.execute(cmds) != 0)
	    throw new CompilerException("Failed to copy resources " + src
		    + " by running " + "'" + copy.getLastCommand() + "'");

	return 0;
    }

    public Set<String> getPackages() {
	Set<String> packages = new HashSet<String>();
	for (Resource r : resources.values()) {
	    packages.add(r.packageName);
	}

	return packages;
    }
}
