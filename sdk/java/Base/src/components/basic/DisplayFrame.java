package components.basic;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import api.CSenseComponent;
import api.CSenseErrors;
import api.CSenseException;
import api.IInPort;
import api.IOutPort;
import messages.RawMessage;
import messages.TypeInfo;
import messages.fixed.CharVector;
import messages.fixed.DoubleMatrix;
import messages.fixed.DoubleVector;
import messages.fixed.FloatMatrix;

public class DisplayFrame<T extends RawMessage> extends CSenseComponent {
    protected TypeInfo<T> _type;
    public final IInPort<T> in = newInputPort(this, "in");
    public final IOutPort<T> out = newOutputPort(this, "out");
    int perline = 50;

    public DisplayFrame(TypeInfo<T> type) throws CSenseException {
	_type = type;
    }

    @Override
    public void doInput() throws CSenseException {
	T msg = in.getMessage();

	if (_type.getJavaType() == DoubleVector.class) {
	    DoubleBuffer doubles = msg.buffer().asDoubleBuffer();

	    doubles.position(0);
	    double avg = 0;

	    for (int i = 0; i < doubles.limit(); i++) {
		double d = doubles.get(i);
		avg += d;
		System.out.print(d + " ");
		if (i % perline == perline - 1)
		    System.out.println();
	    }
	    System.out.println("\navg=" + avg + "\n");
	} else if (_type.getJavaType() == RawMessage.class) {
	    msg.position(0);
	    for (int i = 0; i < msg.limit(); i++) {
		byte d = msg.buffer().get(i);
		System.out.print(d + " ");
		if (i % perline == perline - 1)
		    System.out.println();
	    }
	} else if (_type.getJavaType() == CharVector.class) {
	    ByteBuffer buf = msg.buffer();

	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < buf.limit(); i++) {
		char d = (char) buf.get(i);
		sb.append(d);
	    }
	    System.out.print(sb.toString());
	} else if (_type.getJavaType() == DoubleMatrix.class) {
	    DoubleMatrix dm = (DoubleMatrix) msg;
	    System.out.println(dm.displayValues());
	} else if (_type.getJavaType() == FloatMatrix.class) {
	    FloatMatrix dm = (FloatMatrix) msg;
	    System.out.println(dm.displayValues());
	} else {
	    throw new CSenseException(CSenseErrors.UNSUPPORTED_OPERATION,
		    "Display does not support type " + _type.getJavaType());
	}

	out.push(msg);
    }
}
