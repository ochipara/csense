package components.bluetooth.shimmer;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.TypeInfoC;
import edu.uiowa.csense.runtime.types.FilenameType;
import edu.uiowa.csense.runtime.types.RawFrame;


public class ShimmerFileOutputComponentC extends CSenseComponentC {
    public ShimmerFileOutputComponentC(long spaceLimitInBytes, int fileSizeLimitInSamples, String path, String extension) throws CompilerException {
	setComponent("components.bluetooth.shimmer.ShimmerFileOutputComponent");
	addArgument(new ArgumentC(spaceLimitInBytes));
	addArgument(new ArgumentC(fileSizeLimitInSamples));
	addArgument(new ArgumentC(path));
	addArgument(new ArgumentC(extension));
	
	addIOPort(TypeInfoC.newJavaMessage(RawFrame.class), "data");	
	addOutputPort(TypeInfoC.newJavaMessage(FilenameType.class), "path");
    }
}
