package compiler.model;

import java.util.ArrayList;
import java.util.List;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.types.BaseTypeC;


/**
 * Specifies the output port of a component
 * 
 * Notes:
 * - a port can be either a source or a reference. when the port is a source it means that the data is generated within the component. 
 * this means that this port will be part the source of a path. if the component is not set as a source, this means that its type depends 
 * on the input link that it is connected to.  
 * 
 * @author ochipara
 *
 */

public class OutputPortC extends PortC {
    protected List<InputPortC> _outLinks = new ArrayList<InputPortC>();
    protected InputPortC _internalInput = null;
    protected boolean _canPull;
    protected boolean isSource = false;

    public OutputPortC(CSenseComponentC component, BaseTypeC portType, String name) {
	this(component, portType, name, false);
    }

    public OutputPortC(CSenseComponentC component, BaseTypeC portType,
	    String name, boolean canPull) {
	super(component, portType, name);
	_canPull = canPull;
    }

    public void addOutgoing(InputPortC destination) {
	_outLinks.add(destination);
    }

    public void removeOutgoingLink(InputPortC destination) {
	_outLinks.remove(destination);
    }

    public List<InputPortC> outLinks() {
	return _outLinks;
    }

    public boolean canBePulled() {
	return _canPull;
    }

    public List<InputPortC> getOutgoing() {
	return _outLinks;
    }

    public void setSupportsPull(boolean canPull) {
	_canPull = canPull;
    }

    public void setInternalInput(InputPortC internalInput) {
	_internalInput = internalInput;
    }

    public InputPortC getInternalInput() {
	return _internalInput;
    }

    public InputPortC getSingleOutgoing() throws CompilerException {
	if (_outLinks.size() != 1) {
	    throw new CompilerException("Expected a single outgoing link");
	}
	return _outLinks.get(0);
    }

    public String getVariableName() {
	return component.getVariableName();
    }

    public void removeOutgoing() {
	_outLinks.clear();
    }
    
    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean isSource) {
        this.isSource = isSource;
    }

}
