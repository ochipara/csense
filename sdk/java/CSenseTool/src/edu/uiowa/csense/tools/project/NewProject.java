package edu.uiowa.csense.tools.project;

import java.util.HashMap;

import edu.uiowa.csense.tools.CSenseToolException;
import edu.uiowa.csense.tools.Command;
import edu.uiowa.csense.tools.Environment;

public class NewProject extends Command {
    public static final String COMMAND_NAME = "newproject";
    protected HashMap<String, NewJavaProject> projects = new HashMap<String, NewJavaProject>();

    public NewProject() {
	projects.put(NewJavaProject.PROJECT, new NewJavaProject());
    }

    @Override
    public void processCommand(String[] args, int argStart, Environment env) throws CSenseToolException {
	String projectType = args[argStart];
	Command project = projects.get(projectType);

	project.processCommand(args, argStart + 1, env);
    }

    @Override
    protected String getShortDescription() {
	return "newproject [java] - creates a new code project";
    }

}
