package components.basic;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.components.basic.MemorySource;


public class MemorySourceC extends CSenseSourceC {
    public MemorySourceC(FrameTypeC portType) throws CompilerException {
	super(MemorySource.class, portType);

	addOutputPort(portType, "out").setSupportsPull(true);
	addGenericType(portType);
    }
}
