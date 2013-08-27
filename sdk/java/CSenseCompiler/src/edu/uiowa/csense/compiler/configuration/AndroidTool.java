package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "android")
@XmlAccessorType(XmlAccessType.FIELD)
public class AndroidTool extends Tool {
    public static final String TOOL_NAME = "android-tools";
    @XmlElement(required = true)
    File sdkTools;
    @XmlElement(required = true)
    File ndkBuild;
    @XmlElement(required = true)
    String target;    
    @XmlElement(required = true)
    File rusage;

    public AndroidTool() {
	this.name = TOOL_NAME;
    }

    public File getSdkTools() {
	return sdkTools;
    }

    public File getAndroid() {
	return new File(sdkTools, "android").getAbsoluteFile();
    }

    public void setSdkTools(File sdkTools) {
	this.sdkTools = sdkTools;
    }

    public File getNdkBuild() {
	return ndkBuild.getAbsoluteFile();
    }

    public void setNdkBuild(File ndkBuild) {
	this.ndkBuild = ndkBuild;
    }

    public String getTarget() {
	return target;
    }

    public void setTarget(String target) {
	this.target = target;
    }

    public File getRusage() {
	return rusage.getAbsoluteFile();
    }

    public void setRusage(File rusage) {
	this.rusage = rusage;
    }
}
