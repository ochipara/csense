package compiler.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import compiler.CompilerException;




import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

public class MatlabCommand {
    protected static Logger logger = Logger.getLogger(MatlabCommand.class);
    protected static MatlabProxyFactoryOptions options = null;
    protected static MatlabProxyFactory factory = null;
    protected static MatlabProxy proxy = null;
    protected static MatlabCommand command = null;

    public MatlabCommand() throws CompilerException {
	logger.setLevel(Level.INFO);
	try {
	    options = new MatlabProxyFactoryOptions.Builder()
		    .setUsePreviouslyControlledSession(true).build();
	    factory = new MatlabProxyFactory(options);
	    proxy = factory.getProxy();
	} catch (MatlabConnectionException e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new CompilerException("Failed to connected to matlab");
	}
    }

    public static MatlabCommand getMatlabCommand() throws CompilerException {
	if (command == null)
	    command = new MatlabCommand();
	return command;
    }

    public void command(String cmd) throws CompilerException {
	try {
	    logger.debug(cmd);
	    proxy.eval(cmd);
	} catch (MatlabInvocationException e) {
	    // close the matlab connection
	    disconnect();
	    logger.error("Matlab command failed:" + cmd);
	    logger.error(e);
	    e.printStackTrace();
	    throw new CompilerException("Matlab command failed");
	}
    }

    public void disconnect() {
	proxy.disconnect();
    }

    public static void main(String[] args) throws MatlabConnectionException,
	    MatlabInvocationException {
	// Create a proxy, which we will use to control MATLAB
	// MatlabProxyFactory factory = new MatlabProxyFactory();

	MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
		.setUsePreviouslyControlledSession(true).build();
	MatlabProxyFactory factory = new MatlabProxyFactory(options);
	MatlabProxy proxy = factory.getProxy();

	// Display 'hello world' just like when using the demo
	proxy.eval("disp('hello world')");

	// Disconnect the proxy from MATLAB
	proxy.disconnect();
    }

    public MatlabProxy getProxy() {
	return proxy;
    }
}
