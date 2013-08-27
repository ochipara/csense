package edu.uiowa.csense.compiler.transformations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.configuration.Version;
import edu.uiowa.csense.compiler.targets.Target;


/**
 * This class will provide toolkit specialization by allowing you to switch between versions
 * - it will go through all components and specialize it for a given toolkit version
 * 	- it will replace Component with the specific component implementation
 * 	- it will replace Source with the specific source implementation 
 * 
 * @author ochipara
 *
 */
public class ToolkitDevirtualize {
    private static final String NL = System.getProperty("line.separator");

    private static boolean fixComponent(File src, String code, Version version) {
	// try to fix the files the extend the component
	String componentImpl = version.getComponent();
	Pattern componentPattern = Pattern.compile("(?s).*(extends +Component +\\{).*");
	Matcher componentMatcher = componentPattern.matcher(code);
	if (componentMatcher.matches()) {
	    if (componentMatcher.groupCount() != 1) return false;
	    String newCode = code.substring(0, componentMatcher.start(1) - 1) + " extends " + componentImpl + " {" + code.substring(componentMatcher.end(1));
	    saveSource(src, newCode);
	    return true;
	}

	return false;
    }

    private static boolean fixSource(File src, String code, Version version) {
	// try to fix the files the extend the component
	String componentImpl = version.getSource();
	Pattern componentPattern = Pattern.compile("(?s).*(extends +Source).*");
	Matcher componentMatcher = componentPattern.matcher(code);
	if (componentMatcher.matches()) {
	    if (componentMatcher.groupCount() != 1) return false;
	    String newCode = code.substring(0, componentMatcher.start(1) - 1) + " extends " + componentImpl + code.substring(componentMatcher.end(1));
	    saveSource(src, newCode);
	    return true;
	}

	return false;
    }

    private static void fixSource(File src, Version version) throws IOException {
	String code = loadSource(src);

	if (fixComponent(src, code, version)) return;
	if (fixSource(src, code, version)) return;	
    }

    private static String loadSource(File src) throws IOException {
	StringBuffer sb = new StringBuffer();
	BufferedReader br = new BufferedReader(new FileReader(src));

	while(true) {
	    String line = br.readLine();
	    if (line == null) break;
	    sb.append(line + NL);
	}
	br.close();
	return sb.toString();
    }

    private static void saveSource(File src, String code) {
	try {
	    FileWriter fw = new FileWriter(src);
	    fw.write(code);
	    fw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void fixAllSources(File sourceDir, Version version) throws CompilerException {
	Collection<File> files = FileUtils.listFiles(sourceDir, new String[] {"java"}, true);
	for (File src : files) {
	    try {
		fixSource(src, version);
	    } catch (IOException e) {
		throw new CompilerException(e);
	    }
	}
    }


    public static void devirtualize(Target target, Version version) throws CompilerException {
	File dir = target.getDirectory();
	fixAllSources(dir, version);
    }

    public static void main(String[] args) throws IOException, CompilerException {
	ToolkitConfiguration sdkConfig = ToolkitConfiguration.autodetectSDK();
	Version version = sdkConfig.getVersion("v4", "android");
	File dir = new File("/Users/ochipara/Working/CSense/csense-git/systems/Benchmarks/ProducerConsumer/gen/ProducerConsumer");

	fixAllSources(dir, version);
    }
}
