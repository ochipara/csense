package edu.uiowa.csense.compiler.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import edu.uiowa.csense.compiler.CompilerException;

public class CSenseInclude extends Include {
    public static final String INCLUDE_CSENSE = "csense";
  
    @XmlElementWrapper
    @XmlElement(name = "source")
    public List<File> sources = new LinkedList<File>();    

    public CSenseInclude() throws CompilerException {
	setType(INCLUDE_CSENSE);	
    }

    public void addSource(File f) throws CompilerException {
	if (f.exists() == false) {
	    throw new CompilerException("Source " + f + " does not exist");
	}

	sources.add(f);
    }

    public void addSource(String fn) throws CompilerException {
	addSource(new File(fn));
    }

    public List<File> getSources() {
	return sources;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("sources: [");
	for (File f : sources)
	    sb.append(f + " ");
	sb.append("]");
	return sb.toString();
    }
}
