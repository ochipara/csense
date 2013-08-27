package edu.uiowa.csense.pm;

import java.util.HashMap;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.pm.project.NewProject;
import edu.uiowa.csense.pm.project.NewTarget;



public class CSense {
    protected HashMap<String, Command> commands = new HashMap<String, Command>();
    protected ToolkitConfiguration env = null;
    public static boolean debug = false; 

    public CSense() throws CompilerException {
	env = ToolkitConfiguration.autodetectSDK();
	if (env == null) {
	    System.err.println("Failed to load toolkit configuration");
	    System.err.println("Make sure to setup the CSENSE_SDK environment variable");
	    System.exit(-1);
	}

	commands.put(NewProject.COMMAND_NAME, new NewProject());
	commands.put(Init.COMMAND_NAME, new Init());
	commands.put(NewTarget.COMMAND_NAME, new NewTarget());
	//commands.put(Help.COMMAND_NAME, new Help());
    }

    private void processCommand(String[] args) throws CSenseToolException, CompilerException {
	int startIndex = 0;
	for (startIndex = 0; startIndex< args.length; startIndex++) {
	    String arg = args[startIndex];
	    if (arg.startsWith("-")) {
		if ("-d".equals(arg)) {
		    debug = true;
		} else {
		    throw new CSenseToolException("Unrecognized option");
		}
	    } else break;
	}

	if (args.length == startIndex) {
	    System.out.println("Available commands are:");
	    for (String command : commands.keySet()) {
		Command cmd = commands.get(command);
		System.out.println("\t" + command + " - " + cmd.shortDescription());
	    }
	    return;
	}

	Command command = commands.get(args[startIndex]);
	if (command == null) {
	    throw new CSenseToolException("Failed to find tool for [" + args[startIndex] + "]");
	}
	command.processCommand(args, startIndex + 1, env);
    }

    public static void main(String[] args) throws CSenseToolException, CompilerException {
	System.out.println("csense-tool v:1.0");
	for (int i = 0; i < args.length; i++) {	    
	    System.out.println("arg[" + i + "]=" + args[i]);
	}
	System.out.println("=============================================");
	CSense tool = new CSense();
	
	try {
	    tool.processCommand(args);
	} catch (Exception e) {
	    System.err.println("ERROR: " + e.getMessage());
	    if (debug) e.printStackTrace(System.out);
	}
    }
}
