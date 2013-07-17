package components.basic;

import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.types.FrameTypeC;
import components.basic.MemorySource;


public class MemorySourceC extends CSenseSourceC {
    public MemorySourceC(FrameTypeC portType) throws CompilerException {
	super(MemorySource.class, portType);

	addOutputPort(portType, "out").setSupportsPull(true);
	addGenericType(portType);
    }
}
