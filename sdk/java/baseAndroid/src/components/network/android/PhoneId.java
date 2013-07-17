package components.network.android;

import messages.TypeInfo;
import messages.fixed.CharVector;
import android.content.Context;
import android.provider.Settings.Secure;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.Message;

public class PhoneId extends CSenseSource<CharVector> {
	public final IOutPort<CharVector> out = newOutputPort(this, "out");
	protected final Context _context;
	protected String _androidId;
	
	public PhoneId(Context context) throws CSenseException {
		super(TypeInfo.newCharVector(16));
		_context = context;
		_androidId = Secure.getString(_context.getContentResolver(),
                Secure.ANDROID_ID); 
		out.setSupportPull(true);
	}
	
	@Override
	public void onStart() throws CSenseException {
		super.onStart();
	}

	@Override
	public void onPoll(IOutPort<? extends Message> port) throws CSenseException {
		CharVector id = getNextMessageToWriteInto();
		id.position(0);
		id.put(_androidId);
		id.flip();
		out.push(id);
	}
}
