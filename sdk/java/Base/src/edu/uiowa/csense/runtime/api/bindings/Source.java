package edu.uiowa.csense.runtime.api.bindings;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.ISource;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class Source<T extends Frame> extends Component implements ISource<T> {
    private final CSenseSource<T> source;

    public Source(TypeInfo type) throws CSenseException {
	source = new CSenseSource<T>(type);
    }

    @Override
    public T getNextMessageToWriteInto() {
	return source.getNextMessageToWriteInto();
    }

    @Override
    public T getNextMessageToWriteIntoAndBlock()
	    throws InterruptedException {
	return source.getNextMessageToWriteIntoAndBlock();
    }

    @Override
    public int getAvailableMessages() {
	return source.getAvailableMessages();
    }

}
