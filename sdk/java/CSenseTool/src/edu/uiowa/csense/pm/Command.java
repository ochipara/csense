package edu.uiowa.csense.tools;

public abstract class Command {
    protected String name;

    public abstract void processCommand(String[] args, int argStart, Environment env)
	    throws CSenseToolException;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    protected abstract String getShortDescription();

    public String getDescription() {
	return "";
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
