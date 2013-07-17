package components.basic;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.BaseTypeC;
import components.basic.TapComponent;


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
