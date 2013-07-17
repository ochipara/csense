package compiler.matlab.types;

import java.io.File;
import java.io.IOException;


import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLShortArray;
import compiler.CompilerException;

public class MLShortMatrix extends MatlabMatrix<Short> {

    public MLShortMatrix(int rows, int columns) {
	this(rows, columns, null);
    }

    public MLShortMatrix(int rows, int columns, Short[] value) {
	super(rows, columns, value);
    }

    public MLShortMatrix(short value) {
	super(1, 1, new Short[] { value });
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
		sb.append(Short.toString(_values[0]));
		return sb.toString();
	    }

	    sb.append("[");
	    for (int r = 0; r < _rows; r++) {
		if (r > 0)
		    sb.append(";");

		for (int c = 0; c < _columns; c++) {
		    int index = r + c * (_rows);
		    short d = _values[index];

		    sb.append(d + " ");
		}
	    }
	    sb.append("]");
	}

	return sb.toString();
    }

    public static MLShortMatrix zeros(int rows, int columns) {
	Short[] shorts = new Short[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    shorts[i] = 0;

	return new MLShortMatrix(rows, columns, shorts);
    }

    public static MLShortMatrix ones(int rows, int columns) {
	Short[] shorts = new Short[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    shorts[i] = 1;

	return new MLShortMatrix(rows, columns, shorts);
    }

    @Override
    public String getCodegenType() {
	return "int16";
    }

    @Override
    public String getCType() {
	return "int16_T";
    }

    @Override
    public String getNioType() {
	return java.nio.ShortBuffer.class.getCanonicalName();
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

    public static MLShortMatrix loadData(String filename, String variableName)
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
	    // MLShort _doubles = (MLShort) data;
	    // int rows = dims[0];
	    // int columns = dims[1];
	    //
	    //
	    // Short [] values = new Short[rows * columns];
	    // for (int r = 0; r < rows; r++) {
	    // for (int c = 0; c < columns; c++) {
	    // int index = r + c * (rows);
	    // double d = _doubles.get(r, c);
	    // values[index] = d;
	    // }
	    // }
	    //
	    // ShortMatrix dmatrix = new ShortMatrix(rows, columns, values);
	    // return dmatrix;

	    return fromMLShort(data);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    public static MLShortMatrix fromMLShort(MLArray data)
	    throws CompilerException {
	if (data.isSingle() == false) {
	    throw new CompilerException("Conversion failed");
	}

	int[] dims = data.getDimensions();

	MLShortArray shorts = (MLShortArray) data;
	int rows = dims[0];
	int columns = dims[1];

	Short[] values = new Short[rows * columns];
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		int index = r + c * (rows);
		short d = shorts.get(r, c);
		values[index] = d;
	    }
	}

	MLShortMatrix dmatrix = new MLShortMatrix(rows, columns, values);
	return dmatrix;
    }

    @Override
    public String toString() {
	return "mlshort " + _rows + " x " + _columns;
    }
}
