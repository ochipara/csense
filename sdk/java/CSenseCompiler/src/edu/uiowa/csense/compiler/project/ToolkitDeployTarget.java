package edu.uiowa.csense.compiler.project;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.compiler.targets.AndroidTarget;
import edu.uiowa.csense.compiler.targets.Target;
import edu.uiowa.csense.compiler.targets.TargetProcessor;

/**
 * Deploys and customizes the current version of the toolkit
 * 
 * 
 * @author ochipara
 *
 */
public class ToolkitDeployTarget extends TargetProcessor {

    @Override
    public void initialize(Project project, Target target) throws CompilerException {
	String api = project.getApi();
	boolean android = target instanceof AndroidTarget;
	
	if ("v4".equals(api)) {
	    deploy_v4(project, target);	    
	    
	} else {
	    throw new CompilerException("Unknown version");
	}
    }

    private void deploy_v4(Project project, Target target) throws CompilerException {	
	ResourceManager rm = project.getResourceManager();
	rm.addPackage("edu.uiowa.csense.runtime.v4");
	rm.deployResources(target);
    }

    @Override
    public void compile(Project project, Target target) throws CompilerException {	
    }
    
}
