package edu.uiowa.csense.analyzer;

public class AnalyzerException extends Exception {	
	// 
	public static final int UNDEFINED = 0;
	public static final int NOT_A_CSENSE_COMPONENT = 1;
	
	public final int error;
	public String msg = null;
	
	public AnalyzerException(int error) {
		this.error = error;
	}
	
}
