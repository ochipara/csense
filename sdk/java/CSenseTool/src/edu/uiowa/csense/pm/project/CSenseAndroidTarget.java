package edu.uiowa.csense.pm.project;

import java.io.File;

import javax.xml.bind.JAXBException;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.RuntimeCompilerException;
import edu.uiowa.csense.compiler.configuration.ProjectConfiguration;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.targets.AndroidTarget;
import edu.uiowa.csense.compiler.utils.ExecuteCommand;
import edu.uiowa.csense.pm.CSenseToolException;
import edu.uiowa.csense.pm.Command;

/**
 * Creates a new csense-android project
 * 
 * @author ochipara
 *
 */
public class CSenseAndroidTarget extends Command {
    public static final String NAME = "csenseandroid";
    
    protected final ExecuteCommand create = ExecuteCommand.executeCommand();

    @Override
    public void processCommand(String[] args, int argStart, ToolkitConfiguration sdk) throws CSenseToolException, CompilerException {
	if (argStart + 2 != args.length) {
	    throw new CSenseToolException("\tInvalid arguments. Excepected " + NAME + " <targetName> <packageName>");
	} 
	
	String targetName = args[argStart];
	File projectDirectory = new File(System.getProperty("user.dir"));
			
	String packageName = args[argStart + 1];
	ProjectConfiguration conf = ProjectConfiguration.defaultAndroidConfiguration(targetName, targetName, packageName, projectDirectory);
	try {
	    File f = new File(projectDirectory,  "project.xml");
	    conf.save(f);
	} catch (JAXBException e) {	    
	    e.printStackTrace();
	    throw new CSenseToolException(e);
	}
	
		
	// configure the android target	
	AndroidTarget target = new AndroidTarget(targetName, "CSenseDeployActivity", packageName, projectDirectory);
	//AndroidTargetProcessor processor = new AndroidTargetProcessor();
	
//	Project pinfo = new Project(projectDirectory, projectName, target.getName());	
//	pinfo.initializeTarget();
//	pinfo.getResourceManager().deployResources(target);
	//processor.initialize(pinfo, target);
	//processor.deployToolkit("latest", target);	
    }

    @Override
    public String shortDescription() {
	return "Creates a new CSenseAndroid target project";
    }

    @Override
    public String description() {
	return shortDescription();
    }

}
