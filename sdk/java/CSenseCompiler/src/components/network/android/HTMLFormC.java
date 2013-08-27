package components.network.android;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.network.HTMLForm;
import edu.uiowa.csense.components.network.HTMLFormMessage;


public class HTMLFormC extends CSenseSourceC {
    public final static BaseTypeC formType = TypeInfoC.newJavaMessage(HTMLFormMessage.class);
    
    public HTMLFormC(String[] fields, String[] types) throws CompilerException {
	super(HTMLForm.class, formType);
	
	// compile-time checks
	if (fields.length != types.length)
	    throw new CompilerException(
		    "The number of fields and types must be equal");

	// create the ports
	for (int i = 0; i < fields.length; i++) {
	    if (("file".equals(types[i]) == false)
		    && ("text".equals(types[i]) == false)) {
		throw new CompilerException(
			"Expected the form type to be either text or file. However, "
				+ types[i] + " was specified");
	    }
	    addIOPort(TypeInfoC.newBaseType(), fields[i]);

	    // addInputPort(TypeInfoC.newBaseType(), fields[i] + "In");
	    // addOutputPort(TypeInfoC.newBaseType(), fields[i] + "Out");
	}
	OutputPortC form = addOutputPort(formType, "form");
	form.setSupportsPull(true);

	// add the arguments
	addArgument(new ArgumentC(fields));
	addArgument(new ArgumentC(types));
    }
}
