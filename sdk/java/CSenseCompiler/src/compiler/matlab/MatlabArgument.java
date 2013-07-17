package compiler.matlab;


import compiler.CompilerException;
import compiler.matlab.types.MatlabType;

/**
 * 
 * A matlab argument maintains information about arguments of the matlab
 * function _name - is the name of the argument _matlabType - is the information
 * of the matlab type _outputType - defines the type of the argument this is
 * INPUT/OUTPUT/INPUT_OUTPUT
 * 
 * 
 * @author ochipara
 * 
 */
public abstract class MatlabArgument {
    public static final int INPUT = 0;
    public static final int INPUT_OUTPUT = 1;
    public static final int OUTPUT = 2;
    public static final int UNSPECIFIED = 3;

    protected int _outputType = UNSPECIFIED;
    protected String _name;
    protected MatlabType _matlabTypeInfo;

    public MatlabArgument(String name, int outputType, MatlabType matlabType)
	    throws CompilerException {
	assert (outputType == INPUT || outputType == OUTPUT || outputType == INPUT_OUTPUT) : outputType;
	_outputType = outputType;
	_name = name;
	_matlabTypeInfo = matlabType;

	if (matlabType == null)
	    throw new CompilerException("Matlab type cannot be null");
    }

    public MatlabType getMatlabType() {
	return _matlabTypeInfo;
    }

    public String getName() {
	return _name;
    }

    public int getOutputType() {
	return _outputType;
    }

    public String outputTypeToString() {
	switch (_outputType) {
	case INPUT:
	    return "input";
	case INPUT_OUTPUT:
	    return "input/output";
	case OUTPUT:
	    return "output";
	default:
	    return "unspecified io";
	}
    }

    public abstract String getStringValue();

}
