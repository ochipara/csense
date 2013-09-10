package edu.uiowa.csense.components.basic;

import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.profiler.Debug;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Constants;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.Options;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.concurrent.IState;


public class Merge<T extends Frame> extends edu.uiowa.csense.runtime.v4.CSenseComponent {
    public final List<InputPort<T>> inputs = new ArrayList<InputPort<T>>();
    public final OutputPort<T> output = newOutputPort(this, "out");
    protected final int numInputs;

    public Merge(int numInputs) throws CSenseException {
	this.numInputs = numInputs;
	for (int i = 0; i < numInputs; i++) {
	    InputPort<T> input = newInputPort(this, "in" + i);
	    inputs.add(input);
	}
    }

    @Override
    public <T2 extends Frame> int onPush(InputPort<T2> self, Frame frame) throws CSenseException {
	if (Options.CHECK_CURRENT_THREAD) {
	    if (Thread.currentThread() != getScheduler().getThread()) {
		throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR);
	    }
	}
	Debug.logMessageInput(this, frame);
	if (transition(IState.STATE_READY, IState.STATE_RUNNING) == true) {
	    // call the doInput method
	    int r = output.push((T) frame);	    
	    if (r == Constants.PUSH_COLLISON) {
		throw new CSenseException("This should not happen");
	    }

	    self.clear();
	    transitionTo(IState.STATE_READY);
	    return r;
	} else {
	    throw new CSenseException(CSenseError.SYNCHRONIZATION_ERROR, "This should not happen");
	}    
    }
}
