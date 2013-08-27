package edu.uiowa.csense.runtime.compatibility.desktop;

import java.io.File;

import edu.uiowa.csense.runtime.compatibility.EnvironmentInterface;

public class DesktopEnvironment implements EnvironmentInterface {

    @Override
    public File getExternalStorageDirectory() {
	return new File(".");
    }

}
