package edu.uiowa.csense.pm.project;

import java.util.HashMap;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.pm.CSenseToolException;
import edu.uiowa.csense.pm.Command;

public class NewProject extends Command {
    public static final String COMMAND_NAME = "newproject";
    protected HashMap<String, Command> projects = new HashMap<String, Command>();

    public NewProject() {
	projects.put(NewJavaProject.PROJECT, new NewJavaProject());
	projects.put(CSenseAndroidTarget.NAME, new CSenseAndroidTarget());
    }

    @Override
    public void processCommand(String[] args, int argStart, ToolkitConfiguration env) throws CSenseToolException, CompilerException {
	if (argStart == args.length) {
	    System.out.println("The following types of projects may be created:");
	    for (String projectType : projects.keySet()) {
		System.out.println("\t" + projectType + " - " + projects.get(projectType).shortDescription());
	    }
	    return;
	}
	String projectType = args[argStart];	
	Command project = projects.get(projectType);

	project.processCommand(args, argStart + 1, env);
    }


    @Override
    public String description() {
	return "creates a new code project";
    }

    @Override
    public String shortDescription() {
	return "creates a new code project";
    }

}
