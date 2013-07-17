package project.resources;

import java.io.File;

import compiler.CompilerException;



import project.targets.Target;


public class JarResource extends FileResource {

    public JarResource(File localFile) throws CompilerException {
	super(localFile);
	if (localFile.getName().endsWith(".jar") == false) {
	    throw new CompilerException("Expected a jar but got [" + localFile
		    + "]");
	}
    }

    @Override
    public void deploy(Target target) throws CompilerException {
	File fn = new File(target.getLibDirectory(), localFile.getName());
	deploy(fn);

    }
}
