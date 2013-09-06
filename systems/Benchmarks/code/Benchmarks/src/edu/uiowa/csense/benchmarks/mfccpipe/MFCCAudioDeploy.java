package edu.uiowa.csense.benchmarks.mfccpipe;

import java.io.File;

import components.sensors.AudioComponentC;
import components.storage.ToDiskComponentC;
import edu.uiowa.csense.compiler.configuration.Options;
import edu.uiowa.csense.compiler.matlab.MatlabOptions;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.matlab.types.MLFloatMatrix;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.transformations.collapsematlab.CollapseMatlabGroup;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.compiler.types.constraints.Constraint;
import edu.uiowa.csense.compiler.types.constraints.Equal;
import edu.uiowa.csense.compiler.types.constraints.GreaterEqual;
import edu.uiowa.csense.compiler.types.constraints.LessEqual;
import edu.uiowa.csense.compiler.types.constraints.MultipleOf;

public class MFCCAudioDeploy {
    private static int minAudioBuffer = 16000;
    private static int frequency = 16000;
    private static boolean useFloats = false;
    private static boolean useNativeConversions = true;

    public static void processArguments(String[] args) {
	for (int i = 0; i < args.length; i++) {
	    String arg =  args[i];
	    if ("-group".equals(arg)) {
		i = i + 1;		
		CollapseMatlabGroup.MIN_GROUP_SIZE = Integer.parseInt(args[i]); 		
	    } else if ("-types".equals(arg)) {
		i = i + 1;
		arg = args[i];
		if ("simple".equals(arg)) {
		    Options.useSimpleTypeInference = true;
		} else if ("ilp".equals(arg)) {
		    Options.useSimpleTypeInference = false;
		} else {
		    System.err.println("-types options [simple|ilp]");
		    System.exit(-1);
		}
	    } else if ("-superframe".equals(arg)) {
		i = i + 1;
		minAudioBuffer = Integer.parseInt(args[i]);
	    } else if ("-rate".equals(arg)) {
		i = i + 1;
		frequency = Integer.parseInt(args[i]);
	    } else if ("-java-conversions".equals(arg)) {
		useNativeConversions = false;
	    } else {
		System.err.println("Unknonw command");
		System.exit(-1);
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	try {
	    processArguments(args);	 
	    Options.useNativeConversions = useNativeConversions;
	    
	    File projectDirectory = new File(System.getProperty("user.dir"));
	    Project project = new Project(projectDirectory, "MFCCPipeline", "MfccPipeline");

	    PipelineConfig config = new PipelineConfig();	
	    config.setFrequency(frequency);
	    config.setAudioBufferSize(minAudioBuffer);
	    System.out.println("Configuration:");
	    System.out.println(config.toString());
	    System.out.println("CSense config:");
	    System.out.println("minGroupSize:" + CollapseMatlabGroup.MIN_GROUP_SIZE);
	    System.out.println("useSimpleTypeInference:" + Options.useSimpleTypeInference);

	    FrameTypeC audioShortType = TypeInfoC.newShortVector(config.getAudioBufferSize());
	    audioShortType.setConstraint(new GreaterEqual(config.getAudioBufferSize()));

	    FrameTypeC frameType;
	    if (useFloats == false) {
		frameType = TypeInfoC.newDoubleVector();
		frameType.setConstraint(new MultipleOf(config.getFrameSize()));
		frameType.setConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
		frameType.mapToJNI(new MLDoubleMatrix(1, config.getFrameSize()));
	    } else {
		frameType = TypeInfoC.newFloatVector();
		frameType.setConstraint(new MultipleOf(config.getFrameSize()));
		frameType.setConstraint(new Equal(Constraint.ROW_DIMENSION, 1));
		frameType.mapToJNI(new MLFloatMatrix(1, config.getFrameSize()));
	    }

	    project.addComponent("audio", new AudioComponentC(audioShortType, config.getFrequency()));

	    if (config.getAudioBufferSize() % config.getFrameSize() != 0) {
		throw new Exception("Invalid configuration");
	    }

	    MFCCFeaturesG features = (MFCCFeaturesG) project.addComponent("features", new MFCCFeaturesG(frameType, config));
	    project.addComponent("saveDoubles", new ToDiskComponentC(frameType, "/sdcard/tara_wav.dbin"));
	    //	    ToDiskComponentC toDisk = (ToDiskComponentC) project.addComponent("toDisk", new ToDiskComponentC(features.getMfccType(), "/sdcard/tara_mfcc.dbin"));

	    if (Options.useSimpleTypeInference) {
		ToDiskComponentC toDisk = (ToDiskComponentC) project.addComponent("toDisk", new ToDiskComponentC(features.getMfccType(), "/sdcard/tara_mfcc.dbin"));
	    } else {
		FrameTypeC toDiskT = TypeInfoC.newDoubleVector();
		toDiskT.addConstraint(new LessEqual(1024 * 20));
		//	    toDiskT.addConstraint(new MultipleOf(1));
		ToDiskComponentC toDisk = (ToDiskComponentC) project.addComponent("toDisk", new ToDiskComponentC(toDiskT, "/sdcard/tara_mfcc.dbin"));
	    }
	    project.link("audio", "saveDoubles");
	    project.link("saveDoubles", "features");
	    project.link("features::mfcc", "toDisk");
	    project.toTap("toDisk", features.mfccType());	

	    Options.generateProfileCode = true;
	    // Options.useSimpleTypeInference = false;
	    MatlabOptions.printPostResults = false;
	    MatlabOptions.printPreResults = false;
//	    CollapseMatlabGroup.MIN_GROUP_SIZE = 20;
//	    project.nocodegen();
	    project.compile();
	    project.build();
	    project.run();
	} catch (Exception e) {
	    System.out.println("Here");
	    e.printStackTrace();
	    System.exit(123);
	}
    }
}
