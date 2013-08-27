package edu.uiowa.csense.components.conversions;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import edu.uiowa.csense.CSenseLib;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.FloatVector;
import edu.uiowa.csense.runtime.types.ShortVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class ShortsToFloats extends CSenseSource<FloatVector> {
    public final InputPort<ShortVector> shortsIn = newInputPort(this, "shortIn");
    public final OutputPort<ShortVector> shortsOut = newOutputPort(this, "shortOut");

    public final OutputPort<FloatVector> floatsOut = newOutputPort(this, "floatsOut");
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
    public void onInput() throws CSenseException {
	ShortVector shortsV = shortsIn.getFrame();
	ShortBuffer shorts = shortsV.getShortBuffer();
	FloatVector floatsV = getNextMessageToWriteInto();
	FloatBuffer floats = floatsV.getFloatBuffer();

	if (shorts.capacity() != floats.capacity() ) {
	    throw new CSenseException(CSenseError.CONFIGURATION_ERROR, "Expected vector capacities to be equal");
	}

	shorts.position(0);
	floats.position(0);	
	if (useNative) {
	    CSenseLib.int16_to_floats(shorts, floats, shorts.capacity());    
	} else {
	    for (int i = 0; i < shorts.capacity(); i++) {
		short s = shorts.get(i);
		float fs = s;
		float sample = fs / (float) 32768.0;
		floats.put(sample);
	    }
	}


	floats.position(0);
	shortsOut.push(shortsV);
	floatsOut.push(floatsV);
    }
}
