package edu.uiowa.csense.compiler.targets;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "target")
@XmlAccessorType(XmlAccessType.FIELD)
public class AndroidTarget extends Target {
    public static final String PLATFORM = "android";
    @XmlElement
    protected String mainActivity;
    @XmlElement
    protected String packageName;

    public AndroidTarget() {
	platform = AndroidTarget.PLATFORM;
    }

    public AndroidTarget(String targetName, String activity, String packageName, File projectDir) {
	super(targetName, projectDir);
	this.platform = PLATFORM;
	this.baseDirectory = projectDir;
	this.mainActivity = activity;
	this.packageName = packageName;
    }

    public String getActivity() {
	return mainActivity;
    }

    public String getPackage() {
	return packageName;
    }

    public String getActivityClass() {
	return packageName + "." + mainActivity;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append("Activity :" + mainActivity + "\n");
	sb.append("Package :" + packageName + "\n");
	return sb.toString();
    }
}
