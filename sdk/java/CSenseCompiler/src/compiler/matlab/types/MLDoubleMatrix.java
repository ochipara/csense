package compiler.matlab.types;

import java.io.File;
import java.io.IOException;



import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import compiler.CompilerException;

public class MLDoubleMatrix extends MatlabMatrix<Double> {

    public MLDoubleMatrix(int rows, int columns) {
	this(rows, columns, null);
    }

    public MLDoubleMatrix(int rows, int columns, Double[] value) {
	super(rows, columns, value);
    }

    public MLDoubleMatrix(double value) {
	super(1, 1, new Double[] { value });
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
		sb.append(Double.toString(_values[0]));
		return sb.toString();
	    }

	    sb.append("[");
	    for (int r = 0; r < _rows; r++) {
		if (r > 0)
		    sb.append(";");

		for (int c = 0; c < _columns; c++) {
		    int index = r + c * (_rows);
		    double d = _values[index];

		    if (d == Double.POSITIVE_INFINITY) {
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

    public static MLDoubleMatrix zeros(int rows, int columns) {
	Double[] doubles = new Double[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    doubles[i] = 0.0;

	return new MLDoubleMatrix(rows, columns, doubles);
    }

    public static MLDoubleMatrix ones(int rows, int columns) {
	Double[] doubles = new Double[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    doubles[i] = 1.0;

	return new MLDoubleMatrix(rows, columns, doubles);
    }

    @Override
    public String getCodegenType() {
	return "double";
    }

    @Override
    public String getCType() {
	return "real_T";
    }

    @Override
    public String getNioType() {
	return java.nio.DoubleBuffer.class.getCanonicalName();
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

    public static MLDoubleMatrix loadData(String filename, String variableName)
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
	    // MLDouble _doubles = (MLDouble) data;
	    // int rows = dims[0];
	    // int columns = dims[1];
	    //
	    //
	    // Double [] values = new Double[rows * columns];
	    // for (int r = 0; r < rows; r++) {
	    // for (int c = 0; c < columns; c++) {
	    // int index = r + c * (rows);
	    // double d = _doubles.get(r, c);
	    // values[index] = d;
	    // }
	    // }
	    //
	    // DoubleMatrix dmatrix = new DoubleMatrix(rows, columns, values);
	    // return dmatrix;

	    return fromMLDouble(data);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    public static MLDoubleMatrix fromMLDouble(MLArray data)
	    throws CompilerException {
	if (data.isDouble() == false) {
	    throw new CompilerException("Conversion failed");
	}

	int[] dims = data.getDimensions();

	MLDouble _doubles = (MLDouble) data;
	int rows = dims[0];
	int columns = dims[1];

	Double[] values = new Double[rows * columns];
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		int index = r + c * (rows);
		double d = _doubles.get(r, c);
		values[index] = d;
	    }
	}

	MLDoubleMatrix dmatrix = new MLDoubleMatrix(rows, columns, values);
	return dmatrix;
    }
    
  
    public static MLDoubleMatrix fromDoubles(int[] lengths, double[][] realArray2D) {
	int rows = lengths[0];
	int columns = lengths[1];
	
	Double[] values = new Double[rows * columns];
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		int index = r + c * (rows);
		double d = realArray2D[r][c];
		values[index] = d;
	    }
	}
	
	MLDoubleMatrix dmatrix = new MLDoubleMatrix(rows, columns, values);
	return dmatrix;
    }

}
