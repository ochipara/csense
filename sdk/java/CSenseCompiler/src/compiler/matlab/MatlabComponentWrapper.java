package compiler.matlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import compiler.CompilerException;
import compiler.matlab.types.MLDoubleMatrix;
import compiler.matlab.types.MatlabMatrix;
import compiler.matlab.types.MatlabPrimitive;
import compiler.matlab.types.MatlabStruct;
import compiler.matlab.types.MatlabType;
import compiler.matlab.types.NameValuePair;
import compiler.model.ComponentCoder;
import compiler.model.DefaultComponentCoder;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.model.PortC;
import compiler.utils.Coder;
import compiler.utils.JavaCoder;


public class MatlabComponentWrapper {
    /**
     * Holds the functions used to generate the wrapper for a component It
     * follows the below template. Everything is literal except the [] which map
     * to function calls
     * 
     * public class [ComponentName]<[TypeSignature]> extends EgoComponent {
     * public InPort<[SimpleTypeName]> [portName] = null; public
     * OutPort<[SimpleTypeName]> [portName] = null;
     * 
     * public [ComponentName]( [typed arguments -- num of outgoing for each
     * port, timer) { [portName] = new InPort<[SimpleTypeName]>(this, 1);
     * [portName] = new OutPort<[SimpleTypeName>(this, [portName]Sz); -- Apply
     * for each output port
     * 
     * @throws CompilerException
     */
    public static Coder generateComponentWrapper(MatlabComponentC component)
	    throws CompilerException {
	JavaCoder coder = new JavaCoder();
	coder.code("package " + component.getPackage() + ";");

	coder.code("\r/** This file was automatically generated. DO NOT EDIT **/\r\r");

	generateImports(component, coder);
	generateClassSignature(component, coder);
	generatePortDeclarations(component, coder);
	generateConstructor(component, coder);
	generateInitialize(component, coder);
	generateDoInput(component, coder);
	generateDeInitialize(component, coder);

	// end of class
	coder.code("}");
	return coder;
    }

    /**
     * @param component
     * @param coder
     */
    private static void generateClassSignature(MatlabComponentC component,
	    JavaCoder coder) {
	// generate the class declaration
	coder.code("public class " + component.getName());
	// componentCoder.genericSignature(component, coder);
	coder.code(" extends CSenseComponent {");
    }

    /**
     * @param component
     * @param coder
     */
    private static void generateImports(MatlabComponentC component,
	    JavaCoder coder) {
	HashSet<String> set = new HashSet<String>();

	coder.code("import java.nio.*;");
	coder.code("import components.basic.MemoryInitialize;");
	// TODO: add only when necessary
	coder.code("import api.*;");
	// coder.code("import messages.Message;");
	coder.code("import " + component.getPackage() + "."
		+ component._swigWrapperFile + ";");

	for (PortC port : component.ports()) {
	    String portPackage = port.getTypeName();
	    if (set.contains(portPackage) == false) {
		coder.code("import " + portPackage + ";");
		set.add(portPackage);
	    }
	}

	coder.newline();
    }

    private static void generateInitialize(MatlabComponentC component,
	    JavaCoder coder) throws CompilerException {
	coder.code("\r@Override\r");
	coder.code("public void onCreate() throws CSenseException {");
	coder.code("super.onCreate();");

	for (MatlabArgument arg : component.allMatlabArguments()) {
	    if (arg instanceof MatlabPersistent) {
		MatlabPersistent param = (MatlabPersistent) arg;
		initialize(param, param.getMatlabType(), null, coder);
	    }
	}

	coder.code("}");
    }

    private static void initialize(MatlabPersistent persistent,
	    MatlabType matlabType, String name, JavaCoder coder)
		    throws CompilerException {
	if (matlabType instanceof MatlabStruct) {
	    MatlabStruct struct = (MatlabStruct) matlabType;
	    for (Iterator<NameValuePair> iter = struct.iterator(); iter
		    .hasNext();) {
		NameValuePair pair = iter.next();
		initialize(persistent, pair.getType(), pair.getName(), coder);
	    }
	} else if (matlabType instanceof MatlabMatrix) {
	    MatlabMatrix matrix = (MatlabMatrix) matlabType;
	    if (matrix.hasZeros() == false) {
		String setter = ".set" + name.substring(0, 1).toUpperCase()
			+ name.substring(1, name.length());
		if (matrix.getNumberOfElements() == 1) {
		    MLDoubleMatrix dm = (MLDoubleMatrix) matrix;
		    coder.code(persistentName(persistent) + setter + "("
			    + dm.getValues()[0] + ");");
		} else {
		    coder.code(persistentName(persistent) + setter
			    + "(MemoryInitialize.ones(" + matrix.getRows()
			    + "," + matrix.getColumns() + "));");
		    // throw new CompilerException("Fix me");
		}

	    }

	} else if (matlabType instanceof MatlabPrimitive) {
	    MatlabPrimitive primitive = (MatlabPrimitive) matlabType;
	    if (primitive.hasZeros() == false) {

	    }
	} else
	    throw new CompilerException(
		    "Failed to generate initialization for component");

    }

    private static void generateDeInitialize(MatlabComponentC component,
	    JavaCoder coder) {
	/*
	 * coder.code("\r@Override\r");
	 * coder.code("public void deinitialize() {"); coder.code("}");
	 */
    }

    /**
     * Generates the doInput method. The limitation of the generator is that it
     * works only when there is a single input.
     * 
     * @param component
     * @param coder
     * @throws CompilerException
     */
    private static void generateDoInput(MatlabComponentC component,
	    JavaCoder coder) throws CompilerException {
	MatlabTypeConverter converter = component.getMatlabTypeCovertert();
	coder.code("\r@Override\r");
	coder.code("public void doInput() throws CSenseException {");

	// make sure that all input ports have data
	coder.comment("check if all inputs have data available");
	for (InputPortC input : component.getInputPorts()) {
	    if (input.supportsPull()) {
		// do nothing
	    } else {
		coder.code("if (" + input.getName()
			+ ".hasMessage() == false) return;");
	    }
	}
	coder.newline();

	// retrieve the inputs
	coder.comment("retrieve pending messages from ports");
	for (InputPortC input : component.getInputPorts()) {

	    String msg_var = input.getName() + "Msg";
	    coder.code(input.getSimpleTypeName() + " " + msg_var + " = "
		    + input.getName() + ".getMessage();");
	    coder.code(msg_var + ".position(0);");
	    if (MatlabOptions.generateAssertions)
		coder.code("assert(" + msg_var + ".capacity() > 0);");
	}
	coder.newline();

	// display the results
	if (MatlabOptions.printPreResults == true) {
	    for (MatlabArgument arg : component.allMatlabArguments()) {
		if (arg instanceof MatlabParameter) {
		    MatlabParameter param = (MatlabParameter) arg;
		    InputPortC input = param.getInputPort();

		    if (input != null) {
			coder.code("System.out.println(\"pre "
				+ component.getName() + "[" + input.getName()
				+ "]:\" + " + input.getName()
				+ "Msg.debugValues());");
		    }
		}
	    }
	    coder.newline();
	}

	// convert the inputs
	coder.comment("convert messages to nio buffers");
	for (MatlabArgument arg : component.allMatlabArguments()) {
	    if (arg instanceof MatlabParameter) {
		MatlabParameter param = (MatlabParameter) arg;
		InputPortC input = param.getInputPort();

		if (input != null) {
		    coder.code(converter.convertInstanceToMatlab(component,
			    param));
		}
	    }
	}
	coder.newline();

	// invoke
	coder.comment("invoke matlab function");
	invokeFunction(component, coder);

	// display the results
	if (MatlabOptions.printPostResults == true) {
	    for (MatlabArgument arg : component.allMatlabArguments()) {
		if (arg instanceof MatlabParameter) {
		    MatlabParameter param = (MatlabParameter) arg;
		    InputPortC input = param.getInputPort();

		    if (input != null) {
			coder.code("System.out.println(\"post "
				+ component.getName() + "[" + input.getName()
				+ "]:\" + " + input.getName()
				+ "Msg.debugValues());");
		    }
		}
	    }
	    coder.newline();
	}

	// push
	coder.comment(" push the results on output ports");
	component.sortMatlabArguments();
	Set<OutputPortC> outputs = new HashSet<OutputPortC>();
	for (MatlabArgument arg : component.allMatlabArguments()) {
	    if (arg instanceof MatlabParameter) {
		MatlabParameter param = (MatlabParameter) arg;
		//if (param.getOutputType() != MatlabArgument.INPUT) {
		if (outputs.contains(param.getOutputPort()) == false) {
		    OutputPortC output = param.getOutputPort();
		    InputPortC input = param.getInputPort();
		    String msg_var = input.getName() + "Msg";

		    if (output != null) {
			coder.code(output.getName() + ".push(" + msg_var + ");");
			outputs.add(output);
		    }
		}
	    }
	}

	coder.code("}");
    }

    private static void invokeFunction(MatlabComponentC component,
	    JavaCoder coder) {
	MatlabParameter returnArg = null;
	String fnDef = component._swigWrapperFile + "."
		+ component.getMatlabFunctionName() + "(";

	List<String> args = new ArrayList<String>();
	for (MatlabArgument arg : component.matlabInputs()) {
	    if ((arg instanceof MatlabParameter)
		    || (arg instanceof MatlabPersistent)) {
		String argName = arg.getName() + "Buf";

		args.add(argName);
	    }
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
		if ((arg.getMatlabType().isPrimitive() == true)
			&& (outputs == 1)) {
		    returnArg = (MatlabParameter) arg;
		} else {
		    String argName = arg.getName() + "Buf";
		    args.add(argName);
		}
	    }
	}

	fnDef = fnDef + Coder.list2string(args) + ");";
	if (returnArg != null) {
	    String returnDef = "double " + returnArg.getName() + "Val";
	    fnDef = returnDef + " = " + fnDef;
	}

	coder.code(fnDef);
	if (returnArg != null) {
	    coder.code(returnArg.getInputPort().getName() + "Msg.put(0, "
		    + returnArg.getName() + "Val);");
	}
	coder.newline();

    }

    /**
     * @param component
     * @param coder
     * @throws CompilerException
     */
    private static void generatePortDeclarations(MatlabComponentC component,
	    JavaCoder coder) throws CompilerException {
	for (InputPortC input : component.getInputPorts()) {
	    coder.code("public IInPort<" + input.getSimpleTypeName() + "> "
		    + input.getName() + " = newInputPort(this, \""
		    + input.getName() + "\");");
	}

	for (OutputPortC output : component.getOutputPorts()) {
	    coder.code("public IOutPort<" + output.getSimpleTypeName() + "> "
		    + output.getName() + " = newOutputPort(this, \""
		    + output.getName() + "\");");
	}

	for (MatlabArgument arg : component.allMatlabArguments()) {
	    if (arg instanceof MatlabPersistent) {
		MatlabPersistent persistent = (MatlabPersistent) arg;
		MatlabType type = arg.getMatlabType();
		String javaType;

		if (type instanceof MatlabStruct) {
		    MatlabStruct struct = (MatlabStruct) arg.getMatlabType();
		    javaType = struct.getCType();
		    coder.code("public " + javaType + " "
			    + persistentName(persistent) + " = new " + javaType
			    + "();");
		} else {
		    throw new CompilerException("fix me");
		}

	    }
	}
    }

    /**
     * @param component
     * @param componentCoder
     * @param coder
     * @param input
     */
    private static void generateConstructor(MatlabComponentC component,
	    JavaCoder coder) {
	ComponentCoder componentCoder = DefaultComponentCoder.getDefaultCoder();

	// generate the constructor
	coder.code("\r//Constructor\r");
	coder.code("public " + component.getName()
		+ componentCoder.argumentSignature(component)
		+ " throws CSenseException {");
	coder.code("super();");
	// == add the input port

	/*
	 * for (InputPortC input : component.inputPorts()) {
	 * coder.code("addPort(" + input.getName() + ");"); }
	 * 
	 * 
	 * for (OutputPortC output : component.outputPorts()) {
	 * coder.code("addPort(" + output.getName() + ");"); }
	 */

	coder.code("}\r");
    }

    private static String persistentName(MatlabPersistent persistent) {
	return persistent.getName() + "Buf";
    }
}
