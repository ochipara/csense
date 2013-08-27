package edu.uiowa.csense.compiler.targets;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;










import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.checks.ConnectedPorts;
import edu.uiowa.csense.compiler.checks.PartitionChecks;
import edu.uiowa.csense.compiler.configuration.CompilerTool;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.configuration.SwigTool;
import edu.uiowa.csense.compiler.configuration.ToolkitConfiguration;
import edu.uiowa.csense.compiler.matlab.MatlabCodegen;
import edu.uiowa.csense.compiler.matlab.MatlabComponentC;
import edu.uiowa.csense.compiler.model.CSenseGroupC;
import edu.uiowa.csense.compiler.model.Domain;
import edu.uiowa.csense.compiler.model.DomainManager;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.project.resources.ClassResource;
import edu.uiowa.csense.compiler.project.resources.JarResource;
import edu.uiowa.csense.compiler.project.resources.Resource;
import edu.uiowa.csense.compiler.project.resources.ResourceManager;
import edu.uiowa.csense.compiler.transformations.ExpandFanIn;
import edu.uiowa.csense.compiler.transformations.ExpandFanOut;
import edu.uiowa.csense.compiler.transformations.ExpandGroup;
import edu.uiowa.csense.compiler.transformations.GenerateAppGraph;
import edu.uiowa.csense.compiler.transformations.RemoveUnusedOptionalPorts;
import edu.uiowa.csense.compiler.transformations.collapsematlab.CollapseMatlabGroup;
import edu.uiowa.csense.compiler.transformations.partition.QuickPartition2;
import edu.uiowa.csense.compiler.transformations.types.CheckTypes;
import edu.uiowa.csense.compiler.transformations.types.OptimizedTypeInference;
import edu.uiowa.csense.compiler.transformations.types.SimpleTypeInference;
import edu.uiowa.csense.compiler.utils.Coder;
import edu.uiowa.csense.compiler.utils.ExecuteCommand;

public abstract class CompileTargetProcessor extends TargetProcessor {
    static ExecuteCommand copy = ExecuteCommand.executeCommand();
    protected static Logger logger = Logger.getLogger("compiler-target");

    public CompileTargetProcessor() {
    }

    protected void instantiateGroup(Project project, CSenseGroupC group)
	    throws CompilerException {
	Target target = project.getTarget();

	// check the components
	for (IComponentC component : group.getComponents()) {
	    if (component instanceof CSenseGroupC) {
		CSenseGroupC innerGroup = (CSenseGroupC) component;
		innerGroup.instantiate();
		instantiateGroup(project, innerGroup);
		//ExpandFanOut.expandFanOut(innerGroup);
		//validate(project, innerGroup);

		File output = new File(target.getDirectory(),
			innerGroup.getVariableName() + ".dot");
		GenerateAppGraph.generateAppGraph(innerGroup, output);
	    }
	}
    }

    public void validate(Project project, CSenseGroupC component)
	    throws CompilerException {
	ConnectedPorts.checkConnections(component);
    }

    public void partitionApplication(CSenseGroupC _mainGroup) throws CompilerException {
	// PartitionApplication.partitionApplication(_mainGroup);
	// PartitionApplication2.partitionApplication(_mainGroup);
	// QuickPartition.partitionApplication(_mainGroup);
	QuickPartition2 qp = new QuickPartition2();
	qp.partitionApplication(_mainGroup);

	System.out.println("Domain partition ======> ");
	DomainManager manager = DomainManager.domainManager();
	for (Domain domain : manager.domains()) {
	    System.out.println(domain);
	    for (IComponentC component : domain.components()) {
		System.out.println("\t" + component);
	    }
	}
	System.out.println("Domain partition <====== ");

	PartitionChecks.checkPartitions(_mainGroup);

    }

    @Override
    public void compile(Project project, Target target) throws CompilerException {
	CSenseGroupC _mainGroup = project.getMainGroup();
	instantiateGroup(project, _mainGroup);	
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "main.dot"));

	//validate(project, _mainGroup);	
	ExpandGroup.expandGroup(_mainGroup.getComponentGraph());
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full0.dot"));

	ExpandFanOut.expandFanOut(_mainGroup);
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full0-fo.dot"));

	ExpandFanIn.expandFanIn(_mainGroup);
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full0-fi.dot"));

	RemoveUnusedOptionalPorts.removeUnsedOptionalPorts(_mainGroup);
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full0-rm.dot"));

	validate(project, _mainGroup);	

	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full1.dot"));
	CollapseMatlabGroup.collapseGroup(project, _mainGroup);
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full2.dot"));

	if (Options.useSimpleTypeInference) {
	    new SimpleTypeInference().convertTypes(_mainGroup, target);
	} else {
	    new OptimizedTypeInference().convertTypes(_mainGroup, target);
	}	

	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "full3.dot"));
	validate(project, _mainGroup);
	CheckTypes.checkTypes(_mainGroup, target);

	partitionApplication(_mainGroup);


	validate(project, _mainGroup);
	GenerateAppGraph.generateAppGraph(_mainGroup, new File(target.getDirectory(), "app.dot"));

	boolean matlab;
	try {
	    matlab = generateMatlabComponents(project, _mainGroup);
	    if (matlab == false) {
		MatlabComponentC.generate_makefile(target);
		MatlabComponentC.run_make(project, target);
	    }
	} catch (IOException e) {
	    throw new CompilerException(e);
	}	
    }

    protected boolean generateMatlabComponents(Project project, CSenseGroupC _mainGroup) throws CompilerException, IOException {
	Target target = project.getTarget();
	ResourceManager rm = project.getResourceManager();

	ToolkitConfiguration conf = project.getSdkConfig();
	SwigTool swig = (SwigTool) conf.getTool(SwigTool.TOOL_NAME);
	CompilerTool compiler = (CompilerTool) conf.getTool(CompilerTool.TOOL_NAME);

	boolean hasMatlabComponents = false;
	for (IComponentC component : _mainGroup.getComponents()) {
	    if (component instanceof MatlabComponentC) {
		hasMatlabComponents = true;
		break;
	    }
	}

	if (hasMatlabComponents) {
	    File codegenScript = new File(target.getJniDirectory(), "codegen_script.m");
	    MatlabCodegen.generate_codegen_script(project, _mainGroup, codegenScript);
	    if (project.isRunCodegen() == true)
		MatlabCodegen.run_codegen(project);

	    for (IComponentC component : _mainGroup.getComponents()) {
		if (component instanceof MatlabComponentC) {
		    logger.debug("Generating code for matlab component " + component.getName());
		    
		    MatlabComponentC matlabComponent = ((MatlabComponentC) component);
		    matlabComponent.generate_code();
		    rm.addResource(new ClassResource(matlabComponent.getName(),
			    matlabComponent.getPackage(),
			    matlabComponent.getFile(), null)).setDeployed(true);	
		} else {
		    if (component.getComponent() != null) {
			rm.addClass(component.getComponent());
		    }
		}
	    }
	}

	int code;
	ExecuteCommand copy = ExecuteCommand.executeCommand();

	ExecuteCommand invokeSwig = ExecuteCommand.executeCommand();
	File outputDir = new File(target.getSourceDirectory(),
		"edu/uiowa/csense/");
	if (outputDir.exists() == false) {
	    if (outputDir.mkdirs() == false)
		throw new CompilerException("Failed to create target directory");
	}
	
	if (hasMatlabComponents) {
	    code = invokeSwig.execute(swig.getSwig().getAbsolutePath() + " -java"
		    + " -package " + "edu.uiowa.csense" + " -outdir " + outputDir
		    + " " + compiler.getSourceDirectory() + "/csense.i");
	    if (code != 0)
		throw new CompilerException("RUN SWIG -- failed run on csense.i\n"
			+ invokeSwig.getLastCommand());

	
	    code = copy.execute("mv " + compiler.getSourceDirectory()
		    + "/csense_wrap.c " + target.getJniDirectory());
	    if (code != 0) throw new CompilerException("RUN SWIG -- copying csense_wrap");
	} else {
	    code = invokeSwig.execute(swig.getSwig().getAbsolutePath() + " -java"
		    + " -package " + "edu.uiowa.csense" + " -outdir " + outputDir
		    + " " + compiler.getSourceDirectory() + "/csense_nomatlab.i");
	    if (code != 0)
		throw new CompilerException("RUN SWIG -- failed run on csense.i\n"
			+ invokeSwig.getLastCommand());	    
	}

	if (Project.make()) {
	    MatlabComponentC.generate_makefile(target);
	    MatlabComponentC.run_make(project, target);
	}

	return hasMatlabComponents;
    }

    protected void newEclipseProjectForAndroid(Project project, Target target)
	    throws CompilerException {
	try {
//	    Target target = project.getTarget();
	    String eclipse_project = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<projectDescription>\n"
		    + "<name>"
		    + project.getName() 
		    + "</name>"
		    + "\t<comment></comment>\n\t<projects>\n\t</projects>\n\t<buildSpec>\n\t\t<buildCommand>\n\t\t\t<name>com.android.ide.eclipse.adt.ResourceManagerBuilder</name>\n\t\t\t<arguments>\n\t\t\t</arguments>\n\t\t</buildCommand>\n\t\t<buildCommand>\n\t\t\t<name>com.android.ide.eclipse.adt.PreCompilerBuilder</name>\n\t\t\t<arguments>\n\t\t\t</arguments>\n\t\t</buildCommand>\n\t\t<buildCommand>\n\t\t\t<name>org.eclipse.jdt.core.javabuilder</name>\n\t\t\t<arguments>\n\t\t\t</arguments>\n\t\t</buildCommand>\n\t\t<buildCommand>\n\t\t\t<name>com.android.ide.eclipse.adt.ApkBuilder</name>\n\t\t\t<arguments>\n\t\t\t</arguments>\n\t\t</buildCommand>\n\t</buildSpec>\n\t<natures>\n\t\t<nature>com.android.ide.eclipse.adt.AndroidNature</nature>\n\t\t<nature>org.eclipse.jdt.core.javanature</nature>\n\t</natures>\n</projectDescription>";

	    Coder projectFile = new Coder();
	    projectFile.line(eclipse_project);

	    projectFile.saveToFile(new File(target.getDirectory(), ".project"));

	    Coder classpath = new Coder();
	    classpath.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    classpath.append("<classpath>\n");
	    classpath.append("\t<classpathentry kind=\"src\" path=\"src\"/>\n");
	    classpath.append("\t<classpathentry kind=\"src\" path=\"gen\"/>\n");
	    classpath.append("\t<classpathentry kind=\"con\" path=\"com.android.ide.eclipse.adt.ANDROID_FRAMEWORK\">\n");
	    classpath.append("\t\t<attributes>\n");
	    classpath.append("\t\t\t<attribute name=\"org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY\" value=\""
		    + target.getJniDirectory() + "\"/>\n");
	    classpath.append("\t\t</attributes>\n");
	    classpath.append("\t</classpathentry>\n");
	    classpath.append("\t<classpathentry kind=\"con\" path=\"com.android.ide.eclipse.adt.LIBRARIES\"/>\n");
	    classpath.append("\t<classpathentry kind=\"output\" path=\"bin/classes\"/>\n");

//	    ResourceManager rm = project.getResourceManager();
//	    for (Resource r : rm.getResources()) {
//		if (r instanceof JarResource) {
//		    JarResource jar = ((JarResource) r);
//
//		    classpath.line("\t<classpathentry kind=\"lib\" path=\"libs/"
//			    + jar.getLocalFile().getName() + "\"/>");
//		}
//	    }

	    classpath.line("</classpath>\n");
	    classpath.saveToFile(new File(target.getDirectory(), "/.classpath"));
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
    }

    protected void newEclipseProject(Project project) throws CompilerException {
	try {
	    Target target = project.getTarget();
	    Coder coder = new Coder();
	    coder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    coder.append("<projectDescription>\n");
	    coder.append("\t<name>" + project.getName() + "</name>\n");
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
	    coder.saveToFile(new File(target.getDirectory(), ".project"));

	    Coder classpath = new Coder();
	    classpath.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    classpath.append("<classpath>\n");
	    classpath.append("\t<classpathentry kind=\"src\" path=\"src\"/>\n");
	    classpath.append("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.6\">\n");
	    classpath.append("\t\t<attributes>\n");
	    classpath.append("\t\t\t<attribute name=\"org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY\" value=\"" + target.getJniDirectory() + "\"/>\n");
	    classpath.append("\t\t</attributes>\n");
	    classpath.append("\t</classpathentry>\n");
	    ResourceManager rm = project.getResourceManager();
	    for (Resource r : rm.getResources()) {
		if (r instanceof JarResource) {
		    JarResource jar = ((JarResource) r);

		    classpath
		    .line("\t<classpathentry kind=\"lib\" path=\"libs/"
			    + jar.getLocalFile().getName() + "\"/>");
		}
	    }

	    classpath
	    .append("\t<classpathentry kind=\"output\" path=\"bin\"/>\n");
	    classpath.append("</classpath>\n");

	    classpath
	    .saveToFile(new File(target.getDirectory(), "/.classpath"));
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
    }
}
