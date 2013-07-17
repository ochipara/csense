package compiler.transformations.collapsematlab;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import compiler.model.InputPortC;

import api.IComponentC;

public class ReachableComponent {
    IComponentC _component;
    Set<InputPortC> _reachedInputs = new HashSet<InputPortC>();
    boolean _reachable = false;

    public ReachableComponent(IComponentC component) {
	_component = component;
    }

    public void reachInput(List<InputPortC> inputs) {
	for (InputPortC input : inputs) {
	    reachInput(input);
	}
    }

    public void reachInput(InputPortC input) {
	_reachedInputs.add(input);
    }

    public boolean allInputsReached() {
	for (InputPortC in : _component.getInputPorts()) {
	    if (_reachedInputs.contains(in) == false) {
		return false;
	    }
	}

	return true;
    }

    public IComponentC component() {
	return _component;
    }

    @Override
    public String toString() {
	return _component.getVariableName();
    }

}
