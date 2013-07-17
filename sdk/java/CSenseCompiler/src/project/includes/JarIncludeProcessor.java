package project.includes;

import java.io.File;

import compiler.CompilerException;
import compiler.model.Project;


import project.resources.ResourceManager;

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
