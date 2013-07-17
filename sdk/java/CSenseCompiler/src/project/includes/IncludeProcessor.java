package project.includes;


import compiler.CompilerException;
import compiler.model.Project;

public interface IncludeProcessor<T extends Include> {
    public void process(Project project, T include) throws CompilerException;
}
