package compiler.matlab;


import compiler.CompilerException;
import compiler.matlab.types.MatlabType;

public class MatlabConstant extends MatlabArgument {
    public MatlabConstant(String name, MatlabType type)
	    throws CompilerException {
	super(name, MatlabArgument.INPUT, type);
    }

    @Override
    public String getStringValue() {
	return getMatlabType().getStringValue();
    }

    @Override
    public String toString() {
	return "constant " + _name + " " + getMatlabType();
    }

}
