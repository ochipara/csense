package components.basic;

import java.util.ArrayList;
import java.util.List;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.Message;

/**
 * This component is used to split the message onto multiple paths. It does
 * bookkeeping on the number of references to each message before being able to
 * dispose of them
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class CopyRefComponent<T extends Message> extends CSenseComponent {
    public final IInPort<T> in = newInputPort(this, "in");
    public final List<IOutPort<T>> outs = new ArrayList<IOutPort<T>>();
    public final int _numOutputs;

    public CopyRefComponent(int numOutputs) throws CSenseException {
	super();
	_numOutputs = numOutputs;

	for (int i = 0; i < numOutputs; i++) {
	    IOutPort<T> out = newOutputPort(this, "out" + i);
	    outs.add(out);
	}
    }

    @Override
    public void doInput() throws CSenseException {
	T m = in.getMessage();

	for (int i = 0; i < _numOutputs - 1; i++)
	    m.incrementReference();

	// start pushing the values
	for (int i = 0; i < _numOutputs; i++) {
	    outs.get(i).push(m);
	}
    }

}
