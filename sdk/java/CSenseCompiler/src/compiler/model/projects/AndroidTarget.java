package compiler.model.projects;

import java.io.File;

public class AndroidTarget {
    public static final String DEFAULT_ACTIVITY = "EgoDeployActivity";
    public static final String DEFAULT_PACKAGE = "edu.uiowa";
    protected final String _name;
    protected final String _activity;
    protected final String _packageName;
    protected final File _directory;

    public AndroidTarget(String name, String activity, String packageName, String importDirectory) {
	_name = name;
	_activity = activity;
	_packageName = packageName;
	if (importDirectory != null) {
	    _directory = new File(importDirectory);
	} else {
	    _directory = null;
	}
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append("Name: " + _name + "\n");
	sb.append("Activity :" + _activity + "\n");
	sb.append("Package :" + _packageName + "\n");

	return sb.toString();
    }

    public String getProjectName() {
	return _name;
    }

    public String getActivity() {
	return _activity;
    }

    public String getPackage() {
	return _packageName;
    }

    public String getActivityClass() {
	return _packageName + "." + _activity;
    }

    public File getDirectory() {
	return _directory;
    }

    public static AndroidTarget defaultTarget(String projectName) {
	return new AndroidTarget(projectName, DEFAULT_ACTIVITY, DEFAULT_PACKAGE, null);
    }
    
    public boolean isDefault() {
	return getActivity() == AndroidTarget.DEFAULT_ACTIVITY && getPackage() == AndroidTarget.DEFAULT_PACKAGE && getDirectory() == null;
    }
}