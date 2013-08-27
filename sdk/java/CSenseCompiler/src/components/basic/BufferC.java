package components.basic;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.model.DefaultComponentCoder;
import edu.uiowa.csense.compiler.model.api.IComponentC;
import edu.uiowa.csense.compiler.types.BaseTypeC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.compiler.utils.JavaCoder;

public class BufferC extends CSenseComponentC {
    private class BufferCoder extends DefaultComponentCoder {
	@Override
	public void genericSignature(IComponentC component, JavaCoder coder) {
	    coder.code("<Double,DoubleVector,DoubleVector>");
	}
    };

    public BufferC(BaseTypeC inT, FrameTypeC outT, int overlap)
	    throws CompilerException {
	super(Buffer.class);

	addInputPort(inT, "in");
	addOutputPort(inT, "out");
	addOutputPort(outT, "frame");
	addArgument(new ArgumentC(overlap));
	addArgument(new ArgumentC(outT));

	// TypeInfoC.newDynamicType(Double.class);
	addGenericType(inT);
	addGenericType(outT);

	setCoder(new BufferCoder());
    }

}
