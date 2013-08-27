package edu.uiowa.csense.compiler.model;

import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.RuntimeCompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;




public class InputPortC extends PortC {
    protected List<OutputPortC> _sources = new ArrayList<OutputPortC>();
    protected OutputPortC _internalOutput = null;

    public InputPortC(CSenseComponentC component, BaseTypeC portType,
	    String name) {
	super(component, portType, name);
    }

    public void addIncoming(OutputPortC source) {
	_sources.add(source);
    }

    public OutputPortC getIncoming()  {
	if (_sources.size() == 1) {
	    return _sources.get(0);
	}  else if (_sources.size() == 0) {
	    return null;
	} else {
	    throw new RuntimeCompilerException("Expected the number of incoming links to one");
	}
    }
    
    public List<OutputPortC> getAllIncoming() {
	return _sources;
    }

    public boolean supportsPull() throws CompilerException {
	return getIncoming().canBePulled();
    }

    public void setInternalOutput(OutputPortC out) {
	_internalOutput = out;
    }

    public OutputPortC getInternalOutput() {
	return _internalOutput;
    }

    public String getVariableName() {
	return component.getVariableName();
    }

    public void removeIncoming() {
	_sources.clear();
    }

    public void removeIncoming(OutputPortC outPort) throws CompilerException {
	if (_sources.remove(outPort) == false) {
	    throw new CompilerException("Failed to remove link");
	}
    }
    
    @Override
    public String toString() {
	return super.toString() + " type:" + getType();
    }
    
}
