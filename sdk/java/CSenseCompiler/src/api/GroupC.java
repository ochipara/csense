package api;

import java.util.Collection;

import compiler.CompilerException;




public interface GroupC extends IComponentC, CompilerStages {
    public void link(String src, String dst) throws CompilerException;

    public IComponentC getComponent(String name) throws CompilerException;

    public Collection<IComponentC> getComponents();

}
