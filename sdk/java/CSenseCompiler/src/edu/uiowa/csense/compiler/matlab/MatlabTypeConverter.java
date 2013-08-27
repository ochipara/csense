package edu.uiowa.csense.compiler.matlab;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;

public interface MatlabTypeConverter {
    public String convertInstanceToMatlab(CSenseComponentC component,
	    MatlabParameter arg) throws CompilerException;
}
