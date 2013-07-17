package components.audio;

import messages.fixed.DoubleVector;
import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;

public class EnergyFilter extends CSenseComponent {
    public IInPort<DoubleVector> in = newInputPort(this, "in");
    public IOutPort<DoubleVector> above = newOutputPort(this, "above");
    public IOutPort<DoubleVector> below = newOutputPort(this, "below");
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
    public void doInput() throws CSenseException {
	DoubleVector v = in.getMessage();

	if (_counter == 0) {
	    v.position(0);
	    s = 0;
	    for (int i = 0; i < v.getNumberOfElements(); i++) {
		s = s + Math.abs(v.get(i));
	    }
	    s = s / v.getNumberOfElements();	
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
