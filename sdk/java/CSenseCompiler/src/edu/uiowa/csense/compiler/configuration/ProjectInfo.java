package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;

/**
 * Contains the project information. 
 * - name 		- the name of the project
 * - dir 		- the directory of the project (must contain project.xml) 
 * 
 * 
 * @author ochipara
 *
 */
public class ProjectInfo {
    protected final String name;
    protected final File dir;
    protected final File projectXml;
    protected final ToolkitConfiguration sdkConfig;
    
    public ProjectInfo(String name, File dir) throws CompilerException {
	this.name = name;
	this.dir = dir;
	this.projectXml = new File(dir, "project.xml");
	
	// validate the existance of project.xml
	if (projectXml.exists() == false) {
	    throw new CompilerException("Failed to locate project.xml in project directory " + dir);
	}
	
	if (projectXml.isFile() == false) {
	    throw new CompilerException("Expected project.xml to be a file in project directory " + dir);
	}
	
	sdkConfig = ToolkitConfiguration.autodetectSDK();
    }

    public ToolkitConfiguration getSdkConfig() {
	return sdkConfig;
    }

    public String getName() {
	return name;
    }
}
