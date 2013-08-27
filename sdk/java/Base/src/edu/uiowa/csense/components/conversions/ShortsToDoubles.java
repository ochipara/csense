package edu.uiowa.csense.components.conversions;

import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;

import edu.uiowa.csense.CSenseLib;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.types.ShortVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class ShortsToDoubles extends CSenseSource<DoubleVector> {
    public final InputPort<ShortVector> shortsIn = newInputPort(this, "shortIn");
    public final OutputPort<ShortVector> shortsOut = newOutputPort(this, "shortOut");

    public final OutputPort<DoubleVector> doublesOut = newOutputPort(this, "doubleOut");
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
    public void onInput() throws CSenseException {
	ShortVector shortsV = shortsIn.getFrame();
	DoubleVector doublesV = getNextMessageToWriteInto();
	ShortBuffer shorts = shortsV.getShortBuffer();
	DoubleBuffer doubles = doublesV.getDoubleBuffer();

	if (shorts.capacity() != doubles.capacity() ) {
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR, "Expected vector capacities to be equal");
	}

	shorts.position(0);
	doubles.position(0);	
	if (useNative) {
	    CSenseLib.int16_to_double(shorts, doubles, shorts.capacity());    
	} else {
	    for (int i = 0; i < shorts.capacity(); i++) {
		short s = shorts.get(i);
		double sample = s / 32768.0;
		doubles.put(sample);
	    }
	}



	doubles.position(0);
	shortsOut.push(shortsV);
	doublesOut.push(doublesV);
    }
}
