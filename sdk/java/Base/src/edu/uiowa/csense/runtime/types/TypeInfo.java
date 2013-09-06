package edu.uiowa.csense.runtime.types;


/**
 * This class will hold the type information. I hope that this should make it
 * easier to handle the typing information and that the initialization of the
 * components will be simplified.
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class TypeInfo {
    protected final Class _class;
    protected int _elementSize = 0; // in bytes
    protected int _rows = 0;
    protected int _columns = 0;
    protected int _numElements = 1;
    protected final boolean _direct;
    protected final boolean _readonly;
    protected final boolean _dynamic;

    public TypeInfo(Class cls) {
	_class = cls;
	_dynamic = true;
	_readonly = false;
	_direct = false;
    }

    public TypeInfo(Class cls, int elementSize, int rows, int columns, boolean direct, boolean readonly) {
	if ((rows <= 0) || (columns <= 0)) {
	    throw new IllegalArgumentException("Type must have at least one row and one column");
	}
	if (elementSize <= 0) {
	    throw new IllegalArgumentException("Element size must be greater than zero."); 
	}
	
	_class = cls;
	_elementSize = elementSize;
	_rows = rows;
	_columns = columns;
	_numElements = rows * columns;
	_direct = direct;
	_readonly = readonly;
	_dynamic = false;
    }

    public boolean isDirect() {
	return _direct;
    }

    public int getNumBytes() {
	return _numElements * _elementSize;
    }

    public Class getJavaType() {
	return _class;
    }

    public int getNumberOfElements() {
	return _numElements;
    }

    public int getElementSize() {
	return _elementSize;
    }

    public String getSimpleName() {
	return _class.getSimpleName();
    }

    public String getName() {
	return _class.getName();
    }

    public static TypeInfo newFloatVector(int size) {
	return new TypeInfo(FloatVector.class, 4, size, 1, true, false);
    }

    public static TypeInfo newDoubleVector(int size) {
	return new TypeInfo(DoubleVector.class, 8, size, 1, true, false);
    }

    public static TypeInfo newByteVector(int size) {
	return new TypeInfo(ByteVector.class, 1, size, 1, true, false);
    }

    public static TypeInfo newCharVector(int size) {
	return new TypeInfo(CharVector.class, 2, size, 1, true, false);
    }

    public static TypeInfo newShortVector(int size) {
	return new TypeInfo(ShortVector.class, 2, size, 1, true, false);
    }

//    public static TypeInfo<DoubleMatrix> newDoubleMatrix(int rows, int columns) {
//	return new TypeInfo<DoubleMatrix>(DoubleMatrix.class, 8, rows, columns, true, false);
//    }

//    public static TypeInfo<FilenameType> newFilenameType() {
//	return new TypeInfo<FilenameType>(FilenameType.class, 2, FilenameType.MAX_SIZE, 1, true, false);
//    }
    
    public static TypeInfo newJavaMessage(Class cls) {
	TypeInfo obj = new TypeInfo(cls, 1, 1, 1, false, false);
	
	return obj;
    }

    public int getColumns() {
	return _columns;
    }

    public int getRows() {
	return _rows;
    }

    public boolean isDynamic() {
	return _dynamic;
    }
}
