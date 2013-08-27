package edu.uiowa.csense.compiler;

import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;

public class CSenseSourceC extends CSenseComponentC {
    protected OutputPortC output = null;
    
    public CSenseSourceC(FrameTypeC type) {
	super();
	addArgument(new ArgumentC(type));
    }
    
    /**
     * Android source components should call this super constructor to avoid dependency on the Android library.
     * @param csenseComponent qualified component name
     * @param type message type
     */
    public CSenseSourceC(String csenseComponent, BaseTypeC type) {
	super(csenseComponent);
	if (type instanceof FrameTypeC) {
	    addArgument(new ArgumentC((FrameTypeC)type));
	} else if (type instanceof JavaTypeC) {
	    addArgument(new ArgumentC((JavaTypeC) type));
	} else throw new RuntimeCompilerException("Invalid argument");
    }
    
    public CSenseSourceC(Class csenseComponent, BaseTypeC type) {
	super(csenseComponent);
	if (type instanceof FrameTypeC) {
	    addArgument(new ArgumentC((FrameTypeC)type));
	} else if (type instanceof JavaTypeC) {
	    addArgument(new ArgumentC((JavaTypeC) type));
	} else throw new RuntimeCompilerException("Invalid argument");    }

    @Override
    public OutputPortC addOutputPort(BaseTypeC portType, String name) throws CompilerException {
	if (output != null) {
	    if (output.getInternalInput() == null)  throw new CompilerException("A source can have a single pool");
	}
	output = new OutputPortC(this, portType, name);
	output.setSource(true);
	return addOutputPort(output);
    }

    public OutputPortC getSourcePort() {
	return output;
    }
}
