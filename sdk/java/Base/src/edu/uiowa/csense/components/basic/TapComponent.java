package components.basic;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.Message;

/**
 * Responsible for 'freeing' messages that are being recycled in message queues.
 * For example, any message consumed by a source component will not be free for
 * recycling until it reaches a TapComponent.
 * 
 * @author Austin
 */
public class TapComponent<T extends Message> extends CSenseComponent {
    public final IInPort<T> in = newInputPort(this, "in");

    public TapComponent() throws CSenseException {
    }

    /**
     * Free the incoming message.
     * 
     * @param msg
     *            the message to free.
     * @throws CSenseException
     */
    @Override
    public void doInput() throws CSenseException {
	T m = in.getMessage();
    	m.decrementReference();
    }
}
