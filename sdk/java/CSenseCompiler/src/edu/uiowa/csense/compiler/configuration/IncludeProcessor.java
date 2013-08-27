package edu.uiowa.csense.compiler.configuration;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;

public interface IncludeProcessor<T extends Include> {
    public void process(Project project, T include) throws CompilerException;
}
