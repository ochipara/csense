package messages.fixed;

import api.CSenseException;
import api.IMessagePool;
import api.Message;
import messages.RawMessage;
import messages.TypeInfo;

public abstract class Matrix<T> extends RawMessage {
    /**
     * 
     */
    private TypeInfo<? extends Matrix<T>> matrixType;
    protected int columns = 0;
    protected int rows = 0;

    public Matrix(IMessagePool<? extends Matrix<T>> pool,
	    TypeInfo<? extends Matrix<T>> type) throws CSenseException {
	super(pool, type);
	matrixType = type;
	this.rows = type.getRows();
	this.columns = type.getColumns();
    }

    public TypeInfo<? extends Matrix<T>> getTypeInfo() {
	return matrixType;
    }

    public abstract void put(T val);

    public abstract T get(int r, int c);

    public abstract void put(int r, int c, T val);

    public abstract String displayValues();

    @Override
    public abstract Message position(int position);

    @Override
    public abstract int remaining();


    @Override
    public void initialize() {
	super.initialize();
    }

}
