package edu.uiowa.csense.components.network.desktop;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.CharVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class PhoneId extends CSenseSource<CharVector> {
    public final OutputPort<CharVector> out = newOutputPort(this, "out");
    protected String _androidId;

    public PhoneId() throws CSenseException {
	super(TypeInfo.newCharVector(16));
	_androidId = "desktop";
	out.setSupportPull(true);
    }

    @Override
    public void onStart() throws CSenseException {
	super.onStart();
    }

    @Override
    public Frame onPoll(OutputPort<? extends Frame> port) throws CSenseException {
	CharVector id = getNextMessageToWriteInto();
	id.position(0);
	id.put(_androidId);
	id.flip();
	return id;
    }
}
