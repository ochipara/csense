package edu.uiowa.csense.compiler.model;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.utils.JavaCoder;

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
