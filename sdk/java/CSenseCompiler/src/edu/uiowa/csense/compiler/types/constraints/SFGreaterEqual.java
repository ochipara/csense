package edu.uiowa.csense.compiler.types.constraints;

public class SFGreaterEqual extends Constraint {
    private final int value;

    public SFGreaterEqual(int dim, int value) {
	super(Constraint.SF_CONSTRAINT_GTE, dim);
	this.value = value;
    }

    @Override
    public int getValue() {
	return value;
    }
    
    
    
    @Override
    public String toString() {
	if (dim == ROW_DIMENSION) return "SF_GTE(" + value + ") [row]";
	else if (dim == COLUMN_DIMENSION) return "SF_GTE(" + value + ") [column]";
	else throw new IllegalStateException();
    }

}
