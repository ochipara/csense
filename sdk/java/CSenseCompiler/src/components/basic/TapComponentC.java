package components.basic;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.components.basic.TapComponent;


/**
 * 
 * @author ochipara
 * 
 */
public class TapComponentC extends CSenseComponentC {
    public TapComponentC(BaseTypeC messageType) throws CompilerException {
	super(TapComponent.class);
	addInputPort(messageType, "in");
	addGenericType(messageType);
    }
}
