package edu.uiowa.csense.compiler.model;

import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.JavaTypeC;
import edu.uiowa.csense.compiler.utils.JavaCoder;
import edu.uiowa.csense.runtime.workspace.Variable;

public class ArgumentC {
    protected Class _type = null;
    protected String _value = null;
    protected Object _obj = null;

    public ArgumentC(Class type, String value) {
	_type = type;
	_value = value;
    }

    public ArgumentC(int value) {
	_type = int.class;
	_value = Integer.toString(value);
    }

    public ArgumentC(double value) {
	_type = double.class;
	_value = Double.toString(value);
    }

    public ArgumentC(boolean b) {
	_type = boolean.class;
	_value = Boolean.toString(b);
    }

    public ArgumentC(String s) {
	_type = String.class;
	_value = "\"" + s + "\"";
    }

    public ArgumentC(FrameTypeC portType) {
	_type = BaseTypeC.class;
	// _value = "" + portType.getMessageType().getSimpleName() + ".class";
	String qn = portType.getMessageType().getSimpleName();
	_value = "new TypeInfo(" + qn + ".class, "
		+ portType.getElementSize() + ","
		+ portType.getRows() + "," 
		+ portType.getColumns() + " ,true, false)";
    }

    public ArgumentC(long l) {
	_type = Long.class;
	_value = Long.toString(l) + "L";
    }

    public ArgumentC(String[] values) {
	_type = String[].class;
	_value = "new String[] {";
	for (int i = 0; i < values.length; i++) {
	    if (i != 0)
		_value += ",";
	    _value += "\"" + values[i] + "\"";
	}
	_value += "}";

    }

    public ArgumentC(Variable variable) {
	_type = Variable.class;
	_value = " new " + Variable.class.getName() + "(\""
		+ variable.getName() + "\")";
    }

    public ArgumentC(long[] values) {
	_type = Long[].class;
	_value = "new long[] {";
	for (int i = 0; i < values.length; i++) {
	    if (i != 0)
		_value += ",";
	    _value += values[i];
	}
	_value += "}";    
    }
 
    public ArgumentC(JavaTypeC type) {
	_type = type.getMessageType();
	String qn = type.getMessageType().getSimpleName();
	_value = "new TypeInfo(" + qn + ".class, "
		+ type.getElementSize() + ","
		+ 1 + "," 
		+ 1 + " ,true, false)";
    }

    /**
     * Will create an argument with the type of the port after the type is materialized.
     * This is really useful for generic components that have generic port
     * 
     * @param inputPort
     */
    public ArgumentC(InputPortC inputPort) {
	_type = InputPortC.class;
	_obj = inputPort;
    }

    public void code(JavaCoder coder) {
	if (_type == InputPortC.class == false) { 
	    coder.append(_value);
	} else {
	    
	    InputPortC port = (InputPortC) _obj;
	    BaseTypeC type = port.getType();
	    
	    String qn = type.getMessageType().getSimpleName();
	    _value = "new TypeInfo(" + qn + ".class, " 
			+ type.getElementSize() + ", " 
			+ type.getRows() + ", " 
			+ type.getColumns() + ", true, false)";
	    coder.append(_value);
	}
    }

    // TODO: we need a better way of handling this!
    public static ArgumentC self() {
	ArgumentC arg = new ArgumentC("this");
	arg._type = Class.class;
	arg._value = "this";
	return arg;
    }
    
    public Class getType() {
	return _type;
    }

}
