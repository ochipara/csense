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
	return baseDirectory;
    }

    public File getJniDirectory() {	
	return jniDirectory;
    }

    public File getSourceDirectory() {
	return sourceDirectory;
    }

    public String getName() {
	return name;
    }

    public File getLibDirectory() {
	return libDirectory;
    }

    public File getResouceDirectory() {
	return resourceDirectory;
    }

    public String getPlatform() {
	return platform;
    }
}
