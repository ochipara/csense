package compiler.transformations.types;

import compiler.CompilerException;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Variable;

public class MultiplierVar extends Variable {
    protected final String[] name = new String[2];
    protected int[] val = null;
    
    public MultiplierVar(String name) {
	this.name[Constraint.COLUMN_DIMENSION] = "M_" + name + "_col";
	this.name[Constraint.ROW_DIMENSION] = "M_" + name + "_row";
    }

    @Override
    public String getName(int dim) {
	return name[dim];
    }
    
    @Override
    public void setValue(int dim, int val) {
	if (this.val == null) this.val = new int[2];
	    	
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
	return name + " val:" + val;
    }
}
