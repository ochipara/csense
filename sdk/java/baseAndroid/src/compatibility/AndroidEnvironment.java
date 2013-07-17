package compatibility;

import java.io.File;

import android.os.Environment;

public class AndroidEnvironment implements EnvironmentInterface {
	@Override
	public File getExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory();
	}
}
