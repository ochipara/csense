package compiler.transformations.types;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.configuration.LpSolveTool;
import project.targets.Target;

import compiler.CompilerException;
import compiler.model.Project;
import compiler.types.constraints.Constraint;
import compiler.types.constraints.Variable;
import compiler.utils.Coder;
import compiler.utils.ExecuteCommand;

public class LinearProgram {
    public List<Variable> variables = new LinkedList<Variable>();
    public List<Inequality> inequalities = new LinkedList<Inequality>();
    public HashMap<String, Variable> variableHash = null;

    public void addVariable(Variable var) {
	variables.add(var);	
    }

    public Inequality newEquation() {
	Inequality e = new Inequality(Constraint.CONSTRAINT_EQUAL);
	inequalities.add(e);
	return e;
    }

    public Inequality newInequality(int operator) {
	if (operator == Constraint.CONSTRAINT_EQUAL) throw new IllegalArgumentException();
	Inequality e = new Inequality(operator);
	inequalities.add(e);

	return e;
    }

    public void code(Inequality inequality, Coder coder) throws CompilerException {
	List<Variable> vars = inequality.getVariables(); 
	List<Double> coefs = inequality.getCoefficients();
	List<Integer> dims = inequality.getDimenesions();

	if (vars.size() != coefs.size()) throw new IllegalStateException();

	//comment(inequality, multiplier, coder, vars, coefs);

	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < vars.size(); i++) {	    
	    Variable var = vars.get(i);
	    Double coef = coefs.get(i);
	    int dim = dims.get(i);
	    String coefStr;
	    if (coef < 0) coefStr = "-";
	    else coefStr = "+";
	    coefStr = coefStr + Double.toString(Math.abs(coef));

	    String varName = var.getName(dim);
	    if (variableHash.containsKey(varName) == false) {
		variableHash.put(varName, var);
	    } else {
		if (variableHash.get(varName) != var) {
		    System.out.println(var.toString());
		    throw new CompilerException("Mapping between names and variables is corrupted");
		}
	    }

	    sb.append(coefStr + " " + varName + " ");   	    
	}	
	if (inequality.getOperator() == Constraint.CONSTRAINT_EQUAL) {
	    sb.append("= ");
	} else if (inequality.getOperator() == Constraint.CONSTRAINT_GTE) {
	    sb.append(">= ");
	} else if (inequality.getOperator() == Constraint.CONSTRAINT_LTE) {
	    sb.append("<= ");
	} else throw new CompilerException("Unknown constraint");
	sb.append(inequality.getConstant());
	coder.code(sb.toString() + ";\r\n");

    }

    public Coder code() throws CompilerException {
	Coder coder = new Coder();
	variableHash = new HashMap<String, Variable>();

	coder.code("min: 1;\r\n");
	int count = 0;
	for (Inequality inequality : inequalities) {
	    code(inequality, coder);
	    count += 1;
	}	

	coder.code("int ");
	boolean first = true;
	for (int i = 0; i < variables.size(); i++) {		    	    
	    for (int dim = 0; dim < 2; dim++) {
		if (first == false) coder.code(", ");
		coder.code(variables.get(i).getName(dim));
		first = false;
	    }
	}
	coder.code(";\r\n");

	return coder;
    }

    public void solve(Target target) throws CompilerException {	
	int count = 0;
	try {
	    Coder coder = code();
	    File fn = new File(target.getDirectory(), "typeassignment.lp");
	    System.out.println(fn);
	    coder.saveToFile(fn);

	    LpSolveTool lpSolve = (LpSolveTool) Project.getProject().getSdkConfig().getTool(LpSolveTool.TOOL_NAME);	    
	    ExecuteCommand cmd = ExecuteCommand.executeCommand();
	    cmd.execute(lpSolve.getLpSolve().getAbsolutePath() + " -lp " + fn.getAbsolutePath());
	    String output = cmd.getOutputMessage();

	    String lines[] = output.split("\\r?\\n");
	    if ("This problem is infeasible".equals(lines[0])) {
		throw new CompilerException("Type assignment is infeasible");
	    }
	    boolean foundValues = false;
	    Pattern p = Pattern.compile("(\\w+)(\\s+)(\\d+)");

	    for (int i = 0; i < lines.length; i++) {				
		if (foundValues) {
		    count = count + 1;
		    Matcher m = p.matcher(lines[i]);
		    if (m.find()) {
			String name = m.group(1); // Access a submatch group; String can't do this.
			String valStr = m.group(3);
			int val = Integer.parseInt(valStr);

			Variable var = variableHash.get(name);
			var.setValue(name, val);
			System.out.println(name + " " + val);
		    } else {
			throw new CompilerException("Cannot convert output");
		    }
		}

		if (lines[i].startsWith("Actual values")) {
		    foundValues = true;
		}
	    }	    
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
	
	if (count == 0) {
	    throw new CompilerException("Failed to resolve ILP");
	}
    }

    public List<Variable> getVariables() {
	return variables;
    }

    public Variable getVariableByName(String name) {
	return variableHash.get(name);

    }
}