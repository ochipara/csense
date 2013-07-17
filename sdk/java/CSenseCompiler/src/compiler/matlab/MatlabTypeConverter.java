package compiler.matlab;

import compiler.CSenseComponentC;
import compiler.CompilerException;

public interface MatlabTypeConverter {
    public String convertInstanceToMatlab(CSenseComponentC component,
	    MatlabParameter arg) throws CompilerException;
}
