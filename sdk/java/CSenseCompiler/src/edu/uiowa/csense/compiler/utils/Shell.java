package edu.uiowa.csense.compiler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.uiowa.csense.compiler.CompilerException;

/**
 * The Shell executes shell commands and print the console output in the blocking mode.
 * @author Farley Lai
 *
 */
public class Shell {    
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE = -1;
    
    private static String strcat(String[] cmdarray) {
	StringBuilder builder = new StringBuilder();
	for(String s: cmdarray) builder.append(s).append(" ");
	builder.setLength(builder.length()-1);
	return builder.toString();
    }
    
    private static int exec(Process p) throws CompilerException {
	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	String line;
	try {
	    while ((line = in.readLine()) != null) System.out.println(line);
	    while ((line = err.readLine()) != null) System.out.println(line);
	    while(true) {
		try {
		    return p.waitFor();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	} catch (IOException e) {
	    throw new CompilerException("failed to read command output", e);
	}
    }
    
    public static int exec(String command) throws CompilerException {	
	try {
	    return exec(Runtime.getRuntime().exec(command));	   
	} catch (IOException e) {
	    throw new CompilerException("failed to execute '" + command + "'", e);
	}
    }
    
    public static int exec(String command, File wd) throws CompilerException { 
	try {
	    return exec(Runtime.getRuntime().exec(command, null, wd));	   
	} catch (IOException e) {
	    throw new CompilerException("failed to execute '" + command + "'", e);
	}
    }
        
    public static int exec(String... cmdarray) throws CompilerException { 
	try {
	    return exec(Runtime.getRuntime().exec(cmdarray));	   
	} catch (IOException e) {
	    throw new CompilerException("failed to execute '" + strcat(cmdarray) + "'", e);
	}
    }
    
    public static int exec(String[] cmdarray, File wd) throws CompilerException { 
	try {
	    return exec(Runtime.getRuntime().exec(cmdarray, null, wd));	   
	} catch (IOException e) {
	    throw new CompilerException("failed to execute '" + strcat(cmdarray) + "'", e);
	}
    }
}
