package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;

/**
 * This is the configuration of the csense toolkit. It will include the necessary
 * components to deploy the code
 * 
 * @author ochipara
 *
 */
public class CSenseIncludeProcessor implements IncludeProcessor<CSenseInclude> {
    @Override
    
    public void process(Project project, CSenseInclude csense)
	    throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	for (File f : csense.getSources()) {
	    rm.addSourceDirectory(f);
	}
	
	String api = project.getApi();
	
	// add the local source directory
	File cdir = new File(System.getProperty("user.dir"));
	File clsdir = new File(cdir, "src");
	rm.addSourceDirectory(clsdir);

	// api packages
	rm.addPackage("edu.uiowa.csense.runtime.api");
	rm.addPackage("edu.uiowa.csense.runtime.api.concurrent");	
	rm.addPackage("edu.uiowa.csense.runtime.api.profile");
	rm.addPackage("edu.uiowa.csense.runtime.api.bindings");
	
	// implementations
	rm.addPackage("edu.uiowa.csense.runtime.types");	
	rm.addPackage("edu.uiowa.csense.runtime.compatibility");
	rm.addPackage("edu.uiowa.csense.runtime.concurrent");
	rm.addPackage("edu.uiowa.csense.runtime.workspace");
	rm.addPackage("edu.uiowa.csense.profiler");
	
	// the component packages
	rm.addPackage("edu.uiowa.csense.components.basic");
//	rm.addPackage("edu.uiowa.csense.components.audio");
//	rm.addPackage("edu.uiowa.csense.components.conversions");
//	rm.addPackage("edu.uiowa.csense.components.matlab");
//	rm.addPackage("edu.uiowa.csense.components.storage");
	
	// implementation
    }
}
