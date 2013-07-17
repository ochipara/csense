package compiler.matlab;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.DefaultComponentCoder;
import compiler.model.OutputPortC;
import compiler.model.Project;
import compiler.utils.Coder;
import compiler.utils.ExecuteCommand;
import compiler.utils.JavaCoder;


import project.configuration.AndroidTool;
import project.targets.Target;

import api.IComponentC;

public class MatlabComponentC extends CSenseComponentC {
    // protected String CSENSE_DIRECTORY =
    // "/Users/ochipara/Working/EgoSense/trunk/csense/sdk/matlab/";
    // protected String COMPILE_DIRECTORY = "/Users/ochipara/CSenseCompile/";
    protected static Logger logger = Logger.getLogger(MatlabComponentC.class);

    // information used to integrate the matlab code into the toolkit
    protected String _matlabFunctionName = null;
    protected List<MatlabArgument> _inputArgs = new ArrayList<MatlabArgument>();
    protected List<MatlabArgument> _outputArgs = new ArrayList<MatlabArgument>();
    protected Set<MatlabArgument> _args = new HashSet<MatlabArgument>();

    protected MatlabTypeConverter _converter = DefaultTypeConverter
	    .getConverter();

    // keep track of the locations where files are generated
    protected File _fileDirectory = null;
    protected File _swigFile = null;
    protected File _jniDirectory = null;

    protected String _swigWrapperFile = null;

    class SortMatlabArgumentByName implements Comparator<MatlabArgument> {
	@Override
	public int compare(MatlabArgument o1, MatlabArgument o2) {
	    return o1.getName().compareTo(o2.getName());
	}

    };

    protected final SortMatlabArgumentByName _argumentSorter = new SortMatlabArgumentByName();
    protected Target target;
    private Project project;

    // private AndroidTool androidTool;

    protected class MatlabComponentCoder extends DefaultComponentCoder {
	@Override
	public void genericSignature(IComponentC component, JavaCoder coder) {
	    // no generics
	}
    }

    public MatlabComponentC(String matlabFunctionName) {
	super(matlabFunctionName);
	setComponent(matlabFunctionName);
    }
    
    @Override
    public void setComponent(String matlabFunctionName) {
	_packageName = "base.matlab";
	_matlabFunctionName = matlabFunctionName;
	if (matlabFunctionName.endsWith("M")) {
	    _componentName = matlabFunctionName.substring(0, matlabFunctionName.length() - 1);
	} else {
	    _componentName = matlabFunctionName;
	}

	project = Project.getProject();
	target = project.getTarget();

	// setup the coder to instantiate matlab components
	_coder = new MatlabComponentCoder();

	// this controls the locations of where code will be generated for this
	// component
	_fileDirectory = new File(target.getSourceDirectory(),
		_packageName.replace(".", "/"));
	_jniDirectory = target.getJniDirectory();
	_swigFile = new File(_fileDirectory, matlabFunctionName + ".i");
	_swigWrapperFile = matlabFunctionName + "W";
    }


    public void addMatlabInput(MatlabArgument arg) {
	if (arg == null) throw new IllegalAccessError("arg cannot be null");
	_inputArgs.add(arg);
	_args.add(arg);
    }

    public void addMatlabOutput(MatlabArgument arg) {
	if (arg == null) throw new IllegalAccessError("arg cannot be null");
	_outputArgs.add(arg);
	_args.add(arg);
    }

    public Collection<MatlabArgument> allMatlabArguments() {
	return _args;
    }

    public File getFile() {
	return new File(_fileDirectory, getName() + ".java");
    }

    protected void generate_component_wrapper() throws CompilerException {
	Coder code = MatlabComponentWrapper.generateComponentWrapper(this);
	try {
	    code.saveToFile(getFile());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to generate wrapper");
	}
    }

    public void generate_code() throws CompilerException {
	setup();
	MatlabSwig.generateSwig(this);
	try {
	    MatlabSwig.run_swig(project, this);
	} catch (IOException e) {
	    throw new CompilerException(e);
	}
	generate_component_wrapper();
    }

    private void setup() throws CompilerException {
	// create the directory where the source will be generated
	int code;
	ExecuteCommand cmd = ExecuteCommand.executeCommand();
	code = cmd.execute("mkdir -p " + _fileDirectory);
	if (code != 0)
	    throw new CompilerException(
		    "SETUP -- failed to create output directory");
    }

    public MatlabTypeConverter getMatlabTypeCovertert() {
	return _converter;
    }

    public static void generate_makefile_android(Target target)
	    throws CompilerException {
	Coder coder = new Coder();
	coder.line("LOCAL_PATH := $(call my-dir)");
	coder.line("include $(CLEAR_VARS)");
	coder.line("LOCAL_MODULE    := csense-native");
	coder.line("LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog");

	File folder = target.getJniDirectory();
	File[] sources = folder.listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File file) {
		return (file.getName().endsWith(".c"));
	    }
	});

	StringBuffer source_str = new StringBuffer();
	for (File source : sources) {
	    source_str.append(source.getName() + " ");
	}

	coder.line("LOCAL_SRC_FILES := " + source_str.toString());
	coder.line("include $(BUILD_SHARED_LIBRARY)");
	try {
	    coder.saveToFile(new File(target.getJniDirectory(), "Android.mk"));
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to save make file");
	}
    }

    public static void generate_makefile_desktop(Target target)
	    throws CompilerException {
	Coder coder = new Coder();
	coder.line("LOCAL_MODULE := libcsense-native.dylib");

	File folder = target.getJniDirectory();
	File[] sources = folder.listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File file) {
		return (file.getName().endsWith(".c"));
	    }
	});

	StringBuffer source_str = new StringBuffer();
	for (File source : sources) {
	    source_str.append(source.getName() + " ");
	}

	coder.line("LOCAL_SRC_FILES := " + source_str.toString());
	coder.line("OBJECT_FILES := $(LOCAL_SRC_FILES:%.c=%.o)");
	coder.line("CFLAGS=-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ -g\n");
	coder.line("all:\t$(LOCAL_MODULE)\n");
	coder.line("$(LOCAL_MODULE):\t$(OBJECT_FILES)");
	coder.line("\tgcc -shared -g -o $(LOCAL_MODULE) $(OBJECT_FILES)\n");
	coder.line("clean:");
	coder.line("\trm -f *.o $(LOCAL_MODULE)");
	try {
	    coder.saveToFile(new File(target.getJniDirectory(), "Makefile"));
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException("Failed to save make file");
	}
    }

    public static void generate_makefile(Target target)
	    throws CompilerException {
	if ("android".compareTo(Project.getPlatform()) == 0) {
	    generate_makefile_android(target);
	} else if ("desktop".compareTo(Project.getPlatform()) == 0) {
	    generate_makefile_desktop(target);
	} else {
	    throw new CompilerException("Cannot generate make file for "
		    + Project.getPlatform() + " platform.");
	}
    }

    public static void run_make(Project project, Target target)
	    throws CompilerException {
	int code;

	if ("android".compareTo(Project.getPlatform()) == 0) {
	    AndroidTool androidTool = (AndroidTool) project.getSdkConfig()
		    .getTool(AndroidTool.TOOL_NAME);
	    try {
		ExecuteCommand update = ExecuteCommand.executeCommand();
		code = update.execute(androidTool.getAndroid()
			+ " update project -p " + target.getJniDirectory()
			+ " -s");
		if (code != 0)
		    throw new CompilerException(
			    "Failed to update android project");
	    } catch (CompilerException e) {
		e.printStackTrace();
		throw new CompilerException("Failed to update android project");
	    }

	    try {
		ExecuteCommand make = ExecuteCommand.executeCommand();
		code = make.execute(androidTool.getNdkBuild().getAbsolutePath(), target.getJniDirectory());
		if (code != 0) {
		    System.err.println(make.getErrorMessage());
		    System.err.println(make.getOutputMessage());
		    throw new CompilerException("Failed to build");
		}
	    } catch (CompilerException e) {
		e.printStackTrace();
		throw new CompilerException("Failed to build");
	    }
	} else if ("desktop".compareTo(Project.getPlatform()) == 0) {
	    try {
		ExecuteCommand make = ExecuteCommand.executeCommand();
		code = make.execute("make", target.getJniDirectory());
		if (code != 0) {
		    logger.error(make.getOutputMessage());
		    // logger.error(make.getErrorMessage());
		    throw new CompilerException("Failed to build\n"
			    + make.getErrorMessage());
		}
	    } catch (CompilerException e) {
		e.printStackTrace();
		throw new CompilerException("Failed to update project");
	    }
	} else {
	    throw new CompilerException("Don't know how to run make file for "
		    + Project.getPlatform() + " platform");
	}
    }

    public File getDeployFile() {
	assert (getName() != null);
	return new File(_fileDirectory, getName() + ".java");
    }

    public String getMatlabFunctionName() {
	return _matlabFunctionName;
    }

    public File getFileDirectory() {
	return _fileDirectory;
    }

    public Collection<MatlabArgument> matlabInputs() {
	return _inputArgs;
    }

    public Collection<MatlabArgument> matlabOutputs() {
	return _outputArgs;
    }

    public File getSwigFile() {
	return _swigFile;
    }

    public MatlabParameter getParameter(OutputPortC out) {
	MatlabParameter r = null;
	for (MatlabArgument arg : _args) {
	    if (arg instanceof MatlabParameter) {
		MatlabParameter param = (MatlabParameter) arg;
		if (param.getOutputPort() == out) {
		    assert (r == null);
		    r = param;
		}
	    }
	}

	return r;
    }

    public void sortMatlabArguments() {
	Collections.sort(_inputArgs, _argumentSorter);
	Collections.sort(_outputArgs, _argumentSorter);
    }
}
