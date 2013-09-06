package edu.uiowa.csense.benchmarks.mfccpipe;
import java.io.File;

import components.storage.FromDiskComponentC;
import components.storage.ToDiskComponentC;
import edu.uiowa.csense.compiler.CSenseComponentC.ThreadingOption;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.matlab.MatlabOptions;
import edu.uiowa.csense.compiler.matlab.types.MLDoubleMatrix;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.transformations.collapsematlab.CollapseMatlabGroup;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.types.TypeInfoC;

public class MFCCPipelineDeploy {
    public static void main(String[] args) throws CompilerException {
	File projectDirectory = new File(System.getProperty("user.dir"));

	Project project = new Project(projectDirectory, "MFCCPipieline", "MfccPipeline");
	PipelineConfig config = new PipelineConfig();

	FrameTypeC frameType = TypeInfoC.newDoubleVector(config.getFrameSize());
	frameType.mapToJNI(new MLDoubleMatrix(1, config.getFrameSize()));
	
	// load from disk and upload
	FromDiskComponentC fromDisk = (FromDiskComponentC) project.addComponent("fromDisk", new FromDiskComponentC(frameType, "/sdcard/tara.dbin"));	
	MFCCFeaturesG features = (MFCCFeaturesG) project.addComponent("features", new MFCCFeaturesG(frameType, config));
	ToDiskComponentC toDisk = (ToDiskComponentC) project.addComponent("toDisk", new ToDiskComponentC(features.getMfccType(), "/sdcard/tara_mfcc.dbin"));

	project.link("fromDisk", "features");
	project.link("features::mfcc", "toDisk");
	project.toTap("toDisk", features.mfccType());
	
	fromDisk.setThreadingOption(ThreadingOption.CSENSE);
	
	
	MatlabOptions.printPostResults = false;
	MatlabOptions.printPreResults = false;
	CollapseMatlabGroup.MIN_GROUP_SIZE = 2;
	project.compile();
    }
}
