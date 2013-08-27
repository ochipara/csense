package edu.uiowa.csense.compiler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.uiowa.csense.compiler.CompilerException;




public class ExecuteCommand {
    public static Logger logger = Logger.getLogger(ExecuteCommand.class);
    protected StreamWrapper error = null;
    protected StreamWrapper output = null;
    protected File workingDirectory = null;
    protected String lastCommand = null;
    protected static ExecuteCommand _cmd = new ExecuteCommand();

    private class StreamWrapper extends Thread {
	InputStream is = null;
	String type = null;
	String message = null;

	public String getMessage() {
	    return message;
	}

	StreamWrapper(InputStream is, String type) {
	    this.is = is;
	    this.type = type;
	}

	@Override
	public void run() {
	    try {
		BufferedReader br = new BufferedReader(
			new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null) {
		    buffer.append(line).append("\n");
		}
		message = buffer.toString();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
    }

    private ExecuteCommand() {
	logger.setLevel(Level.INFO);
    }

    public static ExecuteCommand executeCommand() {
	return _cmd;
    }

    public int execute(String cmd, File workingDirectory) throws CompilerException {
	this.workingDirectory = workingDirectory;
	return execute(cmd);
    }

    public int execute(String cmd) throws CompilerException {
	try {
	    int exitVal;

	    logger.debug("Executing " + cmd);
	    Runtime rt = Runtime.getRuntime();
	    Process process;

	    if (workingDirectory == null)
		process = rt.exec(cmd);
	    else
		process = rt.exec(cmd, null, workingDirectory);

	    error = new StreamWrapper(process.getErrorStream(), "ERROR");
	    output = new StreamWrapper(process.getInputStream(), "OUTPUT");

	    output.start();
	    error.start();
	    output.join();
	    error.join();

	    exitVal = process.waitFor();
	    logger.debug("Exit code " + exitVal);

	    lastCommand = cmd;
	    // System.out.println("Output: \n"+output.message+"\nError:\n "+error.message);
	    return exitVal;
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to run command [" + cmd + "]");
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to run command [" + cmd + "]");
	}
    }

    public int execute(String[] commands) throws CompilerException {
	try {
	    int exitVal;

	    Runtime rt = Runtime.getRuntime();
	    Process process;

	    if (workingDirectory == null)
		process = rt.exec(commands);
	    else
		process = rt.exec(commands, null, workingDirectory);

	    error = new StreamWrapper(process.getErrorStream(), "ERROR");
	    output = new StreamWrapper(process.getInputStream(), "OUTPUT");

	    output.start();
	    error.start();
	    output.join();
	    error.join();

	    exitVal = process.waitFor();
	    logger.debug("Exit code " + exitVal);

	    lastCommand = commands[0];
	    // System.out.println("Output: \n"+output.message+"\nError:\n "+error.message);
	    return exitVal;
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to run command [" + commands[0]
		    + "]");
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to run command [" + commands[0]
		    + "]");
	}
    }

    public String output() {
	return output.message;
    }

    public String error() {
	return error.message;
    }

    public String getLastCommand() {
	return lastCommand;
    }

    public String getErrorMessage() {
	return error.getMessage();
    }

    public String getOutputMessage() {
	return output.getMessage();
    }

}
