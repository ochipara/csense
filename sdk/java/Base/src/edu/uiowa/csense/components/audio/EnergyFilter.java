package edu.uiowa.csense.components.audio;

import java.nio.DoubleBuffer;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

public class EnergyFilter extends CSenseComponent {
    public final InputPort<DoubleVector> in = newInputPort(this, "in");
    public final OutputPort<DoubleVector> above = newOutputPort(this, "above");
    public final OutputPort<DoubleVector> below = newOutputPort(this, "below");
    final double _threshold;
    final int _window;
    int _counter;
    double s = 0;


    public EnergyFilter(double threshold, int smoothingWindow) throws CSenseException {
	_threshold = threshold;
	_window = smoothingWindow;
	_counter = 0;
    }

    @Override
    public void onInput() throws CSenseException {
	DoubleVector v = in.getFrame();		

	if (_counter == 0) {
	    v.position(0);
	    s = 0;
	    for (int i = 0; i < v.size(); i++) {
		s = s + Math.abs(v.get(i));
	    }
	    s = s / v.size();	
	    if (s >= _threshold) {
		_counter = _window;
	    }
	} else {
	    _counter = _counter - 1;
	}

	if (_counter > 0) {	    
	    above.push(v);
	    info("active " + _counter);
	} else {
	    below.push(v);
	}
    }

}
