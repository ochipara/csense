package components.bluetooth.shimmer;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.TypeInfoC;

import messages.RawMessage;
import messages.fixed.FilenameType;


public class ShimmerFileOutputComponentC extends CSenseComponentC {
    public ShimmerFileOutputComponentC(long spaceLimitInBytes, int fileSizeLimitInSamples, String path, String extension) throws CompilerException {
	setComponent("components.bluetooth.shimmer.ShimmerFileOutputComponent");
	addArgument(new ArgumentC(spaceLimitInBytes));
	addArgument(new ArgumentC(fileSizeLimitInSamples));
	addArgument(new ArgumentC(path));
	addArgument(new ArgumentC(extension));
	
	addIOPort(TypeInfoC.newJavaMessage(RawMessage.class), "data");	
	addOutputPort(TypeInfoC.newJavaMessage(FilenameType.class), "path");
    }
}
