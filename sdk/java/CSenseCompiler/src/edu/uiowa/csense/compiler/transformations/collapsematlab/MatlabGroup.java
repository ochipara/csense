package edu.uiowa.csense.compiler.transformations.collapsematlab;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.model.InputPortC;
import edu.uiowa.csense.compiler.model.OutputPortC;
import edu.uiowa.csense.compiler.model.api.IComponentC;

public class MatlabGroup {
    Set<IComponentC> _matlabGroup = new HashSet<IComponentC>();
    Set<OutputPortC> _out = new HashSet<OutputPortC>();
    Set<InputPortC> _in = new HashSet<InputPortC>();
    Set<InputPortC> _internalIn = new HashSet<InputPortC>();
    String _name = null;
    HashMap<InputPortC, InputPortC> _inputOld2New = new HashMap<InputPortC, InputPortC>();
    HashMap<InputPortC, InputPortC> _inputNew2Old = new HashMap<InputPortC, InputPortC>();
    HashMap<OutputPortC, OutputPortC> _outputOld2New = new HashMap<OutputPortC, OutputPortC>();
    HashMap<OutputPortC, OutputPortC> _outputNew2Old = new HashMap<OutputPortC, OutputPortC>();

    public MatlabGroup() {
    }

    public boolean addComponent(IComponentC component) {
	if (_matlabGroup.contains(component))
	    return false;
	_matlabGroup.add(component);
	return true;
    }

    public boolean containsComponent(MatlabComponentC componet) {
	return _matlabGroup.contains(componet);
    }

    public Collection<IComponentC> components() {
	return _matlabGroup;
    }

    public int size() {
	return _matlabGroup.size();
    }

    public void addOutput(OutputPortC outPort) {
	_out.add(outPort);
    }

    public Collection<OutputPortC> outputPorts() {
	return _out;
    }

    public void addAllInputs(List<InputPortC> inputs) {
	_in.addAll(inputs);
    }

    public void addAllOutputs(List<OutputPortC> outputs) {
	_out.addAll(outputs);
    }

    public void addInput(InputPortC prev) {
	_in.add(prev);
    }

    public Collection<InputPortC> inputPorts() {
	return _in;
    }

    public String displayMembers() {
	StringBuffer sb = new StringBuffer();
	sb.append("members: ");
	for (IComponentC m : _matlabGroup)
	    sb.append(m.getVariableName() + " ");

	return sb.toString();
    }

    public String displayIn() {
	StringBuffer sb = new StringBuffer();
	sb.append("in: ");
	for (InputPortC m : _in)
	    sb.append(m.toString() + " ");

	return sb.toString();
    }

    public String displayOut() {
	StringBuffer sb = new StringBuffer();
	sb.append("out: ");
	for (OutputPortC m : _out)
	    sb.append(m.toString() + " ");

	return sb.toString();
    }

    @Override
    public String toString() {
	return displayMembers();
    }

    public void addAll(List<MatlabComponentC> toAddComponents) {
	_matlabGroup.addAll(toAddComponents);
    }

    public boolean contains(IComponentC next) {
	return _matlabGroup.contains(next);
    }

    public void setName(String name) {
	_name = name;
    }

    public String getName() {
	return _name;
    }

    public boolean isInternal(IComponentC component) {
	for (IComponentC next : component.nextComponents()) {
	    if (_matlabGroup.contains(next) == false) {
		return false;
	    }
	}

	for (IComponentC prev : component.prevComponents()) {
	    if (_matlabGroup.contains(prev) == false) {
		return false;
	    }
	}

	return true;
    }

    public void mapInput(InputPortC oldInput, InputPortC newInput) {
	if (oldInput == null)
	    throw new IllegalArgumentException();
	if (newInput == null)
	    throw new IllegalArgumentException();

	_in.add(oldInput);
	_inputOld2New.put(oldInput, newInput);
	_inputNew2Old.put(newInput, oldInput);
    }

    public void mapOutput(OutputPortC oldOutput, OutputPortC newOutput) {
	if (oldOutput == null)
	    throw new IllegalArgumentException();
	if (newOutput == null)
	    throw new IllegalArgumentException();

	_out.add(oldOutput);
	_outputOld2New.put(oldOutput, newOutput);
	_outputNew2Old.put(newOutput, oldOutput);
    }

    public Map<InputPortC, InputPortC> mapOld2NewInput() {
	return _inputOld2New;
    }

    public Map<InputPortC, InputPortC> mapNew2OldInput() {
	return _inputNew2Old;
    }

    public Map<OutputPortC, OutputPortC> mapOld2NewOutput() {
	return _outputOld2New;
    }

    public Map<OutputPortC, OutputPortC> mapNew2OldOutput() {
	return _outputNew2Old;
    }

    public void addInternalInput(InputPortC originalInput) {
	_internalIn.add(originalInput);
    }

    public Collection<? extends InputPortC> internalInputPorts() {
	return _internalIn;
    }

}
