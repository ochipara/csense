package messages.fixed;

import java.nio.DoubleBuffer;

import api.CSenseException;
import api.IMessagePool;
import api.Message;

import messages.TypeInfo;

public class DoubleMatrix extends Matrix<Double> {
    /**
     * 
     */
    protected DoubleBuffer doubles;

    public DoubleMatrix(IMessagePool pool, TypeInfo<DoubleMatrix> typeInfo)
	    throws CSenseException {
	super(pool, typeInfo);
	doubles = buffer().asDoubleBuffer();

	assert (doubles.capacity() > 0);
    }

    @Override
    public Double get(int r, int c) {
	int offset = r + c * rows;
	return doubles.get(offset);
    }

    @Override
    public void put(int r, int c, Double val) {
	int offset = r + c * rows;
	doubles.put(offset, val);
    }

    public void put(int r, int c, double val) {
	int offset = r + c * rows;
	doubles.put(offset, val);
    }

    @Override
    public String displayValues() {
	StringBuffer sb = new StringBuffer();
	for (int r = 0; r < rows; r++) {
	    for (int c = 0; c < columns; c++) {
		sb.append(doubles.get(r + c * rows) + " ");
	    }
	    sb.append("\n");
	}

	return sb.toString();
    }

    @Override
    public void put(Double val) {
	doubles.put(val);

    }

    public void put(double val) {
	doubles.put(val);

    }

    @Override
    public void initialize() {
	super.initialize();
	doubles.clear();
    }

    @Override
    public String toString() {
	return "DoubleMatrix[C:" + doubles.capacity() + " P:" + doubles.position() + "] "
		+ super.toString();
    }


    @Override
    public Message position(int position) {
	doubles.position(position);
	return this;
    }
    
    @Override
    public int remaining() {	
	return doubles.remaining();
    }
    
    public DoubleBuffer getBuffer() {
	return doubles;
    }
}
