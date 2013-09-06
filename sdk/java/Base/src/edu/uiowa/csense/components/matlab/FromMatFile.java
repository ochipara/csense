package edu.uiowa.csense.components.matlab;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLSingle;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Event;
import edu.uiowa.csense.runtime.types.DoubleVector;
import edu.uiowa.csense.runtime.types.FloatVector;
import edu.uiowa.csense.runtime.types.RawFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class FromMatFile<T extends RawFrame> extends CSenseSource<T> {
    public OutputPort<T> out = newOutputPort(this, "out");
    protected MLDouble _doubles = null;
    protected MLSingle _floats = null;
    protected TypeInfo<T> _type;
    protected int _index = 0;
    protected int column = 0, row = 0;
    private int _length;

    public FromMatFile(TypeInfo<T> type, String filename, String variableName)
	    throws CSenseException {
	super(type);

	_type = type;
	loadData(filename, variableName);
    }

    protected void loadData(String filename, String variableName)
	    throws CSenseException {
	File f = new File(filename);
	MatFileFilter filter = new MatFileFilter();
	filter.addArrayName(variableName);

	MatFileReader reader;
	try {
	    reader = new MatFileReader(f, filter);
	    MLArray data = reader.getContent().get(variableName);
	    if (data == null)
		throw new CSenseException(CSenseError.ERROR,
			"Could not find variable [" + variableName
				+ "] to load from file");
	    int[] dims = data.getDimensions();
	    _length = dims[0] * dims[1];

	    if ((_type.getJavaType() == DoubleMatrix.class) || (_type.getJavaType() == DoubleVector.class)) {
		_doubles = (MLDouble) data;
	    } else if ((_type.getJavaType() == FloatMatrix.class) || (_type.getJavaType() == FloatVector.class)) {
		_floats = (MLSingle) data;
	    } else {
		throw new CSenseException("Invalid data types");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}
    }

    /**
     * Since no previous component is present, no message will be passed to this
     * component.
     */
    @Override
    public void onEvent(Event t) throws CSenseException {
	T msg = getNextMessageToWriteInto();
	ByteBuffer byteBuffer = msg.getBuffer();
	byteBuffer.position(0);
	if ((_type.getJavaType() == DoubleVector.class) || (_type.getJavaType() == DoubleMatrix.class)) {
	    DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
	    doubleBuffer.position(0);
	    int count;
	    double avg = 0;
	    for (count = 0; count < _type.getNumberOfElements() && _index < _length; count++) {
		double dval = _doubles.get(_index++);
		doubleBuffer.put(dval);
		avg += dval;
	    }
	    avg = avg / count;
	    // System.out.println("Avg=" + avg);

	    if (count == _type.getNumberOfElements()) {
		out.push(msg);
		getScheduler().schedule(this, asTask());
	    } else {
		getScheduler().stop();
	    }	    
	} else if ((_type.getJavaType() == FloatVector.class) || (_type.getJavaType() == FloatMatrix.class)) {
	    FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
	    floatBuffer.position(0);
	   
	    int sz = _floats.getSize();
	    double avg = 0;	    
	    int count = 0;
	    for (column = 0; column < _type.getColumns() && _index < sz; column++) {
		for (row = 0; row < _type.getRows() && _index < sz; row++) {		    
		    float dval = _floats.get(_index++);
		    floatBuffer.put(dval);
		    avg += dval;
		    count += 1;
		}		
	    }
	    avg = avg / count;	    	  
	    // System.out.println("Avg=" + avg);

	    if (count == _type.getNumberOfElements()) {
		out.push(msg);
		getScheduler().schedule(this, asTask());
	    } else {
		getScheduler().stop();
	    }	    
	} else {	
	    throw new CSenseException(CSenseError.ERROR,
		    "Don't know how to handle type " + _type.getJavaType());
	}
    }

    @Override
    public void onCreate() throws CSenseException {
	getScheduler().schedule(this, asTask());
    }

    public static <T extends RawFrame> FromMatFile<T> create(
	    TypeInfo<T> type, String filename, String variableName)
	    throws CSenseException {
	return new FromMatFile<T>(type, filename, variableName);
    }
}
