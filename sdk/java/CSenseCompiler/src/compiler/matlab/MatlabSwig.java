package compiler.matlab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.matlab.types.MatlabMatrix;
import compiler.matlab.types.MatlabStruct;
import compiler.matlab.types.MatlabType;
import compiler.model.Project;
import compiler.utils.Coder;
import compiler.utils.ExecuteCommand;


import project.configuration.CompilerTool;
import project.configuration.SwigTool;
import project.targets.Target;



public class MatlabSwig {
    protected static Logger logger = Logger.getLogger(MatlabSwig.class);
    public static Set<String> typedefs = new HashSet<String>();

    private static String generate_swig(MatlabType type, String name)
	    throws CompilerException {
	String c_type = null;
	if (type instanceof MatlabMatrix) {
	    c_type = type.getCType() + " " + name + "["
		    + type.getNumberOfElements() + "]";

	    return c_type;
	} else if (type instanceof MatlabStruct) {
	    c_type = type.getCType() + "* " + name;
	} else
	    throw new CompilerException("cannot interpret type");

	return c_type;
    }

    public static void generateSwig(MatlabComponentC component)
	    throws CompilerException {
	// clear the typedefs for this file
	typedefs.clear();

	String swigWrapperFile = component.getMatlabFunctionName() + "W";
	Coder coder = new Coder();

	coder.line("%module " + swigWrapperFile);
	coder.line("%include \"matlab.i\"");
	coder.line("");

	String fn_def = functionSignature(component, coder);

	coder.line("%include \"CSenseLib_types.h\"");
	coder.line("%{");
	coder.line("//import functions");
	coder.line("#include \"" + component.getMatlabFunctionName() + ".h\"");
	coder.line("#include \"CSenseLib_types.h\"");
	coder.line(fn_def);
	coder.line("%}");

	coder.line(fn_def);
	try {
	    coder.saveToFile(component.getSwigFile());
	    logger.info("Generating swig file [" + component.getSwigFile()
		    + "]");
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Cannot save swig script");
	}
    }

    /**
     * This is a bit of magic because it is undocumentated how the function
     * declaration from matlab will look like
     * 
     * @param component
     * @param coder
     * @return
     * @throws CompilerException
     */
    private static String functionSignature(MatlabComponentC component,
	    Coder coder) throws CompilerException {
	String return_def = null;

	List<String> args = new ArrayList<String>();
	for (MatlabArgument arg : component.matlabInputs()) {
	    String c_type = generateArgument(arg);

	    if (c_type != null)
		args.add(c_type);
	}

	int outputs = 0;
	for (MatlabArgument arg : component.matlabOutputs()) {
	    if ((arg instanceof MatlabConstant == false)
		    && (arg.getOutputType() == MatlabArgument.OUTPUT)) {
		outputs += 1;
	    }
	}

	for (MatlabArgument arg : component.matlabOutputs()) {
	    if ((arg instanceof MatlabParameter)
		    && (arg.getOutputType() == MatlabArgument.OUTPUT)) {
		String c_type = generateArgument(arg);

		if (c_type != null) {
		    if ((arg.getMatlabType().isPrimitive()) && (outputs == 1)) {
			assert (return_def == null);
			return_def = "real_T";
		    } else {
			args.add(c_type);
		    }
		}
	    }
	}

	String fn_def = component.getMatlabFunctionName() + "(";
	fn_def = fn_def + Coder.list2string(args);
	fn_def = fn_def + ");";

	if ((return_def == null) || (outputs != 1))
	    return_def = "void";
	fn_def = "extern " + return_def + " " + fn_def;
	return fn_def;
    }

    private static String generateArgument(MatlabArgument arg)
	    throws CompilerException {
	MatlabType type = arg.getMatlabType();
	String c_type = null;

	if (arg instanceof MatlabPersistent) {
	    c_type = generate_swig(type, arg.getName());
	    if (arg.getOutputType() == MatlabArgument.INPUT) {
		c_type = "const " + c_type;
	    }
	} else if (arg instanceof MatlabParameter) {
	    MatlabParameter param = (MatlabParameter) arg;
	    // you can have multiple params with the same input but different
	    // outputs
	    c_type = generate_swig(type, param.getName());
	    if (arg.getOutputType() == MatlabArgument.INPUT) {
		c_type = "const " + c_type;
	    }
	} else if (arg instanceof MatlabConstant) {
	    // to nothing as constants are not used as params
	} else
	    throw new IllegalStateException("This should not happen");

	return c_type;
    }

    public static void run_swig(Project project, MatlabComponentC component)
	    throws CompilerException, IOException {
	Target target = project.getTarget();
	CompilerTool compiler = (CompilerTool) project.getSdkConfig().getTool(
		CompilerTool.TOOL_NAME);
	SwigTool swig = (SwigTool) project.getSdkConfig().getTool(SwigTool.TOOL_NAME);

	int code;
	// copy the matlab library from the toolkit to the deployment directory
	FileUtils.copyFileToDirectory(new File(compiler.getSourceDirectory(),
		"matlab.i"), component.getFileDirectory());

	ExecuteCommand invokeSwig = ExecuteCommand.executeCommand();
	code = invokeSwig.execute(
		swig.getSwig() + " -java" + " -I" + target.getJniDirectory()
			+ " -package " + component.getPackage() + " -outdir "
			+ component.getFileDirectory().getCanonicalPath() + " "
			+ component.getSwigFile().getCanonicalPath(), component
			.getFileDirectory().getAbsoluteFile());

	if (code != 0) {
	    logger.error("RUN SWIG -- invokation failed");
	    logger.error(invokeSwig.getLastCommand());
	    logger.error(invokeSwig.output());
	    logger.error(invokeSwig.error());

	    throw new CompilerException("SWIG invokation failed");
	} else {
	    logger.info(invokeSwig.getLastCommand());
	}

	code = invokeSwig.execute("cp "
		+ component.getFileDirectory().getCanonicalPath() + "/"
		+ component.getMatlabFunctionName() + "_wrap.c "
		+ target.getJniDirectory());
	if (code != 0) {
	    logger.error(invokeSwig.getLastCommand());
	    throw new CompilerException(
		    "SWIG -- failed to move the wrapper to JNI folder");
	} else {
	    logger.info(invokeSwig.getLastCommand());
	}
    }

}
