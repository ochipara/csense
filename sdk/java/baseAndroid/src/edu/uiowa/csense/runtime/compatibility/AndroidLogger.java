package edu.uiowa.csense.runtime.compatibility;

import java.util.logging.Level;

import edu.uiowa.csense.profiler.Utility;
import edu.uiowa.csense.runtime.api.ILog;
import android.util.Log;

public class AndroidLogger implements ILog {	
	@Override
	public void d(String tag, Object... args) {
		StringBuilder sb = Utility.getStringBuilder();
		for (Object arg: args) {
			sb.append(arg.toString() + " ");
		}

		Log.d(tag, sb.toString());
	}

	@Override
	public void e(String tag, Object... args) {
		StringBuilder sb = Utility.getStringBuilder();
		for (Object arg: args) {
			sb.append(arg.toString() + " ");
		}

		Log.e(tag, sb.toString());		
	}

	@Override
	public void w(String tag, Object... args) {
		StringBuilder sb = Utility.getStringBuilder();
		for (Object arg: args) {
			sb.append(arg.toString() + " ");
		}

		Log.w(tag, sb.toString());	
	}

	@Override
	public void i(String tag, Object... args) {
		StringBuilder sb = Utility.getStringBuilder();
		for (Object arg: args) {
			sb.append(arg.toString() + " ");
		}

		Log.i(tag, sb.toString());		
	}


	@Override
	public void v(String tag, Object... args) {
		StringBuilder sb = Utility.getStringBuilder();
		for (Object arg: args) {
			sb.append(arg.toString() + " ");
		}

		Log.v(tag, sb.toString());		
	}

	@Override
	public void setLevel(Level level) {		

	}


}
