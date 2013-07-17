package compiler.matlab;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.InputPortC;

public class DefaultTypeConverter implements MatlabTypeConverter {
    protected final static DefaultTypeConverter converter = new DefaultTypeConverter();

    public DefaultTypeConverter() {
    }

    public static MatlabTypeConverter getConverter() {
	return converter;
    }

    @Override
    public String convertInstanceToMatlab(CSenseComponentC component,
	    MatlabParameter arg) throws CompilerException {
	StringBuffer sb = new StringBuffer();
	String type = arg.getMatlabType().getNioType();
	InputPortC port = arg.getInputPort();

	String msg_var = port.getName() + "Msg";
	String argName = arg.getName() + "Buf";
	if ("java.nio.DoubleBuffer".compareTo(type) == 0) {
	    sb.append("DoubleBuffer " + argName + " =  " + msg_var + ".getBuffer();");

	    return sb.toString();
	} else if ("java.nio.FloatBuffer".compareTo(type) == 0) {
	    sb.append("FloatBuffer " + argName + " =  " + msg_var + ".getBuffer();");

	    return sb.toString();
	} else if ("java.nio.ShortBuffer".compareTo(type) == 0) {
	    sb.append("ShortBuffer " + argName + " =  " + msg_var + ".getBuffer();");

	    return sb.toString();	    
	}

	throw new CompilerException("Could not convert type " + arg + " for component " + component);
    }

}
