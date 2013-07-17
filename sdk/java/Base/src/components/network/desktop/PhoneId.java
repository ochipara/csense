package components.network.desktop;

import messages.TypeInfo;
import messages.fixed.CharVector;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Message;

public class PhoneId extends CSenseSource<CharVector> {
    public final IOutPort<CharVector> out = newOutputPort(this, "out");
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
    public Message onPoll(IOutPort<? extends Message> port) throws CSenseException {
	CharVector id = getNextMessageToWriteInto();
	id.position(0);
	id.put(_androidId);
	id.flip();
	return id;
    }
}
