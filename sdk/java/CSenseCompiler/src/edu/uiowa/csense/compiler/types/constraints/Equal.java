package edu.uiowa.csense.compiler.types.constraints;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.RuntimeCompilerException;

public class Equal extends Constraint { 
    protected final int value;

    public Equal(int value) throws CompilerException {
	this(COLUMN_DIMENSION, value);
    }    

    public Equal(int dim, int value) throws RuntimeCompilerException {
	super(CONSTRAINT_EQUAL, dim);
	if (value <= 0) throw new IllegalArgumentException("Value must be greater than 0");
	this.value = value;	
	if ((dim != Constraint.COLUMN_DIMENSION) && (dim != Constraint.ROW_DIMENSION)) {
	    throw new RuntimeCompilerException("Invalid dimension");
	}
    }


    @Override
    public int getValue() {
	return value;
    }
    
    @Override
    public String toString() {	
	if (dim == ROW_DIMENSION) return " EQUAL(" + value + ") [row]";
	else if (dim == COLUMN_DIMENSION) return " EQUAL(" + value + ") [column]";
	else throw new IllegalStateException();
    }
}
