package edu.uiowa.csense.compiler.project.resources;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.targets.Target;

public abstract class Resource {
    protected boolean deployed = false;

    public abstract void deploy(Target target) throws CompilerException;

    public boolean isDeployed() {
	return deployed;
    }

    public void setDeployed(boolean deployed) {
	this.deployed = deployed;
    }
}
