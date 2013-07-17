package compatibility;

import java.util.logging.Level;

import base.Utility;
import android.util.Log;
import api.ILog;

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
