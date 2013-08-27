package components.conversions;

import edu.uiowa.csense.compiler.CSenseSourceC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.components.conversions.SimpleConversion;

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
