package project.includes;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class AndroidInclude extends Include {
    public static final String INCLUDE_NAME = "android";

    @XmlElement
    protected String source = "src";

    @XmlTransient
    protected File sourceDir = null;

    public AndroidInclude() {
	setType(INCLUDE_NAME);
    }

    public AndroidInclude(String dir) {
	setDirectory(dir);
    }

    public File getSourceDirectory() {
	if (sourceDir == null) {
	    sourceDir = new File(getDirectory(), source);
	}
	return sourceDir;
    }
}
