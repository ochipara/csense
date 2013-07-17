package components.basic;

import api.CSenseComponent;
import api.Message;


/**
 * This is an abstract implementation of the blocking queue.
 * Different implementations are allowable
 */
public abstract class SyncQueue<T extends Message> extends CSenseComponent {
}
