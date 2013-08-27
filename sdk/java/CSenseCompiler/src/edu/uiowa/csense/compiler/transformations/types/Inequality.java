package edu.uiowa.csense.compiler.transformations.types;

import java.util.LinkedList;
import java.util.List;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.types.constraints.Variable;

public class Inequality {        
    protected final List<Variable> variables = new LinkedList<Variable>();
    protected final List<Double> coefficients = new LinkedList<Double>();
    protected final List<Integer> dimensions = new LinkedList<Integer>();
    protected final int operator;
    protected Double constant;
    
    public Inequality(int operator) {
	this.operator = operator;
    }

    public void addVariable(Variable var, int dim, double coef) {
	if ((dim != 0) && (dim != 1)) {
	    throw new IllegalArgumentException("Dimension can be either 0 or 1.");
	}
	variables.add(var);
	dimensions.add(dim);
	coefficients.add(coef);
    }
    
    public void setConstant(double d) {
	this.constant = d;
    }

    public int getOperator() {
	return operator;
    }

    public List<Variable> getVariables() {
	return variables;
    }
    
    public List<Double> getCoefficients() {
	return coefficients;
    }
    
    public List<Integer> getDimenesions() {
	return dimensions;
    }
    
    public Double getConstant() {
	return constant;
    }
    
    @Override
    public String toString() {	
	if (variables.size() != coefficients.size()) throw new IllegalStateException();

	//comment(inequality, multiplier, coder, vars, coefs);

	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < variables.size(); i++) {	    
	    Variable var = variables.get(i);
	    Double coef = coefficients.get(i);
	    int dim = dimensions.get(i);
	    String coefStr;
	    if (coef < 0) coefStr = "-";
	    else coefStr = "+";
	    coefStr = coefStr + Double.toString(Math.abs(coef));

	    String varName;
	    try {
		varName = var.getName(dim);
		sb.append(coefStr + " " + varName + " ");   	    
	    } catch (CompilerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }	   
	}	
	if (operator == Constraint.CONSTRAINT_EQUAL) {
	    sb.append("= ");
	} else if (operator == Constraint.CONSTRAINT_GTE) {
	    sb.append(">= ");
	} else if (operator == Constraint.CONSTRAINT_LTE) {
	    sb.append("<= ");
	}
	
	sb.append(constant);
	return sb.toString();
    }
}
