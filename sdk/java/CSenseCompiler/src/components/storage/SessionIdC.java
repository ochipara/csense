package components.storage;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.BaseTypeC;
import compiler.types.TypeInfoC;

public class SessionIdC extends CSenseComponentC {
    public BaseTypeC type;

    public SessionIdC() throws CompilerException {
	super(SessionId.class);
	type = TypeInfoC.newCharVector(16);
	addOutputPort(type, "out");
    }
}
