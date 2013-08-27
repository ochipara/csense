package edu.uiowa.csense.compiler.project.resources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;







import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.targets.Target;


public class ClassResource extends FileResource {
    protected final String className;
    protected final String packageName;
    protected final String fileSuffix;

    public ClassResource(String className, String packageName, File localFile,
	    String fileSuffix) throws CompilerException {
	super(localFile);
	this.className = className;
	this.packageName = packageName;
	this.fileSuffix = fileSuffix;
    }

    @Override
    public void deploy(Target target) throws CompilerException {
	if (deployed == true)
	    return;

	File targetFile = new File(target.getSourceDirectory(), fileSuffix);
	File targetDir = targetFile.getParentFile();
	if (targetDir.exists() == false) {
	    if (targetDir.mkdirs() != true)
		throw new CompilerException("Failed to create directory ["
			+ targetFile + "]");
	}

	try {
	    if (localFile.equals(targetFile) == false) {
		FileUtils.copyFile(localFile, targetFile);
	    }
	} catch (IOException e) {
	    throw new CompilerException(e);
	}

	deployed = true;
    }

    public String getPackage() {
	return packageName;
    }
}
