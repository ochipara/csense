package edu.uiowa.csense.compiler.types.constraints;

public abstract class Constraint {    
    public final static int CONSTRAINT_EQUAL = 1;
    public final static int CONSTRAINT_GTE = 2;
    public final static int CONSTRAINT_LTE = 3;
    public static final int CONSTRAINT_MULTIPLEOF = 4;
    
    public final static int SF_CONSTRAINT_GTE = 1000;
    
    // these ints are used to index in an array so they better start from 0
    public final static int ROW_DIMENSION = 0;
    public final static int COLUMN_DIMENSION = 1;
   

    protected final int dim;
    protected final int type;

    public Constraint(int type, int dim) {
	if ((dim != 0) && (dim != 1)) {
	    throw new IllegalArgumentException("Dimension can be either 0 or 1");
	}
	this.type = type;
	this.dim = dim;
    }

    public int getType() {
	return this.type;
    }

    public abstract int getValue();

    public int getDimension() {
	return dim;
    }
}
