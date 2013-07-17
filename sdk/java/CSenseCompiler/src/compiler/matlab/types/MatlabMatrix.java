package compiler.matlab.types;

public abstract class MatlabMatrix<T> extends MatlabType {
    protected int _rows;
    protected int _columns;
    protected T[] _values;

    public MatlabMatrix(int rows, int columns, T[] value) {
	_rows = rows;
	_columns = columns;
	_values = value;
    }

    public int getRows() {
	return _rows;
    }

    public int getColumns() {
	return _columns;
    }

    @Override
    public int getNumberOfElements() {
	return _rows * _columns;
    }

    public abstract boolean hasZeros();

    public T[] getValues() {
	return _values;
    }

    public void set(int r, int c, T value) {
	int index = r + c * _rows;
	_values[index] = value;
    }

    public int index(int r, int c) {
	return r + c * _rows;
    }

    public T getValue(int r, int c) {
	return _values[index(r, c)];
    }
}
