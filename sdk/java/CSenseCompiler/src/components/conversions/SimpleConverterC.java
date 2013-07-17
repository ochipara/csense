package components.conversions;

import compiler.CSenseSourceC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.types.FrameTypeC;
import components.conversions.SimpleConversion;

public class SimpleConverterC extends CSenseSourceC {
    public SimpleConverterC(FrameTypeC src, FrameTypeC dest) throws CompilerException {
  	super(SimpleConversion.class, src);
  	addIOPort(src, "src");
  	addOutputPort(dest, "dstOut");
  	
  	addGenericType(src);
  	addGenericType(dest);
  	
  	addArgument(new ArgumentC(src.getTypeInfo()));
  	//addArgument(new ArgumentC(dest.getTypeInfo()));
      }
}
