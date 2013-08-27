package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import javax.xml.bind.annotation.*;

import edu.uiowa.csense.compiler.model.Project;


@XmlRootElement(name = "include")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AndroidInclude.class, CSenseInclude.class, JarInclude.class })
public abstract class Include {
    @XmlAttribute(required = true)
    protected String type;
    @XmlElement(name = "directory")
    protected String dirStr;

    @XmlTransient
    protected File dir = null;

    @XmlElement(required = true)
    protected String name;
    
    public File getDirectory() {
	if (dir == null) {
	    dir = new File(Project.getProject().getProjectDirectory(), dirStr);
	}

	return dir;
    }

    public void setDirectory(String dir) {
	dirStr = dir;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
}
