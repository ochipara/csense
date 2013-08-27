package edu.uiowa.csense.compiler.matlab.types;



import com.jmatio.types.MLUint8;

import edu.uiowa.csense.compiler.CompilerException;

public class MLLogicalArray extends MatlabMatrix<Byte> {

    public MLLogicalArray(int rows, int columns, Byte[] values) {
	super(rows, columns, values);
    }

    public static MLLogicalArray False(int rows, int columns) {
	Byte[] values = new Byte[rows * columns];
	for (int i = 0; i < values.length; i++)
	    values[i] = 0;

	MLLogicalArray l = new MLLogicalArray(rows, columns, values);

	return l;
    }

    @Override
    public String getCodegenType() {
	return "logical";
    }

    @Override
    public String getStringValue() {
	boolean all_false = true;

	for (int i = 0; i < _rows * _columns; i++) {
	    if (_values[i] != 0) {
		all_false = false;
		break;
	    }
	}

	if (all_false)
	    return "false(" + _rows + ", " + _columns + ")";

	assert (false) : "implement me";

	return null;
    }

    @Override
    public String getCType() {
	return "boolean_T";
    }

    @Override
    public String getNioType() {
	return java.nio.Buffer.class.getCanonicalName();
    }

    @Override
    public int getNumberOfBytes() {
	return _rows * _columns;
    }

    @Override
    public boolean hasZeros() {
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

    public static MLLogicalArray fromMLArray(MLUint8 data)
	    throws CompilerException {
	int[] dims = data.getDimensions();

	int rows = dims[0];
	int columns = dims[1];

	Byte[] values = new Byte[rows * columns];
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		int index = r + c * (rows);
		int d = data.getByte(r, c);
		if ((d != 0) && (d != 1)) {
		    throw new CompilerException(
			    "Invalid values found during processing of LogicalArray");
		}
		values[index] = (byte) d;
	    }
	}
	MLLogicalArray array = new MLLogicalArray(rows, columns, values);

	return array;
    }
}
