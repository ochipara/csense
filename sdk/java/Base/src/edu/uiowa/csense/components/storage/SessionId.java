package edu.uiowa.csense.components.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.CharVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class SessionId extends CSenseSource<CharVector> {
    public OutputPort<CharVector> out = newOutputPort(this, "out");
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
    public Frame onPoll(OutputPort port) throws CSenseException {
	CharVector msg = getNextMessageToWriteInto();
	msg.position(0);
	msg.put(_session);
	msg.flip();
	return msg;
    }

}
