package components.conversions;


import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.TypeInfoC;
import components.conversions.ShortsToDoubles;


public class ShortsToDoublesC extends CSenseSourceC {
    public ShortsToDoublesC(int size, boolean useNative) throws CompilerException {
	super(ShortsToDoubles.class, TypeInfoC.newDoubleVector(size));
	addIOPort(TypeInfoC.newShortVector(size), "short");
	addOutputPort(TypeInfoC.newDoubleVector(size), "doubleOut");
	addArgument(new ArgumentC(useNative));
    }
}
