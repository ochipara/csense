package edu.uiowa.csense.components.basic;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;

/**
 * Responsible for 'freeing' messages that are being recycled in message queues.
 * For example, any message consumed by a source component will not be free for
 * recycling until it reaches a TapComponent.
 * 
 * @author Austin
 */
public class TapComponent<T extends Frame> extends edu.uiowa.csense.runtime.v4.CSenseComponent {
    public final InputPort<T> in = newInputPort(this, "in");

    public TapComponent() throws CSenseException {
	super();
    }

    /**
     * Free the incoming message.
     * 
     * @param msg
     *            the message to free.
     * @throws CSenseException
     */
    @Override
    public void onInput() throws CSenseException {
	T m = in.getFrame();
    	m.decrementReference();
    }
}
