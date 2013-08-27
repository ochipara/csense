package components.conversions;


import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.components.conversions.ShortsToDoubles;


public class ShortsToFloatsC extends CSenseSourceC {
    public ShortsToFloatsC(int size, boolean useNative) throws CompilerException {
	super(ShortsToDoubles.class, TypeInfoC.newDoubleVector(size));
	addIOPort(TypeInfoC.newShortVector(size), "short");
	addOutputPort(TypeInfoC.newFloatVector(size), "floatOut");
	addArgument(new ArgumentC(useNative));
    }
}
