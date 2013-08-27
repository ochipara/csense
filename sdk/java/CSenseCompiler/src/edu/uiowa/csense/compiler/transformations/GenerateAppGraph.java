package edu.uiowa.csense.compiler.transformations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.ComponentGraph;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.utils.Coder;

/**
 * This will visualize the component graph. It requires graphviz.
 * 
 * @author ochipara
 * 
 */

// 1: digraph structs {
// 2: node [shape=record];
// 3: struct1 [shape=record,label="<f0> left|<f1> middle|<f2> right"];
// 4: struct2 [shape=record,label="<f0> one|<f1> two"];
// 5: struct3
// [shape=record,label="hello\nworld |{ b |{c|<here> d|e}| f}| g | h"];
// 6: struct1:f1 -> struct2:f0;
// 7: struct1:f2 -> struct3:here;
// 8: }

public class GenerateAppGraph {

    public static void generateAppGraph(CSenseGroupC group, File file) throws CompilerException {
	ComponentGraph graph = group.getComponentGraph();
	DomainManager manager = graph.getDomainManager();

	if (manager.domains().size() == 0) {
	    generateAppGraphNoDomain(group, file);
	} else {
	    generateAppGraphWithDomains(group, file);
	}
    }

    /**
     * 
     * @param graph
     * @throws CompilerException
     */
    public static void generateAppGraphWithDomains(CSenseGroupC main, File out) throws CompilerException {
	ComponentGraph graph = main.getComponentGraph();
	DomainManager manager = graph.getDomainManager();
	String[] colors = new String[] { "black", "red", "blue", "green",
		"grey", "cyan", "aliceblue" };
	// File out = new File(Project.DEPLOY_DIR, out);

	Coder code = new Coder();
	code.append("digraph G {\n");
	code.append("\trankdir=\"LR\";\n");
	code.append("\tranksep=1.2;\n");

	int c = -1;
	// plot each domain
	for (Domain domain : manager.domains()) {
	    // code.append("\tsubgraph " + domain.toString() + " {\n");
	    // code.append("\t\tlabel=\"" + domain.toString() + "\";\n");
	    // code.append("\t\tnode[color=" + colors[c] + "];\n");
	    c += 1;

	    // plot the nodes
	    for (IComponentC node : domain.components()) {
		code.append("\t\t" + node.getVariableName() + "[color="
			+ colors[c] + ",shape = record,label=\"{");

		if (node.getInputPorts().size() > 0) {
		    code.append("{");
		    boolean first = true;
		    for (InputPortC input : node.getInputPorts()) {
			if (first)
			    first = false;
			else {
			    code.append("|");
			}
			String portname = "<in_" + input.getName() + ">";
			BaseTypeC type = input.getType();
			if (type instanceof FrameTypeC) {
			    FrameTypeC frame = (FrameTypeC) type;
			    
			    code.append(portname + " " + input.getName() 
				    + "\\n" + frame.getBaseType() + ":" + frame.getRows() + " x " + frame.getColumns() 
				    + "\\nM:" + frame.getMultipler(Constraint.ROW_DIMENSION) + " x " + frame.getMultipler(Constraint.COLUMN_DIMENSION));
			} else {
			    code.append(portname + " " + input.getName());
			}
		    }
		    code.append("}|");
		}

		code.append(node.getVariableName());

		if (node.getOutputPorts().size() > 0) {
		    code.append("| {");
		    boolean first = true;
		    for (OutputPortC output : node.getOutputPorts()) {
			if (first)
			    first = false;
			else {
			    code.append("|");
			}
			String portname = "<out_" + output.getName() + ">";
			BaseTypeC type = output.getType();
			if (type instanceof FrameTypeC) {
			    FrameTypeC frame = (FrameTypeC) type;
			    code.append(portname + " " + output.getName() 
				    + "\\n" + frame.getBaseType() + ":" + frame.getRows() + " x " + frame.getColumns()
				    + "\\nM:" + frame.getMultipler(Constraint.ROW_DIMENSION) + " x " + frame.getMultipler(Constraint.COLUMN_DIMENSION));
			} else {
			    code.append(portname + " " + output.getName());
			}						
		    }
		    code.append("}");
		}

		code.append("}\"]\n");
	    }
	    // code.append("\t}\n");
	    // code.newline();

	    // plot the links
	    for (IComponentC src : domain.components()) {

		for (OutputPortC output : src.getOutputPorts()) {
		    String outname = "out_" + output.getName() + "";

		    List<InputPortC> nextPorts = output.getOutgoing();
		    InputPortC next = null;
		    if (nextPorts.size() == 0) {
			if (output.isOptional() == false)
			    throw new CompilerException("Invalid Graph");
		    } else if (nextPorts.size() == 1) {
			InputPortC nextPort = output.getOutgoing().get(0);
			IComponentC dst = nextPort.getComponent();

			String inname = "in_" + nextPort.getName() + "";
			code.append("\t\t" + src.getVariableName() + ":"
				+ outname + " -> " + dst.getVariableName()
				+ ":" + inname + ";\n");
		    } else {
			throw new CompilerException("Invalid Graph");
		    }
		}

	    }

	}
	code.append("}");

	try {
	    code.saveToFile(out);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

    public static void generateAppGraphNoDomain(CSenseGroupC group, File out)
	    throws CompilerException {
	ComponentGraph graph = group.getComponentGraph();
	DomainManager manager = graph.getDomainManager();
	String[] colors = new String[] { "black", "red", "blue", "green" };
	// File out = new File(Project.DEPLOY_DIR, file);

	Coder code = new Coder();
	code.append("digraph G {\n");
	code.append("\trankdir=\"LR\";\n");
	code.append("\tranksep=1.2;\n");

	// plot the nodes
	for (IComponentC node : group.getComponents()) {
	    code.append("\t\t" + node.getVariableName()
		    + "[shape = record,label=\"{");

	    if (node.getInputPorts().size() > 0) {
		code.append("{");
		boolean first = true;
		for (InputPortC input : node.getInputPorts()) {
		    if (first)
			first = false;
		    else {
			code.append("|");
		    }
		    String portname = "<in_" + input.getName() + ">";

		    BaseTypeC type = input.getType();
		    if (type instanceof FrameTypeC) {
			FrameTypeC frame = (FrameTypeC) type;
			code.append(portname + " " + input.getName() + "\\n" + frame.getBaseType() + ":" + frame.getRows() + " x " + frame.getColumns());
		    } else {
			code.append(portname + " " + input.getName());
		    }		    
		}
		code.append("}|");
	    }

	    code.append(node.getVariableName());

	    if (node.getOutputPorts().size() > 0) {
		code.append("| {");
		boolean first = true;
		for (OutputPortC output : node.getOutputPorts()) {
		    if (first)
			first = false;
		    else {
			code.append("|");
		    }
		    String portname = "<out_" + output.getName() + ">";

		    BaseTypeC type = output.getType();
		    if (type instanceof FrameTypeC) {
			FrameTypeC frame = (FrameTypeC) type;
			code.append(portname + " " + output.getName() + "\\n" + frame.getBaseType() + ":" + frame.getRows() + " x " + frame.getColumns());
		    } else {
			code.append(portname + " " + output.getName());
		    }
		}
		code.append("}");
	    }

	    code.append("}\"]\n");
	}

	// plot the links
	for (IComponentC src : group.getComponents()) {
	    for (OutputPortC output : src.getOutputPorts()) {
		String outname = "out_" + output.getName() + "";

		try {
		    List<InputPortC> nextPorts = output.getOutgoing();
		    for (InputPortC nextPort: nextPorts) {
			IComponentC dst = nextPort.getComponent();

			String inname = "in_" + nextPort.getName() + "";
			code.append("\t\t" + src.getVariableName() + ":"
				+ outname + " -> " + dst.getVariableName()
				+ ":" + inname + ";\n");
		    }
		    
		    
//		    if (nextPorts.size() == 0) {
//			if (output.isOptional() == false) {
//			    throw new CompilerException("Invalid graph");
//			} else {
//			    // we are okay
//			}
//		    } else if (nextPorts.size() == 1) {
//			InputPortC nextPort = nextPorts.get(0);
//
//			IComponentC dst = nextPort.getComponent();
//
//			String inname = "in_" + nextPort.getName() + "";
//			code.append("\t\t" + src.getVariableName() + ":"
//				+ outname + " -> " + dst.getVariableName()
//				+ ":" + inname + ";\n");
//		    } else {
//			throw new CompilerException("Invalid graph");
//		    }
		} catch (java.lang.IndexOutOfBoundsException e) {
		    System.err.println("Failure to process component " + src);
		    System.err.println("Graph must be incorrect!");
		    throw new CompilerException("Invalid graph");
		}
	    }

	}
	code.append("}");

	try {
	    code.saveToFile(out);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }
}
