package compiler.model;

import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

import compiler.CompilerException;
import compiler.types.BaseTypeC;
import components.basic.TapComponentC;

import api.IComponentC;
import api.IComponentGraph;

/**
 * In this class, we maintain the component graph. It will store the relevant
 * information for generating code
 * 
 * @author ochipara
 * 
 */
public class ComponentGraph implements IComponentGraph {
    protected final HashMap<String, IComponentC> _components = new HashMap<String, IComponentC>();
    protected final DomainManager _domains = DomainManager.domainManager();
//    protected final HashMap<String, Integer> _tapCounter = new HashMap<String, Integer>();
    protected final Logger logger;

    public ComponentGraph(String name) {
	logger = Logger.getLogger(name + "Graph");
    }

    @Override
    public void addComponent(String variableName, IComponentC component) throws CompilerException {
	if (this._components.containsKey(variableName) == false) {
	    component.setVariableName(variableName);
	    this._components.put(variableName, component);
	} else {
	    throw new CompilerException("Duplicate component name " + variableName);
	}
    }

    public IComponentC getComponent(String name) throws CompilerException {
	if (_components.containsKey(name) == false)
	    throw new CompilerException("Could not find component [" + name
		    + "]");
	return _components.get(name);
    }

    @Override
    public Collection<IComponentC> components() {
	return _components.values();
    }


    public void toTap(String component, BaseTypeC type) throws CompilerException {
	String typeName = type.getSimpleName();
	
	int count = 0;
	 String componentName;
	do {
	    componentName = "tap" + typeName + count;
	    count += 1;
	} while(hasComponent(componentName));	

	TapComponentC newTap = new TapComponentC(type);
	addComponent(componentName, newTap);
	link(component, componentName);
//	_tapCounter.put(typeName, count);
    }

    public Domain newDomain() {
	return _domains.newDomain();
    }

    @Override
    public void link(String src, String dst) throws CompilerException {
	String[] src_link = src.split("::");
	String[] dst_link = dst.split("::");

	IComponentC srcComponent = getComponent(src_link[0]);
	OutputPortC srcPort;
	if (src_link.length == 1) {
	    if (srcComponent.getOutputPorts().size() == 1) {
		srcPort = srcComponent.getOutputPorts().get(0);
	    } else {
		throw new CompilerException(
			"Could not find identify component and port specified by "
				+ src);
	    }
	} else if (src_link.length == 2) {
	    srcPort = srcComponent.getOutputPort(src_link[1]);
	} else {
	    throw new CompilerException(
		    "Could not find identify component and port specified by "
			    + src);
	}

	IComponentC dstComponentC = getComponent(dst_link[0]);
	InputPortC dstPort;
	if (dst_link.length == 1) {
	    if (dstComponentC.getInputPorts().size() == 1) {
		dstPort = dstComponentC.getInputPorts().get(0);
	    } else {
		throw new CompilerException(
			"Could not find identify component and port specified by "
				+ dst);
	    }
	} else if (dst_link.length == 2) {
	    dstPort = dstComponentC.getInputPort(dst_link[1]);
	} else {
	    throw new CompilerException(
		    "Could not find identify component and port specified by "
			    + dst);
	}

	logger.debug("Add link " + srcPort + " to " + dstPort);
	link(srcPort, dstPort);
    }

    @Override
    public void link(OutputPortC source, InputPortC destination) {
	source.addOutgoing(destination);
	destination.addIncoming(source);
    }

    public void relink(OutputPortC out, InputPortC prev, InputPortC next) {
	prev.removeIncoming();
	out.removeOutgoingLink(prev);
	link(out, next);
    }

    public void relink(InputPortC in, OutputPortC prev, OutputPortC next) {
	prev.removeOutgoingLink(in);
	in.removeIncoming();
	link(next, in);
    }

    public void display() {
	for (IComponentC component : _components.values()) {
	    System.out.println(component.toString());
	}
    }

    public DomainManager getDomainManager() {
	return _domains;
    }

    public void removeComponent(IComponentC component) {
	_components.remove(component.getVariableName());
    }

    public void removeComponentAndClean(IComponentC component) {
	_components.remove(component.getVariableName());
	for (InputPortC input : component.getInputPorts()) {
	    OutputPortC output = input.getIncoming();
	    if (output != null)
		output.removeOutgoing();
	}

	for (OutputPortC output : component.getOutputPorts()) {
	    for (InputPortC input : output.getOutgoing()) {
		input.removeIncoming();
	    }
	}
    }

    public boolean hasComponent(String variableName) {
	return _components.containsKey(variableName);
    }

}
