package compiler.matlab;


import compiler.CompilerException;
import compiler.matlab.types.MatlabType;

public class MatlabPersistent extends MatlabArgument {
    public MatlabPersistent(String name, MatlabType type)
	    throws CompilerException {
	super(name, MatlabArgument.INPUT_OUTPUT, type);
    }

    @Override
    public String getStringValue() {
	return _matlabTypeInfo.getStringValue();
    }

    @Override
    public String toString() {
	return "persistent " + _name + " " + getMatlabType();
    }
}
