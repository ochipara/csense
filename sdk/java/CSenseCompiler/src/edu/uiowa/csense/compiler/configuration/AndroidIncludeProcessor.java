package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.project.resources.FileResource;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;


public class AndroidIncludeProcessor implements
	IncludeProcessor<AndroidInclude> {

    @Override
    public void process(Project project, AndroidInclude androidInclude)
	    throws CompilerException {
	ResourceManager rm = project.getResourceManager();
	File src = androidInclude.getSourceDirectory();

	project.getResourceManager().addSourceDirectory(src);

	File directory = androidInclude.getDirectory();
	if (directory.exists() == false) {
	    throw new CompilerException("Could not locate directory [" + directory + "]");
	}

	// exclude gen and bin
	String[] files = directory.list();
	for (int i = 0; i < files.length; i++) {
	    String file = files[i];
	    if ((file.equals("bin") == false) && (file.equals("gen") == false)
		    && (file.equals("libs") == false)) {
		File fn = new File(directory, file);
		if (fn.isDirectory()) {
		    FileResource fres = new FileResource(directory, fn.getName());
		    rm.addResource(fres);
		}
	    }
	}
    }
}
