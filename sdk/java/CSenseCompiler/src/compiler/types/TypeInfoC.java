package compiler.types;

import compiler.CompilerException;
import compiler.RuntimeCompilerException;
import compiler.model.Project;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Equal;
import compiler.types.constraints.MultipleOf;
import messages.fixed.ByteVector;
import messages.fixed.CharVector;
import messages.fixed.DoubleMatrix;
import messages.fixed.DoubleVector;
import messages.fixed.FilenameType;
import messages.fixed.FloatMatrix;
import messages.fixed.FloatVector;
import messages.fixed.ShortMatrix;
import messages.fixed.ShortVector;
import api.Message;

public class TypeInfoC {
    public static JavaTypeC newFilenameType() throws RuntimeCompilerException {
	return TypeInfoC.newJavaMessage(FilenameType.class);
    }

    public static JavaTypeC newJavaMessage(Class<? extends Message> cls) throws RuntimeCompilerException {
	try {
	    Project.getProject().getResourceManager().addClass(cls);
	    JavaTypeC f = new JavaTypeC(cls);
	    f.addConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
	    f.addConstraint(new Equal(Constraint.COLUMN_DIMENSION, 1));
	    return f; 
	} catch (CompilerException e) {
	    throw new RuntimeCompilerException(e);
	}
    }

    public static BaseTypeC newBaseType() {
	return new JavaTypeC(Message.class);
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

    public static FrameTypeC newDoubleMatrix() throws RuntimeCompilerException {
	return new FrameTypeC(DoubleMatrix.class);
    }

    public static FrameTypeC newShortMatrix() {
	return new FrameTypeC(ShortMatrix.class);
    }

    public static FrameTypeC newFloatMatrix() throws RuntimeCompilerException {
	return new FrameTypeC(FloatMatrix.class);
    }

    public static FrameTypeC newFloatMatrix(int rows, int cols) throws RuntimeCompilerException {
	FrameTypeC m = newFloatMatrix();
	m.addConstraint(new Equal(Constraint.ROW_DIMENSION, rows));
	m.addConstraint(new Equal(Constraint.COLUMN_DIMENSION, cols));
	return m;
    }

    


}
