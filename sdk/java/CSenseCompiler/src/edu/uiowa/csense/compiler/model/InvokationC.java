package edu.uiowa.csense.compiler.model;

import edu.uiowa.csense.compiler.utils.JavaCoder;

public class InvokationC extends ArgumentC {
    String _method;
    ArgumentC _argument;

    public InvokationC(Class type, String method, ArgumentC argument) {
	super(type, method);
	_method = method;
	_argument = argument;
    }

    @Override
    public void code(JavaCoder coder) {
	coder.code(_type.getCanonicalName() + "." + _method + "(");
	_argument.code(coder);
	coder.code(")");
    }
}
