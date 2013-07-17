package api;

import compiler.CompilerException;

public interface CompilerStages {
    public void instantiate() throws CompilerException;

    public void validate() throws CompilerException;
}
