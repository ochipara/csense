package components.matlab;

import api.CSenseComponent;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import base.*;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

import messages.fixed.ByteVector;

/**
 * Communicates with Matlab sessions through JMI.
 * 
 * @author Farley Lai
 * 
 */
public class MatlabCtrlComponent extends CSenseComponent {
    public IInPort<ByteVector> in_file = newInputPort(this, "in");
    public IOutPort<ByteVector> out_file = newOutputPort(this, "out");

    MatlabProxyFactory _factory;
    private MatlabProxy _proxy;

    public MatlabCtrlComponent() throws CSenseException {
	super();
	_factory = new MatlabProxyFactory();
    }

    @Override
    public void doInput() throws CSenseException {
	ByteVector msg = in_file.getMessage();
	try {
	    String path = new String(msg.bytes());
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