package edu.uiowa.csense.components.basic;

import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.bindings.Component;


/**
 * This is an abstract implementation of the blocking queue.
 * Different implementations are allowable
 */
public abstract class SyncQueue<T extends Frame> extends Component {
}
