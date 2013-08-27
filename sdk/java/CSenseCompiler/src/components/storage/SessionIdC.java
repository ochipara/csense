package components.storage;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.storage.SessionId;

public class SessionIdC extends CSenseComponentC {
    public BaseTypeC type;

    public SessionIdC() throws CompilerException {
	super(SessionId.class);
	type = TypeInfoC.newCharVector(16);
	addOutputPort(type, "out");
    }
}
