package compiler.types.constraints;

import compiler.CompilerException;

public abstract class Variable {  
    public abstract String getName(int dim) throws CompilerException;
    public abstract int getValue(int dim);
    
    
    public abstract void setValue(int dim, int val);
    public abstract void setValue(String dimName, int val) throws CompilerException;
}
