package project.configuration;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tool")
@XmlAccessorType(XmlAccessType.FIELD)
public class LpSolveTool extends Tool {
    public static final String TOOL_NAME = "lpsolve";
    File lpsolve;

    public LpSolveTool() {
	this.name = "lpsolve";
    }

    public File getLpSolve() {
	return lpsolve;
    }
}
