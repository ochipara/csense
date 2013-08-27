package edu.uiowa.csense.compiler.types;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.constraints.Constraint;

public class TypeMaterialization {
    protected int rows = -1;
    protected int columns = - 1;
    
    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    
    public int getColumns() {
        return columns;
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    public void materialize(int dim, int val) throws CompilerException {
	if (dim == Constraint.ROW_DIMENSION) {
	    rows = val;
	} else if (dim == Constraint.COLUMN_DIMENSION) {
	    columns = val;
	} else {
	    throw new CompilerException("Invalid dimension");
	}
    }
    
    @Override
    public String toString() {
	return "rows: " + this.rows + " cols: " + this.columns; 
    }
    
}
