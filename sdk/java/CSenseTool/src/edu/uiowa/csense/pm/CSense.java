package edu.uiowa.csense.tools;

import java.util.HashMap;

import compiler.CompilerException;



import edu.uiowa.csense.tools.project.NewProject;



public class CSense {
    protected HashMap<String, Command> commands = new HashMap<String, Command>();
    protected Environment env = new Environment();    

    public CSense() throws CompilerException {
	commands.put(NewProject.COMMAND_NAME, new NewProject());
	commands.put(Init.COMMAND_NAME, new Init());
	commands.put(Help.COMMAND_NAME, new Help());
    }
    
    private void processCommand(String[] args) throws CSenseToolException {
	Command command = commands.get(args[0]);
	if (command == null) {
	    throw new CSenseToolException("Failed to find tool for [" + args[0] + "]");
	}
	int argStart = 1;

	if (Help.COMMAND_NAME.equals(args[0])) {
	    ((Help) command).processCommand(args, argStart, env, commands);
	} else {
	    command.processCommand(args, argStart, env);
	}
    }

    public static void main(String[] args) throws CSenseToolException, CompilerException {
	for (int i = 0; i < args.length; i++) {
	    System.out.println("arg[" + i + "]=" + args[i]);
	}
	CSense tool = new CSense();
	tool.processCommand(args);
    }
}
