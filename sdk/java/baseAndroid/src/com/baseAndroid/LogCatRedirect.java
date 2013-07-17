package com.baseAndroid;

import java.io.OutputStream;
import java.io.PrintStream;

import android.util.Log;

/**
 * This enables a uniform logging system for our toolkit.
 * LogCatRedirect redirects all System.err and System.out messages to Log.
 *  
 * Unfortunately, this class will have to reside in every android project. 
 * @author Austin
 *
 */
public class LogCatRedirect extends PrintStream {
	public LogCatRedirect ( OutputStream out ) {
		super(out);
	}

	public void println ( String str ){
		Log.d( "Redirect" , str );
	}
}

