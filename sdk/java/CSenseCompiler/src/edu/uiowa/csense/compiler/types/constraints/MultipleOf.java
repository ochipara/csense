package edu.uiowa.csense.compiler.types.constraints;

public class MultipleOf extends Constraint {
    protected final int value;
    
    public MultipleOf(int value) {
  	this(Constraint.COLUMN_DIMENSION, value);
      }    
      
      public MultipleOf(int dim, int value) {
  	super(CONSTRAINT_MULTIPLEOF, dim);
  	if (value <= 0) throw new IllegalArgumentException("Value must be greater than 0");
  	
  	this.value = value;
      }


      @Override
      public int getValue() {
  	return value;
      }
    
    @Override
    public String toString() {
	if (dim == ROW_DIMENSION) return "MULTIPLE(" + value + ") [row]";
	else if (dim == COLUMN_DIMENSION) return "MULTIPLE(" + value + ") [column]";
	else throw new IllegalStateException();
    }

}
