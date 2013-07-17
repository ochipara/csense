package edu.uiowa.csense.tools.project;

import java.io.File;
import java.io.IOException;

import edu.uiowa.csense.tools.CSenseToolException;
import edu.uiowa.csense.tools.Command;
import edu.uiowa.csense.tools.Environment;

import project.targets.EclipseUtils;

public class NewJavaProject extends Command {
    public static final String PROJECT = "java";

    @Override
    public void processCommand(String[] args, int argStart, Environment env) throws CSenseToolException {
	String projectName = args[argStart];	
	File directory = new File("code/java/" + projectName);
	
	System.out.println("Creating project " + projectName + " in " + directory);
	if (args.length != argStart + 1) {
	    throw new CSenseToolException("Failed to process command");
	}
	try {
	    createDirectory(directory);
	    createDirectory(new File(directory, "src"));
	    createDirectory(new File(directory, "bin"));
	    
	    EclipseUtils.newEclipseProject(projectName, directory);
	} catch (IOException e) {
	    throw new CSenseToolException(e);
	}
    }

    public void createDirectory(File dir) throws CSenseToolException {
	if (dir.exists() == false) {
	    if (dir.mkdirs() == false) throw new CSenseToolException("Failed to create directory");
	}
    }

    @Override
    protected String getShortDescription() {
	return null;
    }

}
