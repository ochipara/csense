package edu.uiowa.csense.compiler.targets;


import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "target")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AndroidTarget.class, DesktopTarget.class })
public class Target {
    @XmlAttribute(required = true)
    protected String name;

    @XmlAttribute(required = true)
    protected String platform;

    @XmlElement(name = "baseDirectory", required = true)
    protected File baseDirectory;

    @XmlElement(name = "sourceDirectory")
    protected File sourceDirectory;

    @XmlElement(name = "jniDirectory")
    protected File jniDirectory;

    @XmlElement(name = "libDirectory")
    protected File libDirectory;

    @XmlElement(name = "resourceDirectory")
    private File resourceDirectory;

    public Target() {
    }

    public Target(String targetName, File projectDirectory) {
	this();
	this.name = targetName;
	sourceDirectory = new File(projectDirectory, "src");
	jniDirectory = new File(projectDirectory, "jni");
	libDirectory = new File(projectDirectory, "libs");
	resourceDirectory = new File(projectDirectory, "res");		
    }

    public File getDirectory() {
	if (baseDirectory.isAbsolute() == false) {
	    baseDirectory = baseDirectory.getAbsoluteFile();
	}
	return baseDirectory;
    }

    public File getJniDirectory() {	
	if (jniDirectory.isAbsolute() == false) {
	    jniDirectory = new File(baseDirectory, jniDirectory.getPath());
	}
	return jniDirectory;
    }

    public File getSourceDirectory() {
	if (sourceDirectory.isAbsolute() == false) {
	    sourceDirectory = new File(baseDirectory, sourceDirectory.getPath());
	}
	return sourceDirectory;
    }

    public String getName() {
	return name;
    }

    public File getLibDirectory() {
	if (libDirectory.isAbsolute() == false) {
	    libDirectory = new File(baseDirectory, libDirectory.getPath());
	}
	return libDirectory;
    }

    public File getResouceDirectory() {
	if (resourceDirectory.isAbsolute() == false) {
	    resourceDirectory = new File(baseDirectory, resourceDirectory.getPath());
	}
	return resourceDirectory;
    }

    public String getPlatform() {
	return platform;
    }
}
