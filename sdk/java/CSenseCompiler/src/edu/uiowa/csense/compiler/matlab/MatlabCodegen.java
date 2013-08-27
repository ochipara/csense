package edu.uiowa.csense.compiler.matlab;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.matlab.types.MatlabMatrix;
import edu.uiowa.csense.compiler.matlab.types.MatlabPrimitive;
import edu.uiowa.csense.compiler.matlab.types.MatlabStruct;
import edu.uiowa.csense.compiler.matlab.types.MatlabType;
import edu.uiowa.csense.compiler.matlab.types.NameValuePair;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.targets.Target;
import edu.uiowa.csense.compiler.utils.MatlabCoder;
import edu.uiowa.csense.compiler.utils.MatlabCommand;
import edu.uiowa.csense.runtime.types.DoubleVector;

public class MatlabCodegen {
    protected static Logger logger = Logger.getLogger(MatlabCodegen.class);
    protected static Integer argCounter = 0;

    public static void generate_codegen_script(Project project,
	    CSenseGroupC main, File codegenScript) throws CompilerException {
	ComponentGraph cgraph = main.getComponentGraph();
	Target target = project.getTarget();

	MatlabCoder code = new MatlabCoder();
	code.line("cfg = coder.config('lib');");
	code.line("%configuring the hardware implementation");
	code.line("hw_cfg = coder.HardwareImplementation;");
	if ("android".compareTo(Project.getPlatform()) == 0) {
	    // special HW configuration for android targets
	    code.line("hw_cfg.TargetHWDeviceType ='ARM Compatible->ARM 10';");
	} else {
	    code.line("hw_cfg.TargetHWDeviceType ='ARM Compatible->ARM 10';");
	}
	code.line("cfg.HardwareImplementation = hw_cfg;");

	HashMap<MatlabComponentC, String> arguments = new HashMap<MatlabComponentC, String>();
	for (IComponentC component : cgraph.components()) {
	    if (component instanceof MatlabComponentC) {
		MatlabComponentC matlabComponent = (MatlabComponentC) component;
		generate_codegen_script(matlabComponent, arguments, code);

	    }
	}

	code.line("\n%running the code generator");
	code.line("disp('Genering code from matlab')");

	StringBuffer invoke = new StringBuffer("codegen -c -config cfg -d "
		+ target.getJniDirectory().getAbsolutePath() + " ...\n");
	invoke.append("\t\tCSenseLib ... \n");
	for (Iterator<MatlabComponentC> iter = arguments.keySet().iterator(); iter
		.hasNext();) {
	    MatlabComponentC component = iter.next();
	    String arg = arguments.get(component);
	    invoke.append("\t\t" + component.getMatlabFunctionName()
		    + " -args {" + arg + "} ... \n");
	}
	invoke.append("\t\t-report");
	code.line(invoke.toString());

	try {
	    code.saveToFile(codegenScript);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("cannot create matlab compile script");
	}
    }

    public static String generate_type(MatlabType type, MatlabCoder coder)
	    throws CompilerException {
	String argName;

	if (type instanceof MatlabPrimitive) {
	    argName = "arg" + argCounter;
	    coder.line(argName + " = coder.newtype('" + type.getCodegenType()
		    + "', [1 1]);");
	} else if (type instanceof MatlabMatrix) {
	    argName = "arg" + argCounter;
	    MatlabMatrix array_type = (MatlabMatrix) type;
	    coder.line(argName + " = coder.newtype('"
		    + array_type.getCodegenType() + "', ["
		    + array_type.getRows() + ", " + array_type.getColumns()
		    + "]);");

	} else if (type instanceof MatlabStruct) {
	    MatlabStruct struct_type = (MatlabStruct) type;

	    String m_str = "";
	    for (Iterator<NameValuePair> i = struct_type.iterator(); i
		    .hasNext();) {
		NameValuePair pair = i.next();

		String nestedArg = generate_type(pair.getType(), coder);
		if (m_str != "")
		    m_str += ",...\n\t ";
		else
		    m_str += "struct(";

		m_str += "'" + pair.getName() + "', " + nestedArg + "";
		argCounter = argCounter + 1;
	    }
	    m_str += ")";

	    argName = "arg" + argCounter;
	    coder.line(argName + " = coder.newtype('struct', " + m_str + ");");
	    coder.line(argName + "T = coder.cstructname(" + argName + ",'"
		    + struct_type.getCType() + "');");
	    argName = argName + "T";
	} else {
	    throw new CompilerException("Unknown matlab type");
	}

	return argName;
    }

    public static String generate_type(MatlabArgument arg, MatlabCoder coder,
	    Integer argCounter) throws CompilerException {
	String argName = null;

	// if (arg.isreturn() == false) {
	if (arg instanceof MatlabParameter) {
	    MatlabParameter param = (MatlabParameter) arg;
	    if (param.getOutputType() != MatlabArgument.OUTPUT)
		argName = generate_type(arg.getMatlabType(), coder);
	} else if (arg instanceof MatlabPersistent) {
	    argName = generate_type(arg.getMatlabType(), coder);
	} else if (arg instanceof MatlabConstant) {
	    MatlabConstant constant = (MatlabConstant) arg;
	    argName = "arg" + argCounter;
	    argCounter += 1;

	    coder.line(argName + " = coder.Constant("
		    + constant.getStringValue() + ");");
	} else {
	    throw new CompilerException("Cannot interpret argument " + arg);
	}
	// }

	return argName;
    }

    public static void generate_codegen_script(MatlabComponentC component,
	    HashMap<MatlabComponentC, String> arguments, MatlabCoder coder)
	    throws CompilerException {
	coder.line("\n%generating the argument types for "
		+ component.getName());

	String arg_str = "";
	for (MatlabArgument arg : component.matlabInputs()) {
	    String arg_name = generate_type(arg, coder, argCounter);
	    if (arg_name != null) {
		if (arg_str == "")
		    arg_str += arg_name;
		else
		    arg_str += ", " + arg_name;
		argCounter = argCounter + 1;
	    } else {
		// this happens when the argument is output only
	    }
	}

	arguments.put(component, arg_str);
    }

    protected static MatlabType covertToMatlabType(InputPortC port) throws CompilerException {
	String qn = port.getTypeName();

	if (DoubleVector.class.getCanonicalName().compareTo(qn) == 0) {
	    return new MLDoubleMatrix(port.getMessageSize(), 1, null);
	} else if (Double.class.getCanonicalName().compareTo(qn) == 0) {
	    return new MLDoubleMatrix(0.0);
	}

	return null;
    }

    public static void run_codegen(Project project) throws CompilerException {
	Target target = project.getTarget();

	MatlabCommand cmd = new MatlabCommand();
	cmd.command("cd " + target.getJniDirectory());
	cmd.command("clear all; codegen_script");
	cmd.disconnect();
    }
}
