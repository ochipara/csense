package edu.uiowa.csense.compiler.configuration;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Configures a specific version of the toolkit. 
 * This information will be used during code deployment and generation.
 * 
 * @author ochipara
 *
 */
public class Version {        
    @XmlAttribute(required=true)
    String version;
    
    @XmlAttribute(required = true)
    String platform;
    
    @XmlElementWrapper
    @XmlElement(name = "package")
    List<String> packages = new LinkedList<String>();
  
    @XmlElement(required = true)
    String component;
    
    @XmlElement(required = true)
    String source;
    
    @XmlElement(required = true)
    String messagePool;
    
    @XmlElement(required = true)
    String scheduler;
    
    @XmlElement(required = true)
    String taskQueue;
    
    @XmlElement(required = true)
    String eventQueue;

    @XmlElement(required = true)
    public String idleLock;

    public String getIdleLock() {
        return idleLock;
    }

    public void addPackage(String p) {
	this.packages.add(p);
    }
    
    public String getComponent() {
	return component;    
    }
    
    public String getSource() {
	return source;
    }

    public String getVersion() {
        return version;
    }

    public String getPlatform() {
        return platform;
    }

    public List<String> getPackages() {
        return packages;
    }

    public String getMessagePool() {
        return messagePool;
    }

    public String getTaskQueue() {
        return taskQueue;
    }

    public String getEventQueue() {
        return eventQueue;
    }
    
    public String getScheduler() {
	return scheduler;
    }
}
