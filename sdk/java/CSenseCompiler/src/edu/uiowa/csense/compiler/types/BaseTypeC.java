package edu.uiowa.csense.compiler.types;

import java.util.LinkedList;
import java.util.List;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.types.MatlabType;
import edu.uiowa.csense.compiler.types.constraints.Constraint;

public abstract class BaseTypeC implements Cloneable {
    protected final Class _messageType;
    protected int _elementSize;
    protected MatlabType _matlabType = null;
    protected List<Constraint> _constraints = new LinkedList<Constraint>();
    protected int _rows = -1;
    protected int _columns = -1;

    protected BaseTypeC() {
	_messageType = null;
	_elementSize = 0;
    }

    protected BaseTypeC(Class messageType) {
	if (messageType == null) {
	    throw new IllegalArgumentException("Argument cannot be null");
	}
	_messageType = messageType;	
    }

    public int getElementSize() {
	return _elementSize;
    }

    public void mapToJNI(MatlabType matlabType) {
	_matlabType = matlabType;
    }

    public MatlabType getMatlabType() {
	return _matlabType;
    }

    public Class getMessageType() {
	return _messageType;
    }

    public String getSimpleName() {
	return _messageType.getSimpleName();
    }

    public String getName() { 
	return _messageType.getName();
    }

    public void addConstraint(Constraint constraint) {	
	_constraints.add(constraint);
    }

    public List<Constraint> getConstraints() {
	return _constraints;
    }

    public int getRows() {
	return _rows;
    }

    public int getColumns() {
	return _columns;
    }

    public void setColumns(int columns) {
	_columns = columns;  	
    }

    public void setRows(int rows) {
	_rows = rows;	
    }

    public abstract int getNumberOfElements() throws CompilerException;
    public abstract void setMultiplier(int row, int col);
    public abstract int getMultipler(int dimension);
    @Override
    public abstract Object clone();

}
