package components.conversions;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.TypeInfoC;
import components.conversions.ShortsToDoubles;


public class ShortsToFloatsC extends CSenseSourceC {
    public ShortsToFloatsC(int size, boolean useNative) throws CompilerException {
	super(ShortsToDoubles.class, TypeInfoC.newDoubleVector(size));
	addIOPort(TypeInfoC.newShortVector(size), "short");
	addOutputPort(TypeInfoC.newFloatVector(size), "floatOut");
	addArgument(new ArgumentC(useNative));
    }
}
