package project.targets.desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import compiler.CompilerException;
import compiler.matlab.MatlabComponentC;
import compiler.model.CSenseGroupC;
import compiler.model.Domain;
import compiler.model.DomainCoder;
import compiler.model.DomainManager;
import compiler.model.Project;
import compiler.utils.JavaCoder;


import project.resources.ClassResource;
import project.resources.Resource;
import project.resources.ResourceManager;
import project.targets.Target;

import api.IComponentC;

public class CSenseService {
    public static void generateCSenseService(Project project, Target target) throws CompilerException {
	boolean matlab = false;
	CSenseGroupC main = project.getMainGroup();
	for (IComponentC c : main.getComponents()) {
	    if (c instanceof MatlabComponentC) {
		matlab = true;
		break;
	    }
	}	

	// read the start of the EgoService
	try {
	    ResourceManager rm = project.getResourceManager();
	    BufferedReader file = null;
	    String line = null;
	    JavaCoder coder = new JavaCoder();
	    coder.code("package edu.uiowa.csense;");

	    // extract the import from the classes to be deployed
	    Set<String> packages = new HashSet<String>();
	    for (Resource resource : rm.getResources()) {
		if (resource instanceof ClassResource) {
		    ClassResource cls = (ClassResource) resource;
		    packages.add(cls.getPackage());
		}
	    }

	    for (String p : packages) {
		coder.code("import " + p + ".*;");
	    }
	    coder.code("import org.apache.log4j.BasicConfigurator;");

	    coder.code("public class CSenseService  {");
	    if (matlab) {
		coder.code("static {");
		coder.code("System.out.println(\"attempting to load native support\");");
		coder.code("System.loadLibrary(\"csense-native\");");
		coder.code("System.out.println(\"native support loaded\");");

		coder.code("CSenseLib.CSenseLib_initialize();");
		coder.code("}");
	    }



	    coder.newline();
	    // field definitions
	    DomainManager domainManager = DomainManager.domainManager();
	    for (Domain domain : domainManager.domains()) {
		coder.code("private IScheduler " + domain.schedulerName() + ";");
	    }
	    coder.code("private api.CSense csense = new api.CSense(\""
		    + project.getApi() + "\");");
	    coder.newline();

	    coder.code("protected void InitializeSystem()  throws CSenseException {");

	    // instantiate the components
	    coder.setIndent(2);
	    coder.newline();
	    DomainCoder.codeDomains(project.getMainGroup(), coder);

	    for (Domain domain : domainManager.domains()) {
		coder.code(domain.schedulerName() + ".start();");
	    }

	    for (Domain domain : domainManager.domains()) {
		coder.code(domain.schedulerName() + ".join();");
	    }
	    coder.code("}");

	    // generate main
	    coder.newline();
	    coder.code("public static void main(String[] args) throws CSenseException {");
	    coder.code("BasicConfigurator.configure();");
	    coder.code("EgoService service = new EgoService();");
	    coder.code("service.InitializeSystem();");
	    coder.code("System.out.println(\"Application ran correctly!\");");
	    if (matlab)
		coder.code("CSense.CSense_terminate();");
	    coder.code("}");
	    coder.code("}");

	    coder.saveToFile(new File(target.getDirectory(),
		    "src/edu/uiowa/csense/CSenseService.java"));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }
}
