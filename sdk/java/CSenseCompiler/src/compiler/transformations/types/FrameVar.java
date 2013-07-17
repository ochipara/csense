package compiler.transformations.types;

import compiler.CompilerException;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Variable;

public class FrameVar extends Variable {       
    protected final String[] name =  new String[2];
    protected int[] val = new int[2];
    
    public FrameVar(String string) {
	this.name[Constraint.COLUMN_DIMENSION] = string + "_col";
	this.name[Constraint.ROW_DIMENSION] = string + "_row";
	this.val[0] = -1;
	this.val[1] = -1;
    }
    
    @Override
    public String getName(int dim) {
	return name[dim];
    }

    @Override
    public void setValue(int dim, int val) {
	this.val[dim] = val;
    }
    
    @Override
    public void setValue(String dimName, int val) throws CompilerException {
	for (int dim = 0; dim < 2; dim++) {
	    if (this.name[dim].equals(dimName)) {
		setValue(dim, val);
		return;
	    }
	}
	
	throw new CompilerException("Invalid dimension name");
    }
    
    
    @Override
    public int getValue(int dim) {
	return this.val[dim];
    }
    
    @Override
    public String toString() {
	return name[Constraint.ROW_DIMENSION] + ":" + val[Constraint.ROW_DIMENSION] + " " + 
		name[Constraint.COLUMN_DIMENSION] + ":" + val[Constraint.COLUMN_DIMENSION];
    }
}
