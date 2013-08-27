package tests.components;


import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.Project;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.runtime.types.DoubleVector;


public class TestNormalizeWav {
    // the system's parameters that are used to instantiate the components
    static int numFrames = 256;

    public static void main(String[] args) throws CompilerException {
	Project project = new DesktopProject("TestNormalizeWav");
	project.addExternalJar("/Users/ochipara/Working/lib/java/jmatio.jar");
	project.addExternalJar("/Users/ochipara/Working/lib/java/log4j-1.2.17.jar");

	BaseTypeC frameT = project.templateType(DoubleVector.class,
		numFrames + 1);
	frameT.mapToJNI(new DoubleMatrix(1, numFrames + 1));

	String fileName = "/Users/ochipara/Working/EgoSense/trunk/apps/TestNormalizeWav/test_input.mat";
	project.addComponent("fromDisk", new FromMatFileC(frameT, fileName,
		"input_psd"));

	project.addComponent("normalize_wav", new NormalizeWavC(frameT));
	project.addComponent("tap_frame", new TapComponentC(frameT));

	project.link("fromDisk", "normalize_wav");
	project.link("normalize_wav", "tap_frame");
	project.compile();
	System.out.println("Compilation complete");
    }
}
