package edu.uiowa.csense.pm;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;


/**
 * This class provides an abstract description of a command 
 * 
 * @author ochipara
 *
 */
public abstract class Command {
    protected String name;

    public abstract void processCommand(String[] args, int argStart, ToolkitConfiguration sdk) throws CSenseToolException, CompilerException;       
    public abstract String shortDescription();
    public abstract String description();
    
    
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
    
    protected String getArgument(String[] args, int argStart) throws CSenseToolException {
	try {
	    String arg = args[argStart];
	    return arg;
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new CSenseToolException("Invalid number of arguments");
	}
    }
    
    protected void validateFinalArgument(String[] args, int argStart) throws CSenseToolException {
	if (args.length != argStart + 1) {
	    throw new CSenseToolException("Invalid number of arguments");
	}
    }
  
    
    

}
