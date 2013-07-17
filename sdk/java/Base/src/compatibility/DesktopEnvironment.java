package compatibility;

import java.io.File;

public class DesktopEnvironment implements EnvironmentInterface {

    @Override
    public File getExternalStorageDirectory() {
	return new File(".");
    }

}
