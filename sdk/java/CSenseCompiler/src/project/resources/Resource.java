package project.resources;

import compiler.CompilerException;

import project.targets.Target;

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
