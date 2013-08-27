package edu.uiowa.csense.compiler.model;

import java.util.Iterator;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.utils.JavaCoder;

public class DefaultComponentCoder implements ComponentCoder {
    protected static final ComponentCoder defaultCoder = new DefaultComponentCoder();

    public static ComponentCoder getDefaultCoder() {
	return defaultCoder;
    }

    @Override
    public void genericSignature(IComponentC component, JavaCoder coder) {
	// by default there is no generic signature
	if (component.hasGenericTypes()) {
	    coder.code("<");
	    boolean first = true;
	    for (Iterator<BaseTypeC> iter = component.genericTypeIterator(); iter
		    .hasNext();) {
		BaseTypeC type = iter.next();
		if (first) {
		    coder.code(type.getSimpleName());
		    first = false;
		} else
		    coder.code(", " + type.getSimpleName());
	    }
	    coder.code(">");
	}

    }

    @Override
    public void arguments(IComponentC component, JavaCoder coder) throws CompilerException {
	boolean first = true;
	for (Iterator<ArgumentC> iter = component.arguments(); iter.hasNext();) {
	    ArgumentC arg = iter.next();

	    if (first == false) {
		coder.code(", ");
		arg.code(coder);
	    } else {		
		first = false;
		if (component instanceof CSenseSourceC) {
		    CSenseSourceC source = (CSenseSourceC) component;
		    BaseTypeC type = source.getSourcePort().getType();
		    ArgumentC newArg;
		    if (type instanceof FrameTypeC) {
			FrameTypeC ftype = (FrameTypeC) type;
			newArg = new ArgumentC(ftype);
		    } else if (type instanceof JavaTypeC) {			
			JavaTypeC jtype = (JavaTypeC) type;
			newArg = new ArgumentC(jtype);
		    } else throw new CompilerException("Invalid type");
		    newArg.code(coder);		    
		} else {
		    arg.code(coder);
		}
		
		
	    }

	    
	}
    }

    @Override
    public String argumentSignature(IComponentC component) {
	StringBuffer sb = new StringBuffer("(");

	sb.append(")");

	return sb.toString();
    }

}
