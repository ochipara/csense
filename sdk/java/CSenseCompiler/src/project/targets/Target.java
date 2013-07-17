package project.targets;

import java.io.File;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import compiler.model.Project;

import project.targets.android.AndroidTarget;

@XmlRootElement(name = "target")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AndroidTarget.class, DesktopTarget.class })
public class Target {
    @XmlAttribute(required = true)
    protected String name;

    @XmlAttribute(required = true)
    protected String platform;

    @XmlElement(name = "baseDirectory", required = true)
    protected String baseStr;

    @XmlTransient
    protected File baseDirectory = null;

    @XmlElement(name = "sourceDirectory")
    protected String sourceDirectoryStr = "src";
    @XmlTransient
    protected File sourceDirectory = null;

    @XmlElement(name = "jniDirectory")
    protected String jniDirectoryStr = "jni";
    @XmlTransient
    protected File jniDirectory = null;

    @XmlElement(name = "libDirectory")
    protected String libDirectoryStr = "libs";
    @XmlTransient
    protected File libDirectory = null;
    
    
    @XmlElement(name = "resourceDirectory")
    private String resourceDirectoryStr = "res";
    @XmlTransient
    protected File resourceDirectory;
    
    public Target() {
    }

    public Target(String targetName) {
	this();
	this.name = targetName;
    }

    public File getDirectory() {
	if (baseDirectory == null) {
	    baseDirectory = new File(Project.getProject().getProjectDirectory(), baseStr);
	}

	return baseDirectory;
    }

    public File getJniDirectory() {
	if (jniDirectory == null) {
	    baseDirectory = getDirectory();
	    jniDirectory = new File(baseDirectory, jniDirectoryStr);
	}
	return jniDirectory.getAbsoluteFile();
    }

    public File getSourceDirectory() {
	if (sourceDirectory == null) {
	    baseDirectory = getDirectory();
	    sourceDirectory = new File(baseDirectory, sourceDirectoryStr);
	}

	return sourceDirectory.getAbsoluteFile();
    }

    public String getName() {
	return name;
    }

    public File getLibDirectory() {
	if (libDirectory == null) {
	    baseDirectory = getDirectory();
	    libDirectory = new File(baseDirectory, libDirectoryStr);
	}

	return libDirectory;
    }

    public File getResouceDirectory() {
	if (resourceDirectory == null) {
	  resourceDirectory = new File(getDirectory(), resourceDirectoryStr);
	}
	
	return resourceDirectory;
    }

    public String getPlatfrom() {
	return platform;
    }

    public String getProjectName() {
	return baseDirectory.getName();
    }

}
