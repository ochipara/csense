package edu.uiowa.csense.compiler.targets;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;

public abstract class TargetProcessor {
    public TargetProcessor() {
    }

    /**
     * Called during the initialization process
     * 
     * @param project
     * @throws CompilerException
     */
    public abstract void initialize(Project project, Target target)
	    throws CompilerException;

    /**
     * Called to compile the target. This happens after initialize
     * 
     * @param project
     * @throws CompilerException
     */
    public abstract void compile(Project project, Target target)
	    throws CompilerException;
}
