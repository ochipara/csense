package edu.uiowa.csense.compiler.types;

import edu.uiowa.csense.compiler.RuntimeCompilerException;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.types.constraints.Equal;
import edu.uiowa.csense.compiler.types.constraints.MultipleOf;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.types.ByteVector;
import edu.uiowa.csense.runtime.types.CharVector;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.types.FloatVector;
import edu.uiowa.csense.runtime.types.ShortVector;

public class TypeInfoC {
    //    public static JavaTypeC newFilenameType() throws RuntimeCompilerException {
    //	return TypeInfoC.newJavaMessage(FilenameType.class);
    //    }

    /**
     * Creates a custom JavaType. 
     * @param cls
     * @return
     * @throws RuntimeCompilerException
     */
    public static JavaTypeC newJavaMessage(Class cls) throws RuntimeCompilerException {
	JavaTypeC f = new JavaTypeC(cls);
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	f.addConstraint(new Equal(Constraint.COLUMN_DIMENSION, 1));
	return f; 
    }

    public static BaseTypeC newBaseType() {
	return new JavaTypeC(Frame.class);
    }

    public static FrameTypeC newCharVector(int size) throws RuntimeCompilerException {
	FrameTypeC f = new FrameTypeC(CharVector.class);
	f.addConstraint(new MultipleOf(size));
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	return f;
    }

    public static FrameTypeC newByteVector(int size) throws RuntimeCompilerException {
	FrameTypeC f = new FrameTypeC(ByteVector.class);
	f.addConstraint(new MultipleOf(size));
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	return f;
    }


    public static FrameTypeC newDoubleVector(int size) throws RuntimeCompilerException {
	FrameTypeC f = new FrameTypeC(DoubleVector.class);
	f.addConstraint(new MultipleOf(size));
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	return f;
    }

    public static FrameTypeC newDoubleVector() {
	FrameTypeC f = new FrameTypeC(DoubleVector.class);   	
	return f;
    }

    public static FrameTypeC newFloatVector() {
	FrameTypeC f = new FrameTypeC(FloatVector.class);   	
	return f;
    }

    public static FrameTypeC newFloatVector(int size) throws RuntimeCompilerException {
	FrameTypeC f = new FrameTypeC(FloatVector.class);
	f.addConstraint(new MultipleOf(size));
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	return f;
    }

    public static FrameTypeC newShortVector(int size) throws RuntimeCompilerException {
	FrameTypeC f = new FrameTypeC(ShortVector.class);	
	f.addConstraint(new MultipleOf(size));
	f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	return f;
    }

    //    public static FrameTypeC newDoubleMatrix() throws RuntimeCompilerException {
    //	return new FrameTypeC(DoubleMatrix.class);
    //    }
    //
    //    public static FrameTypeC newShortMatrix() {
    //	return new FrameTypeC(ShortMatrix.class);
    //    }
    //
    //    public static FrameTypeC newFloatMatrix() throws RuntimeCompilerException {
    //	return new FrameTypeC(FloatMatrix.class);
    //    }
    //
    //    public static FrameTypeC newFloatMatrix(int rows, int cols) throws RuntimeCompilerException {
    //	FrameTypeC m = newFloatMatrix();
    //	m.addConstraint(new Equal(Constraint.ROW_DIMENSION, rows));
    //	m.addConstraint(new Equal(Constraint.COLUMN_DIMENSION, cols));
    //	return m;
    //    }




}
