package components.basic;

import compatibility.Log;

import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Message;
import messages.RawMessage;
import messages.TypeInfo;

/**
 * This component allocates memory when requests it is polled.
 * 
 * @author ochipara
 * 
 * @param <T>
 */
public class MemorySource<T extends RawMessage> extends CSenseSource<T> {
    public IOutPort<T> out = newOutputPort(this, "out");

    public MemorySource(TypeInfo<T> type) throws CSenseException {
	super(type);
	out.setSupportPull(true);
    }

    @Override
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	T msg = getNextMessageToWriteInto();
	if (msg == null) {
	    Log.e("MemorySource", getName(), "out of memory");
	}
	
	return msg;
    }
}
