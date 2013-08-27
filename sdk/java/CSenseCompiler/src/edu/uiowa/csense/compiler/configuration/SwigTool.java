package edu.uiowa.csense.compiler.configuration;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tool")
@XmlAccessorType(XmlAccessType.FIELD)
public class SwigTool extends Tool {
    public static final String TOOL_NAME = "swig";
    File swig;

    public SwigTool() {
	this.name = "swig";
    }

    public File getSwig() {
	return swig;
    }
}
