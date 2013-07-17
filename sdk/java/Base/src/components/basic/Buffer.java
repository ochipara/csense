package components.basic;

import java.util.ArrayList;


import messages.TypeInfo;
import messages.fixed.Vector;

import api.CSenseException;
import api.CSenseSource;
import api.IInPort;
import api.IOutPort;

public class Buffer<T, Tin extends Vector<T>, Tout extends Vector<T>> extends CSenseSource<Tout> {
    protected final int _numSamples;
    protected final int _overlap;
    protected final ArrayList<Tout> _frames;

    public IInPort<Tin> in = newInputPort(this, "in");
    public IOutPort<Tin> out = newOutputPort(this, "out");
    public IOutPort<Tout> frame = newOutputPort(this, "frame");

    public Buffer(int overlap, TypeInfo<Tout> outType) throws CSenseException {
	super(outType);
	_numSamples = outType.getNumberOfElements();
	_overlap = overlap;

	int overlapFactor = _numSamples / overlap;
	_frames = new ArrayList<Tout>(overlapFactor);
    }

    public void requestBuffer() {
	_frames.add(getNextMessageToWriteInto());
    }

    @Override
    public void doInput() throws CSenseException {
	Tin m = in.getMessage();

	if (_frames.size() == 0) {
	    requestBuffer();
	}

	m.position(0);	
	for (int i = 0; i < m.getNumberOfElements(); i++) {
	    T value = m.get();

	    int allocate = 0;
	    int toremove = -1;
	    for (int j = 0; j < _frames.size(); j++) {		
		Tout mOut = _frames.get(j);		

		if (mOut.remaining() > 0) {
		    mOut.put(value);
		}

		if (mOut.remaining() == 0) {
		    frame.push(mOut);
		    toremove = j;
		} else if (mOut.remaining() == _overlap) {
		    allocate += 1;
		}
	    }

	    if (toremove >= 0) {
		_frames.remove(toremove);
	    }

	    for (;allocate > 0; allocate--) {
		requestBuffer();
	    }
	}




	out.push(m);
    }
}
