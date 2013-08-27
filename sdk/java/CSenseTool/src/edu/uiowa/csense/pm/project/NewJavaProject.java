package edu.uiowa.csense.pm.project;

import java.io.File;
import java.io.IOException;

import edu.uiowa.csense.compiler.configuration.EclipseUtils;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.pm.CSenseToolException;
import edu.uiowa.csense.pm.Command;

public class NewJavaProject extends Command {
    public static final String PROJECT = "eclipse";

    @Override
    public void processCommand(String[] args, int argStart, ToolkitConfiguration sdk) throws CSenseToolException {
	if (argStart == args.length) {
	    System.err.println("Specify the name of the eclipse project");
	    return;
	}
	
	String projectName = args[argStart];	
	File directory = new File(projectName);
	
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
    public final String shortDescription() {
	return "create eclipse project";
    }


    @Override
    public final String description() {
	return "creates a new eclipse project";
    }

}
