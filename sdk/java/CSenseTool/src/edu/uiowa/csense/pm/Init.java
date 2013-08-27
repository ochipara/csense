package edu.uiowa.csense.pm;

import java.io.File;

import javax.xml.bind.JAXBException;








import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.CSenseInclude;
import edu.uiowa.csense.compiler.configuration.ProjectConfiguration;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;

public class Init extends Command {

    public static final String COMMAND_NAME = "init";

    @Override
    public void processCommand(String[] args, int argStart, ToolkitConfiguration env) throws CSenseToolException {
	try {
	    String name = getArgument(args, argStart);
	    validateFinalArgument(args, argStart);
	    
	    File projectDir = new File(name);
	    if (projectDir.exists() && projectDir.isDirectory()) {
		System.out.println();
		System.out.println("Project already created... Recreating configuration!");
		//return;
	    } else {
		projectDir.mkdir();
		new File(projectDir, "code").mkdirs();
		new File(projectDir, "docs").mkdirs();
		new File(projectDir, "gen").mkdirs();
	    }
	    
	    ProjectConfiguration config = new ProjectConfiguration();
	    config.setName(name);
	    config.setApi("v2");
	    
//	    File sdkPath = ToolkitConfiguration.getSdkPath();
//	    CSenseInclude csense = new CSenseInclude();	
//	    csense.addSource(new File(sdkPath, "sdk/java/Base/src"));
//	    csense.addSource(new File(sdkPath, "sdk/java/baseAndroid/src"));
//	    config.addInclude(csense);
//
//	    config.save(new File(projectDir, "project.xml"));
	} catch (CompilerException e) {
	    e.printStackTrace();
	} 
//	catch (JAXBException e) {
//	    throw new CSenseToolException(e);
//	}
    }
   
    @Override
    public String description() {
	String s;
	s =   "Initializes a new csense project in the current directory\n";
	s +=  "\tusage: init [project name]";
	
	return s;
    }


    @Override
    public String shortDescription() {
	return "Initialize the csense project.";
    }
}
