package components.basic;


import edu.uiowa.csense.compiler.CSenseComponentC;
import edu.uiowa.csense.compiler.CompilerException;
import edu.uiowa.csense.compiler.model.ArgumentC;
import edu.uiowa.csense.compiler.types.FrameTypeC;
import edu.uiowa.csense.components.basic.Slice;

public class SliceC extends CSenseComponentC {
    protected final int _lower;
    protected final int _upper;

    public SliceC(FrameTypeC inputType, FrameTypeC outputType, int lower, int upper) throws CompilerException {
	super(Slice.class);
	if (inputType.getMessageType() != outputType.getMessageType()) {
	    throw new CompilerException("Invalid conversion");
	}

	_lower = lower;
	_upper = upper;

//	InputPortC input = addInputPort(inputType, "dataIn");
//	OutputPortC output = addOutputPort(outputType, "dataOut");
//	input.setInternalOutput(output);

	addIOPort(inputType, "data");
	addArgument(new ArgumentC(lower));
	addArgument(new ArgumentC(upper));
    }

    public int lower() {
	return _lower;
    }

    public int upper() {
	return _upper;
    }

}
