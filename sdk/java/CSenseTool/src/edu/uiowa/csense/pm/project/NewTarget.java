package edu.uiowa.csense.pm.project;

import java.util.HashMap;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.pm.CSenseToolException;
import edu.uiowa.csense.pm.Command;

public class NewTarget extends Command {
    public static final String COMMAND_NAME = "newtarget";
    protected HashMap<String, Command> targets = new HashMap<String, Command>();

    public NewTarget() {
	targets.put(CSenseAndroidTarget.NAME, new CSenseAndroidTarget());
    }

    @Override
    public void processCommand(String[] args, int argStart, ToolkitConfiguration env) throws CSenseToolException, CompilerException {
	if (argStart == args.length) {
	    System.out.println("The following types of projects may be created:");
	    for (String projectType : targets.keySet()) {
		System.out.println("\t" + projectType + " - " + targets.get(projectType).shortDescription());
	    }
	    return;
	}
	String projectType = args[argStart];	
	Command project = targets.get(projectType);

	project.processCommand(args, argStart + 1, env);
    }


    @Override
    public String description() {
	return "creates a new target";
    }

    @Override
    public String shortDescription() {
	return "creates a new target";
    }

}
