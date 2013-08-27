package edu.uiowa.csense.compiler.targets;

import java.io.File;

import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.runtime.compatibility.desktop.DesktopEnvironment;
import edu.uiowa.csense.runtime.compatibility.desktop.DesktopLogger;

public class DesktopTargetProcessor extends CompileTargetProcessor {
    protected final Logger logger = Logger.getLogger("desktop-target");

    @Override
    public void initialize(Project project, Target target)
	    throws CompilerException {
	logger.info("Initializing target " + target.getName());
	addResources(project);

	File jniDir = target.getJniDirectory();
	if (jniDir.exists() == false) {
	    if (jniDir.mkdirs() == false) {
		throw new CompilerException("Failed to create new project");
	    }
	}

	newEclipseProject(project);
    }

    @Override
    public void compile(Project project, Target target)
	    throws CompilerException {

	super.compile(project, target);
	CSenseService.generateCSenseService(project, target);
    }

    private void addResources(Project project) throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	String api = project.getApi();

	if ("v0".equals(api)) {
	    rm.addPackage("base.v0");
	} else if ("v1".equals(api)) {
	    rm.addPackage("base.v1");
	} else if ("v2".equals(api)) {
	    rm.addPackage("base.v2");
	} else {
	    throw new CompilerException("Cannot find api implementation");
	}

	rm.addClass(DesktopEnvironment.class);
	rm.addClass(DesktopLogger.class);
    }

}
