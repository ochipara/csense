package compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler.model.ArgumentC;
import compiler.model.ComponentCoder;
import compiler.model.DefaultComponentCoder;
import compiler.model.Domain;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.model.PortC;
import compiler.model.Project;
import compiler.types.BaseTypeC;
import compiler.types.FrameTypeC;

import api.IComponentC;
import api.NextComponentIterator;
import api.PreviousComponentIterator;

public class CSenseComponentC implements IComponentC {
    // the input and output ports
    public List<InputPortC> in = new ArrayList<InputPortC>();
    public List<OutputPortC> out = new ArrayList<OutputPortC>();
    public Map<String, PortC> ports = new HashMap<String, PortC>();

    // the name of the variable
    protected String _variableName = null;

    // the class of the actual EgoComponent
    protected Class _componentClass = null;

    // the component name is the actual EgoSense component to be used
    // if the name remains unspecified within the *C object, then it will be
    // automatically set by removing the C from the
    // end of the object (e.g., AudioComponentC => AudioComponent)
    protected String _componentName = null;
    protected String _packageName = "base";
    protected String _sourceType = null;

    // this is used to store the generic arguments
    protected List<BaseTypeC> _genericTypes = new ArrayList<BaseTypeC>();

    // this is used to store the invokation parameters
    protected List<ArgumentC> _arguments = new ArrayList<ArgumentC>();

    // the arguments the actual (non-meta data) component needs when it is
    // instantiated.
    protected List<String> _userPermissions = new ArrayList<String>();

    // manages the threading and execution domain of the components
    public enum ThreadingOption {
	ANDROID, CSENSE, NONE
    };

    protected ThreadingOption _threading = ThreadingOption.NONE;
    protected Domain _domain = null;

    // this will be used to guide the code generation for each component
    // by putting a code generator per component, we can customize this as we
    // want
    protected ComponentCoder _coder = DefaultComponentCoder.getDefaultCoder();
    private String _qualifiedName;

    public CSenseComponentC(Class csenseComponent) {
	setComponent(csenseComponent);
    }

    public CSenseComponentC(String qualifiedName) {
	setComponent(qualifiedName);
    }

    public CSenseComponentC() {
	_componentClass = null;
	_componentName = null;
	_qualifiedName = null;
    }

    public void setComponent(Class csenseComponent) {
	_componentClass = csenseComponent;
	_componentName = csenseComponent.getName();
	_qualifiedName = csenseComponent.getCanonicalName();
    }

    public void setComponent(String qualifiedName) {
	_componentName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.length());
	_qualifiedName = qualifiedName;
    }

    @Override
    public ComponentCoder getCoder() {
	return _coder;
    }

    public String getQualifiedName() {
	return _qualifiedName;
    }

    public int getNumInputs() {
	return in.size();
    }

    public int getNumOutputs() {
	return out.size();
    }

    @Override
    public List<String> getPermission() {
	return _userPermissions;
    }

    public void addPermission(String permission) {
	_userPermissions.add(permission);

    }

    @Override
    public String getVariableName() {
	return _variableName;
    }

    @Override
    public void setVariableName(String variableName) {
	_variableName = variableName;
    }

    @Override
    public String getName() {
	return _componentName;
    }

    public String getPackage() {
	return _packageName;
    }

    /**
     * Adds an output port to the current component
     * 
     * @param port
     *            - the output port to be added
     * @return the passed port
     * @throws CompilerException
     */
    protected OutputPortC addOutputPort(OutputPortC port) throws CompilerException {
	out.add(port);
	if (ports.containsKey(port.getName()))
	    throw new CompilerException("Port names cannot be duplicated ["
		    + port.getName() + "] on component [" + getVariableName()
		    + "]");
	ports.put(port.getName(), port);
	return port;
    }

    @Override
    public OutputPortC addOutputPort(BaseTypeC portType, String name) throws CompilerException {
	return addOutputPort(new OutputPortC(this, portType, name));
    }

    @Override
    public OutputPortC addOutputPort(BaseTypeC portType, String name,
	    boolean optional) throws CompilerException {
	OutputPortC out = addOutputPort(new OutputPortC(this, portType, name));
	out.setOptional(optional);
	return out;
    }

    protected void addGenericType(BaseTypeC messageType) {
	_genericTypes.add(messageType);
    }

    protected void addArgument(ArgumentC argument) {
	_arguments.add(argument);
    }

    protected void addTypeInfoArgument(BaseTypeC portType2) {
	FrameTypeC portType = (FrameTypeC) portType2;
	String v = "new TypeInfo<" + portType.getMessageType().getSimpleName()
		+ ">(" + portType.getMessageType().getSimpleName() + ".class"
		+ ", " + portType.getElementSize() + ", " + portType.getRows()
		+ ", " + portType.getColumns() + ", true, false)";
	_arguments.add(new ArgumentC(BaseTypeC.class, v));
    }

    @Override
    public Iterator<BaseTypeC> genericTypeIterator() {
	return _genericTypes.iterator();
    }

    @Override
    public boolean hasGenericTypes() {
	return _genericTypes.size() > 0;
    }

    /**
     * Adds an input port to the current component
     * 
     * @param port
     *            - the input port to be added
     * @return the passed port
     * @throws CompilerException
     */
    private InputPortC addInputPort(InputPortC port) throws CompilerException {
	in.add(port);
	if (ports.containsKey(port.getName()))
	    throw new CompilerException("Port names cannot be duplicated");
	ports.put(port.getName(), port);
	return port;
    }

    @Override
    public InputPortC addInputPort(BaseTypeC portType, String name)
	    throws CompilerException {
	return addInputPort(new InputPortC(this, portType, name));
    }

    public void addIOPort(BaseTypeC portType, String name) throws CompilerException {
	InputPortC in = addInputPort(portType, name + "In");
	OutputPortC out = addOutputPort(portType, name + "Out");
	in.setInternalOutput(out);
	out.setInternalInput(in);
    }

    @Override
    public InputPortC addInputPort(BaseTypeC portType, String name,
	    boolean optional) throws CompilerException {
	InputPortC in = addInputPort(new InputPortC(this, portType, name));
	in.setOptional(optional);
	return in;
    }

    @Override
    public InputPortC getInputPort(String portName) throws CompilerException {
	for (Iterator<InputPortC> iter = in.iterator(); iter.hasNext();) {
	    InputPortC port = iter.next();

	    if (portName.compareTo(port.getName()) == 0)
		return port;
	}

	throw new CompilerException("Could not find input port [" + portName
		+ "] on component [" + getName() + "]");
    }

    @Override
    public OutputPortC getOutputPort(String portName) throws CompilerException {
	for (Iterator<OutputPortC> iter = out.iterator(); iter.hasNext();) {
	    OutputPortC port = iter.next();

	    if (portName.compareTo(port.getName()) == 0)
		return port;
	}

	throw new CompilerException("Could not find input port " + portName
		+ " on component " + this.toString());
    }

    public OutputPortC getOutputPort(int index) {
	return out.get(index);
    }

    @Override
    public List<OutputPortC> getOutputPorts() {
	return out;
    }

    @Override
    public List<InputPortC> getInputPorts() {
	return in;
    }

    @Override
    public Collection<PortC> ports() {
	return ports.values();
    }

    @Override
    public Class getComponent() {
	return _componentClass;
    }

    public InputPortC getInputPort(int i) {
	return in.get(i);
    }

    @Override
    public Iterator<ArgumentC> arguments() {
	return _arguments.iterator();
    }

    @Override
    public ThreadingOption getThreadType() {
	return _threading;
    }

    public void setThreadingOption(ThreadingOption threading) {
	_threading = threading;
    }

    @Override
    public void setDomain(Domain domain) {
	_domain = domain;
    }

    @Override
    public Domain getDomain() {
	return _domain;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(getVariableName() + " " + getClass().getName() + " IN["
		+ getNumInputs() + "] OUT[" + getNumOutputs() + "]");

	return sb.toString();
    }

    @Override
    public NextComponentIterator nextComponents() {
	return new NextComponentIterator(this);
    }

    @Override
    public PreviousComponentIterator prevComponents() {
	return new PreviousComponentIterator(this);
    }

    @Override
    public void setCoder(ComponentCoder coder) {
	_coder = coder;
    }

    @Override
    public List<InputPortC> getInputPortsFrom(IComponentC next) {
	List<InputPortC> inputs = new LinkedList<InputPortC>();
	for (InputPortC input : in) {
	    if (input.getIncoming().getComponent() == next) {
		inputs.add(input);
	    }
	}

	return inputs;
    }

    @Override
    public List<OutputPortC> getOutputsPortsTo(IComponentC component)
	    throws CompilerException {
	List<OutputPortC> outputs = new LinkedList<OutputPortC>();
	for (OutputPortC output : out) {
	    InputPortC ins = output.getSingleOutgoing();
	    if (ins.getComponent() == component)
		outputs.add(output);
	}

	return outputs;
    }

    @Override
    public boolean isSource() {
	if (getOutputPorts().size() == 0) return false;
	
	boolean r = true;
	for (OutputPortC output : out) {
	    r = false;
	    if (output.getInternalInput() == null) {
		return true;
	    }
	}
	
	return r;
    }

    @Override
    public void removeOutput(OutputPortC output) {
	out.remove(output);
    }
    
    public void addResource(Class cls) throws CompilerException {
	Project.getProject().getResourceManager().addClass(cls);
    }
    
    public void addResource(String cls) throws CompilerException {
  	Project.getProject().getResourceManager().addClass(cls);
      }
}
