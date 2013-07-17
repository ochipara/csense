package edu.uiowa.csense.tools;

import java.util.HashMap;

public class Help extends Command {

    public static final String COMMAND_NAME = "help";

    @Override
    public void processCommand(String[] args, int argStart, Environment env) throws CSenseToolException {
    }
    
    public int processCommand(String[] args, int argStart, Environment env, HashMap<String, Command> commands) throws CSenseToolException {	
	//System.out.println(args.length + " " + argStart);
	if (args.length == argStart) {
	    validateFinalArgument(args, argStart - 1);
	    System.out.println("Available commands are:");
	    for (String name : commands.keySet()) {
		Command command = commands.get(name);
		System.out.println("\t<<" + name + ">> - " + command.getShortDescription());
	    }
	} else {
	    String commandName = getArgument(args, argStart);
	    validateFinalArgument(args, argStart);
	    
	    Command command = commands.get(commandName);
	    System.out.println(command.getDescription());
	}
  	return 0;
      }

    @Override
    protected String getShortDescription() {
	return "Use help [command_name] to obtain the help for a commnd.";
    }
}
