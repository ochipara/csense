package compiler.matlab;


import compiler.CompilerException;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;

/**
 * Stores necessary information to do the mapping between java and matlab
 * 
 * input port ====> NIO Type ====> Matlab Type ====> output port
 * 
 * if the output port is null, then this is an input only argument if the input
 * port is null, then this is an output only argument if the input and output
 * are not null, then this is an input/output argument. in this case the input
 * and the output ports should match
 * 
 * @author ochipara
 * 
 */
public class MatlabParameter extends MatlabArgument {
    protected InputPortC _in;
    protected OutputPortC _out;
    protected int _argType;
    protected boolean _readOnly = false;

    public MatlabParameter(String name, int outputType, InputPortC in, OutputPortC out) throws CompilerException {
	super(name, outputType, in.getType().getMatlabType());
	if (in == null)
	    throw new CompilerException("Input port cannot be null");
	if (out == null)
	    throw new CompilerException("Ouput port cannot be null");

	this._in = in;
	this._out = out;
    }

    public InputPortC getInputPort() {
	return _in;
    }

    public OutputPortC getOutputPort() {
	return _out;
    }

    @Override
    public String getStringValue() {
	return _in.getType().getMatlabType().getStringValue();
    }

    @Override
    public String toString() {
	// baseCompiler.matlab.types.MatlabType type =
	// _in.getType().getMatlabType();
	// return _in.getQName() + " => " + _out.getQName() + " [" + type + "] "
	// + outputTypeToString();
	return "parameter " + _name + " [" + getMatlabType() + "] "
		+ outputTypeToString();
    }
}
