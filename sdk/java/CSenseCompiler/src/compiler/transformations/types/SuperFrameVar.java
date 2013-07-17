package compiler.transformations.types;

import java.util.LinkedList;
import java.util.List;

import compiler.CompilerException;
import compiler.model.PortC;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Variable;
import api.IComponentC;

public class SuperFrameVar extends Variable {  
    protected List<PortC> path = new LinkedList<PortC>();
    protected List<Constraint> constraints = new LinkedList<Constraint>();
    protected int[] val = new int[2];
    protected boolean finalized = false;
    protected String[] name = null;

    public SuperFrameVar() {
	super();
    }

    public void addConstraint(Constraint constraint) {
	if (finalized == true) throw new IllegalAccessError();
	constraints.add(constraint);	
    }

    @Override
    public String getName(int dim) throws CompilerException {
	finalized = true;
	if (name == null) {
	    name = new String[2];
	    IComponentC src = path.get(0).getComponent();
	    IComponentC dest = path.get(path.size() - 1).getComponent();

	    String prefix = "SF_" + src.getVariableName() + "_" + dest.getVariableName() + "_" + path.get(path.size() - 1).getName();
	    
	    name[Constraint.COLUMN_DIMENSION] = prefix + "_col";
	    name[Constraint.ROW_DIMENSION] =  prefix + "_row";
	}
	
	return name[dim];
    }    

    public List<Constraint> getConstraints() {
	return constraints;
    }

    @Override
    public void setValue(int dim, int val) {
	this.val[dim] = val;
    }

    @Override
    public int getValue(int dim) {
	return this.val[dim];
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
    
    public void addPort(PortC port) {
	path.add(port);
    }

    public boolean pathContainsPort(PortC port) {
	return path.contains(port);
    }

    @Override
    public String toString() {
	if (finalized == false) {
	    return "not finalized";
	} else {
	    if (val == null) return name + " val:None";
	    else {
		return name[Constraint.ROW_DIMENSION] + ":" + val[Constraint.ROW_DIMENSION] +
			" " + name[Constraint.COLUMN_DIMENSION] + ":" + val[Constraint.COLUMN_DIMENSION];
	    }
	}
    }

   
}
