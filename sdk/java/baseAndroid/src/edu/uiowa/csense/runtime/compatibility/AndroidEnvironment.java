package edu.uiowa.csense.runtime.compatibility;

import java.io.File;

import edu.uiowa.csense.runtime.compatibility.EnvironmentInterface;
import android.os.Environment;

public class AndroidEnvironment implements EnvironmentInterface {
	@Override
	public File getExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory();
	}
}
