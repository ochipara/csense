package edu.uiowa.csense.tools;

import java.io.File;

import javax.xml.bind.JAXBException;

import compiler.CompilerException;



import project.ProjectConfiguration;
import project.includes.CSenseInclude;

public class Init extends Command {

    public static final String COMMAND_NAME = "init";

    @Override
    public void processCommand(String[] args, int argStart, Environment env) throws CSenseToolException {
	try {
	    String name = getArgument(args, argStart);
	    validateFinalArgument(args, argStart);
	    
	    ProjectConfiguration config = new ProjectConfiguration();
	    config.setName(name);
	    config.setApi("v2");

	    CSenseInclude csense = new CSenseInclude();	
	    csense.addSource("/Users/ochipara/Working/CSense/svn/trunk/src/Base/src");
	    csense.addSource("/Users/ochipara/Working/CSense/svn/trunk/src/baseAndroid/src");
	    config.addInclude(csense);

	    config.save(new File("project.xml"));
	} catch (CompilerException e) {
	    e.printStackTrace();
	} catch (JAXBException e) {
	    throw new CSenseToolException(e);
	}
    }
    
 
    @Override
    public String getDescription() {
	String s;
	s =   "Initializes a new csense project in the current directory\n";
	s +=  "\tusage: init [project name]";
	
	return s;
    }


    @Override
    protected String getShortDescription() {
	return "Initialize the csense project.";
    }

}
