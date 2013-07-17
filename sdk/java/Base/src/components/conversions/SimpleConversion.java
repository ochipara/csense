package components.conversions;

import java.nio.ByteBuffer;

import messages.TypeInfo;
import messages.fixed.DoubleVector;
import messages.fixed.ShortVector;
import messages.fixed.Vector;
import api.CSenseErrors;
import api.CSenseException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;

public class SimpleConversion<Tsrc extends Vector, Tdst extends Vector> extends CSenseSource<Tdst>{
    public final IInPort<Tsrc> srcIn = newInputPort(this, "srcIn");
    public final IOutPort<Tsrc> srcOut = newOutputPort(this, "srcOut");
    public final IOutPort<Tdst> dstOut = newOutputPort(this, "dstOut");
    
    protected final int conversionIndex;
    protected Tdst destMsg = null;
    protected ByteBuffer destBuf = null;
    
    public SimpleConversion(TypeInfo<Tdst> dst, TypeInfo<Tsrc> src) throws CSenseException {
	super(dst);
	
	if ((src.getJavaType() == ShortVector.class) && (dst.getJavaType() == DoubleVector.class)) {
	    conversionIndex = 1;
	} else throw new CSenseException("Invalid conversion");
    }

    @Override
    public void doInput() throws CSenseException {
	Tsrc msg = srcIn.getMessage();	
		
	try {
	    short2double(msg);
	} catch (InterruptedException e) {
	    throw new CSenseException(CSenseErrors.INTERRUPTED_OPERATION);
	}
    }

    private void short2double(Tsrc sourceMsg) throws CSenseException, InterruptedException {
	ByteBuffer sourceBuf = sourceMsg.buffer();
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
	    destBuf = destMsg.buffer();	    
	}	
    }
    
    
}
