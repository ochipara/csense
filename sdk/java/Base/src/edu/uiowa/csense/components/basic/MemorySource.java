package edu.uiowa.csense.components.basic;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.bindings.Source;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.types.TypeInfo;

/**
 * This component allocates memory when requests it is polled.
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class MemorySource<T extends Frame> extends Source<T> {
    public OutputPort<T> out = newOutputPort(this, "out");

    public MemorySource(TypeInfo type) throws CSenseException {
	super(type);
	out.setSupportPull(true);
    }

    @Override
    public Frame onPoll(OutputPort<? extends Frame> port) {
	Frame msg = getNextMessageToWriteInto();
	if (msg == null) {
	    Log.e("MemorySource", getName(), "out of memory");
	}
	
	return msg;
    }
}
