package edu.uiowa.csense.compiler.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AndroidTool.class, SwigTool.class, CompilerTool.class, LpSolveTool.class, KeyTool.class })
public abstract class Tool {
    @XmlAttribute
    protected String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
