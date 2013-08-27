package messages.fixed;

import messages.TypeInfo;

public class CSenseTypes {
    public static TypeInfo<DoubleVector> newDoubleVector(int numElements,
	    boolean direct, boolean readonly) {
	TypeInfo<DoubleVector> dv = new TypeInfo<DoubleVector>(
		DoubleVector.class, 8, numElements, 1, direct, readonly);
	return dv;
    }
}
