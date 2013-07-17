package api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import compiler.CompilerException;
import compiler.CSenseComponentC.ThreadingOption;
import compiler.model.ArgumentC;
import compiler.model.ComponentCoder;
import compiler.model.Domain;
import compiler.model.InputPortC;
import compiler.model.OutputPortC;
import compiler.model.PortC;
import compiler.types.BaseTypeC;



public interface IComponentC {
    public String getName(); // TODO: rename to getClassName

    public Class getComponent(); // TODO

    public void setVariableName(String groupName);

    public String getVariableName();

    // port management
    public InputPortC addInputPort(BaseTypeC portType, String name)
	    throws CompilerException;

    public InputPortC addInputPort(BaseTypeC portType, String name,
	    boolean isOptional) throws CompilerException;

    public OutputPortC addOutputPort(BaseTypeC portType, String name)
	    throws CompilerException;

    public OutputPortC addOutputPort(BaseTypeC portType, String name,
	    boolean isOptional) throws CompilerException;

    // access to ports
    public List<OutputPortC> getOutputPorts();

    public List<InputPortC> getInputPorts();

    public Collection<PortC> ports();

    public InputPortC getInputPort(String portName) throws CompilerException;

    public OutputPortC getOutputPort(String portName) throws CompilerException;

    // type management
    public boolean hasGenericTypes();

    // argument management
    public Iterator<ArgumentC> arguments();

    // permission management
    public List<String> getPermission();

    // thread options management
    public ThreadingOption getThreadType();

    public void setDomain(Domain domain);

    public Domain getDomain();

    // used during code generation
    public ComponentCoder getCoder();

    public void setCoder(ComponentCoder coder);

    public Iterator<BaseTypeC> genericTypeIterator();

    // internal management
    public NextComponentIterator nextComponents();

    public PreviousComponentIterator prevComponents();

    public List<InputPortC> getInputPortsFrom(IComponentC next);

    public List<OutputPortC> getOutputsPortsTo(IComponentC component)
	    throws CompilerException;

    public boolean isSource();

    public void removeOutput(OutputPortC output);

    // public void addNextComponent(IComponentC destComp);
    // public void addPrevComponent(IComponentC sourceComp)
    // public List<IComponentC> prevComponents();
}
