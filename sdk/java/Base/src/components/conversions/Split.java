package components.conversions;

import java.util.List;

import messages.fixed.Vector;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import api.Message;

public class Split<T extends Vector> extends CSenseComponent {
    private static final String TAG = "split";
    public final IInPort<T> dataIn = newInputPort(this, "dataIn");
    public final IOutPort<T> dataOut = newOutputPort(this, "dataOut");
    
    protected final int numFrames;
    protected int outputFrameSize;
    
    protected int frameCounter = 0;
    protected List<Message> frames = null;
    protected int receivedElements = 0;
    protected int frameElements = 0;

    public Split(int numFrames, int outputFrameSize) throws CSenseException {
	this.numFrames = numFrames;
	this.outputFrameSize = outputFrameSize;
    }

    @Override
    public void doInput() throws CSenseException {
	T input = dataIn.getMessage();
	if (frameCounter == 0) {
	   frames = input.split(this, numFrames);	   	
	}
	
	input.decrementReference();
	int frameSize = input.capacity();
	receivedElements += frameSize;
	while ((frameCounter < numFrames) && (frameElements + outputFrameSize <= receivedElements)) {
	    dataOut.push((T) frames.get(frameCounter));
	    frameCounter += 1;
	    frameElements += outputFrameSize;
	}	
//	T in = dataIn.getMessage();
//
////	Log.d(TAG, in.hashCode() + " pre  refs=" + in.getReference());
//	List<Message> frames = in.split(this, frameSize);
////	Log.d(TAG, in.hashCode() + " post refs=" + in.getReference());
//
//	for (int i = 0; i < frameSize; i++) {
//	    T subframe = (T) frames.get(i);
//	    dataOut.push(subframe);
//	}
////	Log.d(TAG, in.hashCode() + " finish refs=" + in.getReference());
    }
}
