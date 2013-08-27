package edu.uiowa.csense.compiler.project.resources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;







import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.targets.Target;

public class FileResource extends Resource {
    protected final File localFile;

    // if these variables are set, then the file will be deployed to
    // cp baseDir/suffix => target/suffix
    protected File dir = null;
    protected String suffix = null;

    public FileResource(File localFile) throws CompilerException {
	super();
	if (localFile.exists() == false)
	    throw new CompilerException("Cannot locate resource [" + localFile
		    + "]");
	this.localFile = localFile;
	// this.targetFile = targetFile;
    }

    public FileResource(File directory, String name) throws CompilerException {
	this(new File(directory, name));
	dir = directory;
	suffix = name;
    }

    public void deploy(File targetFile) throws CompilerException {
	File dir = targetFile.getParentFile();
	if (dir.exists() == false) {
	    if (dir.mkdirs() == false) {
		throw new CompilerException("Failed to deploy [" + localFile
			+ "] to [" + targetFile + "]");
	    }
	}

	try {
	    FileUtils.copyFile(localFile, targetFile);
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
	deployed = true;
    }

    @Override
    public void deploy(Target target) throws CompilerException {
	if (deployed == true)
	    return;
	if ((suffix == null) || (dir == null)) {
	    throw new CompilerException("Cannot deploy file [" + localFile
		    + "]");
	}

	try {
	    File dir = new File(target.getDirectory(), suffix);
	    // create the directory if it exists

	    if (localFile.isDirectory()) {
		// assume that the target has to be a directory as well
		if (localFile.getName().equals(dir.getName())) {
		    FileUtils.copyDirectoryToDirectory(localFile,
			    dir.getParentFile());
		} else {
		    FileUtils.copyDirectoryToDirectory(localFile, dir);
		}
	    } else {
		// assume that the target has to be a file
		if (dir.getParentFile().mkdirs())
		    throw new CompilerException("Failed to create directory ["
			    + dir + "]");
		FileUtils.copyFile(localFile, dir);
	    }
	} catch (IOException e) {
	    throw new CompilerException(e);
	}

	deployed = true;
    }

    public File getLocalFile() {
	return localFile;
    }

}
