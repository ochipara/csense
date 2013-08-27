package edu.uiowa.csense.compiler.configuration;

import java.io.File;
import java.io.IOException;

import edu.uiowa.csense.compiler.utils.Coder;


public class EclipseUtils {
    public static void newEclipseProject(String projectName, File directory) throws IOException{	
	Coder coder = new Coder();
	coder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	coder.append("<projectDescription>\n");
	coder.append("\t<name>" + projectName + "</name>\n");
	coder.append("\t<comment></comment>\n");
	coder.append("\t<projects></projects>\n");
	coder.append("\t<buildSpec>\n");
	coder.append("\t\t<buildCommand>\n");
	coder.append("\t\t\t<name>org.eclipse.jdt.core.javabuilder</name>\n");
	coder.append("\t\t\t<arguments></arguments>\n");
	coder.append("\t\t</buildCommand>\n");
	coder.append("\t</buildSpec>\n");
	coder.append("\t<natures>\n");
	coder.append("\t\t<nature>org.eclipse.jdt.core.javanature</nature>\n");
	coder.append("\t</natures>\n");
	coder.append("</projectDescription>");
	coder.saveToFile(new File(directory, ".project"));

	Coder classpath = new Coder();
	classpath.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	classpath.append("<classpath>\n");
	classpath.append("\t<classpathentry kind=\"src\" path=\"src\"/>");
	classpath
	.append("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.6\"/>\n");	

	classpath.append("\t<classpathentry kind=\"output\" path=\"bin\"/>\n");
	classpath.append("</classpath>\n");

	classpath
	.saveToFile(new File(directory, "/.classpath"));

    }
}
