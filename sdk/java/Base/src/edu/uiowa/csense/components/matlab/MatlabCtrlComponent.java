package edu.uiowa.csense.components.matlab;

import edu.uiowa.csense.profiler.*;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.ByteVector;
import edu.uiowa.csense.runtime.v4.CSenseComponent;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

/**
 * Communicates with Matlab sessions through JMI.
 * 
 * @author Farley Lai
 * 
 */
public class MatlabCtrlComponent extends CSenseComponent {
    public InputPort<ByteVector> in_file = newInputPort(this, "in");
    public OutputPort<ByteVector> out_file = newOutputPort(this, "out");

    MatlabProxyFactory _factory;
    private MatlabProxy _proxy;

    public MatlabCtrlComponent() throws CSenseException {
	super();
	_factory = new MatlabProxyFactory();
    }

    @Override
    public void onInput() throws CSenseException {
	ByteVector msg = in_file.getFrame();
	try {
	    String path = new String(msg.getBuffer().array());
	    _proxy.eval(Utility.toString("disp('file uploaded: ", path, "')"));
	} catch (MatlabInvocationException e) {
	    e.printStackTrace();
	}
	out_file.push(msg);
    }

    @Override
    public void onCreate() throws CSenseException {
	try {
	    _proxy = _factory.getProxy();
	} catch (MatlabConnectionException e) {
	    throw new CSenseException(e);
	}
    }

    @Override
    public void onStart() throws CSenseException {
	try {
	    _proxy.eval("disp('Connected to Matlab...')");
	} catch (MatlabInvocationException e) {
	    throw new CSenseException(e);
	}
    }

    @Override
    public void onStop() {
	try {
	    _proxy.eval("quit");
	} catch (MatlabInvocationException e) {
	    e.printStackTrace();
	}
	_proxy.disconnect();
    }
}