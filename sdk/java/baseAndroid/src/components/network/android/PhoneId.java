package components.network.android;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.CharVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import android.content.Context;
import android.provider.Settings.Secure;

public class PhoneId extends CSenseSource<CharVector> {
	public final OutputPort<CharVector> out = newOutputPort(this, "out");
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
	public void onPoll(OutputPort<? extends Frame> port) throws CSenseException {
		CharVector id = getNextMessageToWriteInto();
		id.position(0);
		id.put(_androidId);
		id.flip();
		out.push(id);
	}
}
