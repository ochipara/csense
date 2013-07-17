package compiler.matlab.types;

import java.io.File;
import java.io.IOException;


import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLSingle;
import compiler.CompilerException;

public class MLFloatMatrix extends MatlabMatrix<Float> {

    public MLFloatMatrix(int rows, int columns) {
	this(rows, columns, null);
    }

    public MLFloatMatrix(int rows, int columns, Float[] value) {
	super(rows, columns, value);
    }

    public MLFloatMatrix(float value) {
	super(1, 1, new Float[] { value });
    }

    @Override
    public String getStringValue() {
	StringBuffer sb = new StringBuffer("");

	boolean all_zeros = true;
	for (int i = 0; i < _rows * _columns; i++) {
	    if (_values[i] != 0) {
		all_zeros = false;
		break;
	    }
	}

	if (all_zeros) {
	    sb.append("zeros(" + _rows + ", " + _columns + ")");
	} else {
	    if (_rows == 1 && _columns == 1) {
		sb.append(Float.toString(_values[0]));
		return sb.toString();
	    }

	    sb.append("[");
	    for (int r = 0; r < _rows; r++) {
		if (r > 0)
		    sb.append(";");

		for (int c = 0; c < _columns; c++) {
		    int index = r + c * (_rows);
		    double d = _values[index];

		    if (d == Float.POSITIVE_INFINITY) {
			sb.append("Inf ");
		    } else {
			sb.append(d + " ");
		    }
		}

	    }
	    sb.append("]");
	}

	return sb.toString();
    }

    public static MLFloatMatrix zeros(int rows, int columns) {
	Float[] floats = new Float[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    floats[i] = 0.0f;

	return new MLFloatMatrix(rows, columns, floats);
    }

    public static MLFloatMatrix ones(int rows, int columns) {
	Float[] doubles = new Float[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    doubles[i] = 1.0f;

	return new MLFloatMatrix(rows, columns, doubles);
    }

    @Override
    public String getCodegenType() {
	return "single";
    }

    @Override
    public String getCType() {
	return "real32_T";
    }

    @Override
    public String getNioType() {
	return java.nio.FloatBuffer.class.getCanonicalName();
    }

    @Override
    public int getNumberOfBytes() {
	return _rows * _columns * 8;
    }

    @Override
    public boolean hasZeros() {
	if (_values == null)
	    return true;

	for (int r = 0; r < _columns * _rows; r++) {
	    if (_values[r] != 0)
		return false;
	}

	return true;
    }

    @Override
    public boolean isPrimitive() {
	return (_rows == 1 && _columns == 1);
    }

    public static MLFloatMatrix loadData(String filename, String variableName)
	    throws CompilerException {
	File f = new File(filename);
	MatFileFilter filter = new MatFileFilter();
	filter.addArrayName(variableName);

	MatFileReader reader;
	try {
	    reader = new MatFileReader(f, filter);
	    MLArray data = reader.getContent().get(variableName);
	    if (data == null) {
		throw new CompilerException("Could not find variable ["
			+ variableName + "] to load from file");
	    }
	    // int[] dims = data.getDimensions();
	    //
	    //
	    // MLFloat _doubles = (MLFloat) data;
	    // int rows = dims[0];
	    // int columns = dims[1];
	    //
	    //
	    // Float [] values = new Float[rows * columns];
	    // for (int r = 0; r < rows; r++) {
	    // for (int c = 0; c < columns; c++) {
	    // int index = r + c * (rows);
	    // double d = _doubles.get(r, c);
	    // values[index] = d;
	    // }
	    // }
	    //
	    // FloatMatrix dmatrix = new FloatMatrix(rows, columns, values);
	    // return dmatrix;

	    return fromMLFloat(data);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    public static MLFloatMatrix fromMLFloat(MLArray data)
	    throws CompilerException {
	if (data.isSingle() == false) {
	    throw new CompilerException("Conversion failed");
	}

	int[] dims = data.getDimensions();

	MLSingle _doubles = (MLSingle) data;
	int rows = dims[0];
	int columns = dims[1];

	Float[] values = new Float[rows * columns];
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		int index = r + c * (rows);
		float d = _doubles.get(r, c);
		values[index] = d;
	    }
	}

	MLFloatMatrix dmatrix = new MLFloatMatrix(rows, columns, values);
	return dmatrix;
    }

    @Override
    public String toString() {
	return "mlfloat " + _rows + " x " + _columns;
    }
}
