package compiler.model;

import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.matlab.MatlabComponentC;
import compiler.types.BaseTypeC;


import api.GroupC;
import api.IComponentC;

public abstract class CSenseGroupC extends CSenseComponentC implements GroupC {
    /**
     * Wraps multiple components in a single group For each input/output port of
     * the group, there is a matched internal port that is used
     * 
     */
    protected final ComponentGraph _graph;
    protected boolean _main = false;
    protected Logger logger;
    protected HashMap<String, InputPortC> internalInput = new HashMap<String, InputPortC>();
    protected HashMap<String, OutputPortC> internalOutput = new HashMap<String, OutputPortC>();

    public CSenseGroupC(String groupName) throws CompilerException {
	this(groupName, false);
    }

    public CSenseGroupC(String groupName, boolean main)
	    throws CompilerException {
	super();
	logger = Logger.getLogger(groupName + "Group");
	_graph = new ComponentGraph(groupName);
	_main = main;
    }

    @Override
    public OutputPortC addOutputPort(BaseTypeC portType, String name)
	    throws CompilerException {
	internalInput.put(name, new InputPortC(this, portType, name));
	return super.addOutputPort(portType, name);
    }

    @Override
    public InputPortC addInputPort(BaseTypeC portType, String name)
	    throws CompilerException {
	internalOutput.put(name, new OutputPortC(this, portType, name));
	return super.addInputPort(portType, name);
    }

    @Override
    public void link(String src, String dst) throws CompilerException {
	String[] src_link = src.split("::");
	String[] dst_link = dst.split("::");

	IComponentC srcComponent;
	OutputPortC srcPort = null;
	if (src_link.length == 1) {
	    srcComponent = getComponent(src_link[0]);
	    if (srcComponent.getOutputPorts().size() == 1) {
		srcPort = srcComponent.getOutputPorts().get(0);
	    } else {
		// srcPort = srcComponent.getOutputPort(src_link[1]);
		// if (srcPort == null)
		throw new CompilerException(
			"Component "
				+ src
				+ " has multiple ports. You must explictly link them using component::port syntax.");
	    }
	} else if (src_link.length == 2) {
	    if (src_link[0].length() != 0) {
		srcComponent = getComponent(src_link[0]);
		srcPort = srcComponent.getOutputPort(src_link[1]);
	    } else {
		srcComponent = this;
		srcPort = internalOutput.get(src_link[1]);
	    }

	    if (srcPort == null)
		throw new CompilerException(
			"Could not find identify component and port specified by "
				+ src);

	} else {
	    throw new CompilerException(
		    "Could not find identify component and port specified by "
			    + src);
	}

	IComponentC dstComponentC;
	InputPortC dstPort = null;
	if (dst_link.length == 1) {
	    dstComponentC = getComponent(dst_link[0]);
	    if (dstComponentC.getInputPorts().size() == 1) {
		dstPort = dstComponentC.getInputPorts().get(0);
	    } else {
		try {
		    dstPort = dstComponentC.getInputPorts().get(0);
		    if (dstPort == null)
			throw new CompilerException(
				"Could not find identify component and port specified by "
					+ dst);
		} catch (IndexOutOfBoundsException e) {
		    throw new CompilerException("Conponent " + dstComponentC.getName() + " has not ports");
		}
	    }
	} else if (dst_link.length == 2) {
	    if (dst_link[0].length() != 0) {
		dstComponentC = getComponent(dst_link[0]);
		dstPort = dstComponentC.getInputPort(dst_link[1]);
	    } else {
		dstComponentC = this;
		dstPort = internalInput.get(dst_link[1]);
	    }

	    if (dstPort == null)
		throw new CompilerException(
			"Could not find identify component and port specified by "
				+ dst);
	} else {
	    throw new CompilerException(
		    "Could not find identify component and port specified by "
			    + dst);
	}

	logger.debug("Add link " + srcPort + " to " + dstPort);
	connect(srcPort, dstPort);
    }

    public void addComponent(String variableName, IComponentC component) throws CompilerException {
	String newName = variableName;
	if (_graph.hasComponent(variableName)) {
	    int count = 0;
	    do {
		newName = variableName + count;
		count = count + 1;
	    } while (_graph.hasComponent(newName));
	}

	_graph.addComponent(newName, component);
    }

    @Override
    public IComponentC getComponent(String name) throws CompilerException {
	return _graph.getComponent(name);
    }

    public void connect(OutputPortC source, InputPortC destination) {
	_graph.link(source, destination);
    }

    public void toTap(String component, BaseTypeC type)
	    throws CompilerException {
	_graph.toTap(component, type);
    }

    @Override
    public Collection<IComponentC> getComponents() {
	return _graph.components();
    }

    @Override
    public abstract void instantiate() throws CompilerException;

    @Override
    public void validate() throws CompilerException {
	throw new CompilerException("Groups does not implement validate()");
    }

    public ComponentGraph getComponentGraph() {
	return _graph;
    }

    /*
    public void addType(BaseTypeC type) {
	_graph.addType(type);
    }*/

    public boolean isMain() {
	return _main;
    }

    public OutputPortC internalOutputPort(String name) {
	return internalOutput.get(name);
    }

    public InputPortC internalInputPort(String name) {
	return internalInput.get(name);
    }

    public Collection<OutputPortC> internalOutputPorts() {
	return internalOutput.values();
    }

    public boolean isMatlabGroup() {
	for (IComponentC component : _graph.components()) {
	    if (component instanceof MatlabComponentC == false) {
		return false;
	    }
	}
	return true;
    }
}
