package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tool")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompilerTool extends Tool {
    public static final String TOOL_NAME = "csense-compiler";
    @XmlElement(name = "directory")
    private File compilerDirectory;

    public CompilerTool() {
	setName(CompilerTool.TOOL_NAME);
    }

    public File getDirectory() {
	return compilerDirectory.getAbsoluteFile();
    }

    public File getSourceDirectory() {
	return new File(compilerDirectory, "src").getAbsoluteFile();
    }
    
    public File getResourceDirectory() {
	return new File(compilerDirectory, "resources").getAbsoluteFile();
    }

    public void setDirectory(File compilerDirectory) {
	this.compilerDirectory = compilerDirectory;
    }

    public void setDirectory(String dir) {
	setDirectory(new File(dir));
    }

}
