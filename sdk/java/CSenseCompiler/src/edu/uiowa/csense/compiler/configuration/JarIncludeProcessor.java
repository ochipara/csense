package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;

public class JarIncludeProcessor implements IncludeProcessor<JarInclude> {

    @Override
    public void process(Project project, JarInclude include)
	    throws CompilerException {
	ResourceManager rm = project.getResourceManager();

	for (File jar : include.getJars()) {
	    rm.addJar(jar);
	}
    }

}
