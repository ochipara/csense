package compiler.types.constraints;

public class LessEqual extends Constraint {
    protected final int value;
    
    public LessEqual(int value) {
	super(Constraint.CONSTRAINT_LTE, Constraint.COLUMN_DIMENSION);
	this.value = value;
    }
    
    @Override
    public int getValue() {	
	return this.value;
    }
    
    @Override
    public String toString() {
	if (dim == ROW_DIMENSION) return "LTE(" + value + ") [row]";
	else if (dim == COLUMN_DIMENSION) return "LTE(" + value + ") [column]";
	else throw new IllegalStateException();
     }

    @Override
    public int getDimension() {
	// TODO Auto-generated method stub
	return 0;
    }
}
