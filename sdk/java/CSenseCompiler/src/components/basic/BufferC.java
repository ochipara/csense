package components.basic;


import compiler.CSenseComponentC;
import compiler.CompilerException;
import compiler.model.ArgumentC;
import compiler.model.DefaultComponentCoder;
import compiler.types.BaseTypeC;
import compiler.types.FrameTypeC;
import compiler.utils.JavaCoder;

import api.IComponentC;

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
