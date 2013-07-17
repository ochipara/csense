package compiler.model;


public class PortPair {
    protected InputPortC _input;
    protected OutputPortC _output;

    public PortPair(InputPortC input, OutputPortC output) {
	_input = input;
	_output = output;
    }

    public OutputPortC getOutput() {
	return _output;
    }
    
    public InputPortC getInput() {
	return _input;
    }

    @Override
    public String toString() {
	if (_input == null) return "* => " + _output.getQName();
	else {
	    String s = _input.getQName() + " => ";
	    if (_output != null) s += _output.getQName();
	    else s += " *";
	    return s;
	}
    }

}
