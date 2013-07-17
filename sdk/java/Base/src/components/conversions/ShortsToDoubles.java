package components.conversions;

import edu.uiowa.csense.CSenseLib;
import messages.TypeInfo;
import messages.fixed.DoubleVector;
import messages.fixed.ShortVector;
import api.CSenseErrors;
import api.CSenseException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;

public class ShortsToDoubles extends CSenseSource<DoubleVector> {
    public final IInPort<ShortVector> shortsIn = newInputPort(this, "shortIn");
    public final IOutPort<ShortVector> shortsOut = newOutputPort(this, "shortOut");

    public final IOutPort<DoubleVector> doublesOut = newOutputPort(this, "doubleOut");
    private final boolean useNative;

    public ShortsToDoubles(TypeInfo<DoubleVector> type, boolean useNative) throws CSenseException{
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
	DoubleVector doubles = getNextMessageToWriteInto();

	if (shorts.capacity() != doubles.capacity() ) {
	    throw new CSenseException(CSenseErrors.CONFIGURATION_ERROR, "Expected vector capacities to be equal");
	}

	shorts.position(0);
	doubles.position(0);	
	if (useNative) {
	    CSenseLib.int16_to_double(shorts.getBuffer(), doubles.getBuffer(), shorts.capacity());    
	} else {
	    for (int i = 0; i < shorts.capacity(); i++) {
		short s = shorts.getShort();
		double sample = s / 32768.0;
		doubles.put(sample);
	    }
	}



	doubles.position(0);
	shortsOut.push(shorts);
	doublesOut.push(doubles);
    }
}
