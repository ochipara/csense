package components.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Message;
import messages.TypeInfo;
import messages.fixed.CharVector;

public class SessionId extends CSenseSource<CharVector> {
    public IOutPort<CharVector> out = newOutputPort(this, "out");
    private static String _session;
    private static final int SIZE = 32;

    public SessionId() throws CSenseException {
	super(TypeInfo.newCharVector(SIZE));
	out.setSupportPull(true);
    }

    @Override
    public void onStart() {
	DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");
	Date date = new Date();

	// create the session id
	_session = formatter.format(date);
	if (_session.length() > SIZE) {
	    warn("Session ID buffer is too small");
	    _session = _session.substring(0, SIZE - 1);
	}

	info("Session ID", _session);
    }

    @Override
    public Message onPoll(IOutPort port) throws CSenseException {
	CharVector msg = getNextMessageToWriteInto();
	msg.position(0);
	msg.put(_session);
	msg.flip();
	return msg;
    }

}
