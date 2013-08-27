package edu.uiowa.csense.compiler.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class JarInclude extends Include {
    public static final String INCLUDE_JAR = "jar";

    @XmlElement(name = "jar")
    protected List<File> jars = new LinkedList<File>();

    public JarInclude() {
	setType(INCLUDE_JAR);
    }

    public void addJar(File jar) {
	jars.add(jar);
    }

    public void addJar(String fn) {
	addJar(new File(fn));
    }

    public List<File> getJars() {
	return jars;
    }
}
