package edu.uiowa.csense.compiler.model;

import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.types.BaseTypeC;

public class PortC {
    protected CSenseComponentC component = null;
    protected BaseTypeC type = null;
    protected String name = null;
    protected String description = "";
    protected boolean _optional = false;

    public PortC(CSenseComponentC component, BaseTypeC portType, String name) {
	this.component = component;
	this.type = portType;
	this.name = name;
    }

    public String getSimpleTypeName() {
	return type.getSimpleName();
    }

    public BaseTypeC getType() {
	return type;
    }
    
    public void setType(BaseTypeC type) {
	this.type = type;
    }

    public String getTypeName() {
	return type.getName();
    }

    public String getName() {
	return this.name;
    }

    /**
     * Returns the qualified name of the component
     * 
     * @return
     */
    public String getQName() {
	return this.component.getVariableName() + "::" + name;
    }

    public int getMessageSize() throws CompilerException {
	return this.type.getNumberOfElements();
    }

    public String getComponentName() {
	return component.getName();
    }

    public CSenseComponentC getComponent() {
	return component;
    }

    public boolean isOptional() {
	return _optional;
    }

    public void setComponent(CSenseComponentC component) {
	this.component = component;
    }

    public void setOptional(boolean optional) {
	_optional = optional;
    }

    @Override
    public String toString() {
	if (_optional)
	    return getComponent().getVariableName() + "::" + getName()
		    + " OPTIONAL";
	return getComponent().getVariableName() + "::" + getName();
    }

}
