package edu.uiowa.csense.runtime.workspace;

public class Variable {
    protected final String name;

    public Variable(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

    @Override
    public String toString() {
	return name;
    }

    public Object getValue() {
	return Workspace.getWorkspace().getValue(this);
    }
}
