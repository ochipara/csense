package edu.uiowa.csense.compiler.model.api;

import java.util.Collection;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;



public interface IComponentGraph {
    public void link(String src, String dst) throws CompilerException;

    public void link(OutputPortC out, InputPortC in)
	    throws CompilerException;

    public void addComponent(String variableName, IComponentC component)
	    throws CompilerException;

    public Collection<IComponentC> components();
}
