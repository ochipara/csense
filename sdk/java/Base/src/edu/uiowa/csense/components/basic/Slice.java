package edu.uiowa.csense.components.basic;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.Vector;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

public class Slice<T extends Vector> extends CSenseComponent {
    public InputPort<T> input = newInputPort(this, "dataIn");
    public OutputPort<T> output = newOutputPort(this, "dataOut");
    
    public final int _lower;
    public final int _upper;
    
    public Slice(int lower, int upper) throws CSenseException {
	super();
	_lower = lower;
	_upper = upper;	
    }
    
    @Override
    public void onInput() throws CSenseException {
	T m = input.getFrame();
	m.slice(_lower, _upper);
	
	output.push(m);
    }
}
