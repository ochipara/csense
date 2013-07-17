package compiler.types.constraints;


public class GreaterEqual extends Constraint {
    protected final Variable variable;
    protected final int value;

    public GreaterEqual(int dim, int value) {
	super(Constraint.CONSTRAINT_GTE, dim);
	this.variable = null;
	this.value = value;
    }

    public GreaterEqual(Variable variable) {
	super(Constraint.CONSTRAINT_GTE, Constraint.COLUMN_DIMENSION);
	this.variable = variable;
	this.value = -1;
    }

    public GreaterEqual(int value) {
	this(Constraint.COLUMN_DIMENSION, value);
    }

    @Override
    public int getValue() {
	return value;
    }
    
    @Override
    public String toString() {
	return "GTE(" + value + ")";
    }

   
}
