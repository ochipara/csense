package edu.uiowa.csense.components.conversions;

import java.nio.ByteBuffer;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.types.ShortVector;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.types.Vector;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class SimpleConversion<Tsrc extends Vector, Tdst extends Vector> extends CSenseSource<Tdst>{
    public final InputPort<Tsrc> srcIn = newInputPort(this, "srcIn");
    public final OutputPort<Tsrc> srcOut = newOutputPort(this, "srcOut");
    public final OutputPort<Tdst> dstOut = newOutputPort(this, "dstOut");
    
    protected final int conversionIndex;
    protected Tdst destMsg = null;
    protected ByteBuffer destBuf = null;
    
    public SimpleConversion(TypeInfo dst, TypeInfo src) throws CSenseException {
	super(dst);
	
	if ((src.getJavaType() == ShortVector.class) && (dst.getJavaType() == DoubleVector.class)) {
	    conversionIndex = 1;
	} else throw new CSenseException("Invalid conversion");
    }

    @Override
    public void onInput() throws CSenseException {
	Tsrc msg = srcIn.getFrame();	
		
	try {
	    short2double(msg);
	} catch (InterruptedException e) {
	    throw new CSenseException(CSenseError.INTERRUPTED_OPERATION);
	}
    }

    private void short2double(Tsrc sourceMsg) throws CSenseException, InterruptedException {
	ByteBuffer sourceBuf = sourceMsg.getBuffer();
	sourceBuf.position(0);
		
	allocate();	
	while (sourceBuf.hasRemaining()) {
	    short s = sourceBuf.getShort();
	    double d = s / 32768.0;
	    destBuf.putDouble(d);
	    if (destBuf.remaining() == 0) {
		dstOut.push(destMsg);
		destMsg = null;
		destBuf = null;
		allocate();
	    }
	}
	srcOut.push(sourceMsg);	
    }

    private void allocate() throws InterruptedException {
	if (destMsg == null) {
	    destMsg = getNextMessageToWriteIntoAndBlock();
	    destBuf = destMsg.getBuffer();	    
	}	
    }
    
    
}
