package api;

import java.util.Collection;

import compiler.CompilerException;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;



public interface IComponentGraph {
    public void link(String src, String dst) throws CompilerException;

    public void link(OutputPortC out, InputPortC in)
	    throws CompilerException;

    public void addComponent(String variableName, IComponentC component)
	    throws CompilerException;

    public Collection<IComponentC> components();
}
