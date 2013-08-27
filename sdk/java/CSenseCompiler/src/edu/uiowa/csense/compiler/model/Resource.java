package edu.uiowa.csense.compiler.model;

import java.io.File;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.utils.ExecuteCommand;



public class Resource {
    String className;
    String packageName;
    File localFile;
    File deployFile;
    static final ExecuteCommand copy = ExecuteCommand.executeCommand();

    Resource(String className, String packageName, File localFile,
	    File deployFile) {
	this.className = className;
	this.packageName = packageName;
	this.localFile = localFile;
	this.deployFile = deployFile;
    }

    public void deployResource() throws CompilerException {
	int code = copy.execute("mkdir -p " + deployFile.getParent());
	if (code != 0) {
	    throw new CompilerException("Failed to create directory"
		    + deployFile.getParent());
	}

	code = copy.execute("cp " + localFile + " " + deployFile);
	if (code != 0) {
	    throw new CompilerException("Failed to copy " + localFile
		    + " by running " + copy.getLastCommand());
	}
    }

}
