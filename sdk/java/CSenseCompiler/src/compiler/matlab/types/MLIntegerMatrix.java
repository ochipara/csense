package compiler.matlab.types;

public class MLIntegerMatrix extends MatlabMatrix<Integer> {

    public MLIntegerMatrix(int rows, int columns, Integer[] value) {
	super(rows, columns, value);
    }

    public MLIntegerMatrix(int rows, int columns) {
	this(rows, columns, null);
    }

    public MLIntegerMatrix(int value) {
	this(1, 1, new Integer[] { value });
    }

    @Override
    public String getStringValue() {
	StringBuffer sb = new StringBuffer("");

	if (_rows == 1 && _columns == 1) {
	    return Integer.toString(_values[0]);
	}

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
	    sb.append("[");
	    for (int r = 0; r < _rows; r++) {
		if (r > 0)
		    sb.append(";");

		sb.append("[");
		for (int c = 0; c < _columns; c++) {
		    int index = r + c * (_rows);
		    int d = _values[index];

		    sb.append(d + " ");

		}
		sb.append("]");
	    }
	    sb.append("]");
	}

	return sb.toString();
    }

    @Override
    public String getCodegenType() {
	return "int";
    }

    public static MLIntegerMatrix zeros(int rows, int columns) {
	Integer[] ints = new Integer[rows * columns];
	for (int i = 0; i < rows * columns; i++)
	    ints[i] = 0;

	return new MLIntegerMatrix(rows, columns, ints);
    }

    @Override
    public String getCType() {
	return "uint32_t";
    }

    @Override
    public String getNioType() {
	return java.nio.IntBuffer.class.getCanonicalName();
    }

    @Override
    public int getNumberOfBytes() {
	return _columns * _rows;
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
}
