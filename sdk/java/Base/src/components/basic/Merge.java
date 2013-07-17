package components.basic;

import java.util.ArrayList;
import java.util.List;

import base.Debug;

import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.IResult;
import api.Message;

public class Merge<T extends Message> extends CSenseComponent {
    public final List<IInPort<T>> inputs = new ArrayList<IInPort<T>>();
    public final IOutPort<T> output = newOutputPort(this, "out");
    protected final int numInputs;
    
    public Merge(int numInputs) throws CSenseException {
	this.numInputs = numInputs;
	for (int i = 0; i < numInputs; i++) {
	    IInPort<T> input = newInputPort(this, "in" + i);
	    inputs.add(input);
	}
    }

    @Override
    public <T2 extends Message> IResult processInput(IInPort<T2> input, T2 m) throws CSenseException {
	if (Thread.currentThread() != getScheduler().getThread()) {
	    throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR);
	}
	Debug.logMessageInput(this, m);
	if (transition(STATE_READY, STATE_RUNNING) == true) {
	    // call the doInput method
	    result = IResult.PUSH_SUCCESS;	    
	    output.push((T) m);

	    // check the result
	    IResult finalResult = onError(result);
	    ready();
	    return finalResult;
	} else {
	    throw new CSenseException(CSenseErrors.SYNCHRONIZATION_ERROR, "This should not happen");
	}
    }
}
