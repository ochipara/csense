package compiler.model;

import compiler.CompilerException;
import compiler.utils.JavaCoder;

import api.IComponentC;

public interface ComponentCoder {
    /**
     * This generates the generic type signature that will be used during the
     * instantiation of a component
     * 
     * @param component
     * @param coder
     */
    public void genericSignature(IComponentC component, JavaCoder coder);

    /**
     * This generates the set of arguments that will be used during the
     * instation of the component
     * 
     * @param component
     * @param coder
     * @throws CompilerException 
     */
    public void arguments(IComponentC component, JavaCoder coder) throws CompilerException;

    public String argumentSignature(IComponentC component);
}
