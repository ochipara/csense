package components.basic;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import messages.fixed.Vector;

public class Slice<T extends Vector> extends CSenseComponent {
    public IInPort<T> input = newInputPort(this, "dataIn");
    public IOutPort<T> output = newOutputPort(this, "dataOut");
    
    public final int _lower;
    public final int _upper;
    
    public Slice(int lower, int upper) throws CSenseException {
	super();
	_lower = lower;
	_upper = upper;	
    }
    
    @Override
    public void doInput() throws CSenseException {
	T m = input.getMessage();
	m.slice(_lower, _upper);
	
	output.push(m);
    }
}
