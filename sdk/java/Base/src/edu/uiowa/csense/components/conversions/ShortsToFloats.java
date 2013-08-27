package components.conversions;

import edu.uiowa.csense.CSenseLib;
import messages.TypeInfo;
import messages.fixed.FloatVector;
import messages.fixed.ShortVector;
import api.CSenseErrors;
import api.CSenseException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;

public class ShortsToFloats extends CSenseSource<FloatVector> {
    public final IInPort<ShortVector> shortsIn = newInputPort(this, "shortIn");
    public final IOutPort<ShortVector> shortsOut = newOutputPort(this, "shortOut");

    public final IOutPort<FloatVector> floatsOut = newOutputPort(this, "floatsOut");
    private final boolean useNative;


    public ShortsToFloats(TypeInfo<FloatVector> type, boolean useNative) throws CSenseException{
	super(type);
	this.useNative = useNative;
    }

    @Override
    public void onCreate() throws CSenseException {
	super.onCreate();
    }

    @Override
    public void doInput() throws CSenseException {
	ShortVector shorts = shortsIn.getMessage();
	FloatVector floats = getNextMessageToWriteInto();

	if (shorts.capacity() != floats.capacity() ) {
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "Expected vector capacities to be equal");
	}

	shorts.position(0);
	floats.position(0);	
	if (useNative) {
	    CSenseLib.int16_to_floats(shorts.getBuffer(), floats.getBuffer(), shorts.capacity());    
	} else {
	    for (int i = 0; i < shorts.capacity(); i++) {
		short s = shorts.getShort();
		float fs = (float) s;
		float sample = fs / (float) 32768.0;
		floats.put(sample);
	    }
	}


	floats.position(0);
	shortsOut.push(shorts);
	floatsOut.push(floats);
    }
}
