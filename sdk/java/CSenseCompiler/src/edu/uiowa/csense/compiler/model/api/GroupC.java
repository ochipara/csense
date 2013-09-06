package edu.uiowa.csense.compiler.model.api;

import java.util.Collection;

import edu.uiowa.csense.compiler.CompilerException;




public interface GroupC extends IComponentC {
    public void link(String src, String dst) throws CompilerException;

    public IComponentC getComponent(String name) throws CompilerException;

    public Collection<IComponentC> getComponents();
}
