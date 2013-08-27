package edu.uiowa.csense.components.basic;

import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.bindings.Component;

/**
 * This component is used to split the message onto multiple paths. It does
 * bookkeeping on the number of references to each message before being able to
 * dispose of them
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class CopyRefComponent<T extends Frame> extends Component {
    public final InputPort<T> in = newInputPort(this, "in");
    public final List<OutputPort<T>> outs = new ArrayList<OutputPort<T>>();
    public final int _numOutputs;

    public CopyRefComponent(int numOutputs) throws CSenseException {
	super();
	_numOutputs = numOutputs;

	for (int i = 0; i < numOutputs; i++) {
	    OutputPort<T> out = newOutputPort(this, "out" + i);
	    outs.add(out);
	}
    }

    @Override
    public void onInput() throws CSenseException {
	T m = in.getFrame();

	for (int i = 0; i < _numOutputs - 1; i++)
	    m.incrementReference();

	// start pushing the values
	for (int i = 0; i < _numOutputs; i++) {
	    outs.get(i).push(m);
	}
    }

}
