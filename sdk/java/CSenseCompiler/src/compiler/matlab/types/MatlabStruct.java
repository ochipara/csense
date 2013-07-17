package compiler.matlab.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import com.jmatio.io.MatFileFilter;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLStructure;
import com.jmatio.types.MLUint8;

import compiler.CompilerException;

public class MatlabStruct extends MatlabType {
    protected List<NameValuePair> _pairs = new ArrayList<NameValuePair>();
    protected String _ctype;

    public MatlabStruct(String ctype) {
	_ctype = ctype;
    }

    public void addValue(String name, MatlabType value) {
	_pairs.add(new NameValuePair(name, value));
    }

    public void addValue(String name, Double[] doubles) {
	_pairs.add(new NameValuePair(name, new MLDoubleMatrix(1,
		doubles.length, doubles)));
    }

    public void addValue(String name, double value) {
	_pairs.add(new NameValuePair(name, new MLDoubleMatrix(value)));
    }

    public Iterator<NameValuePair> iterator() {
	return _pairs.iterator();
    }

    public List<NameValuePair> values() {
	return _pairs;
    }

    @Override
    public String getStringValue() {
	StringBuffer val = new StringBuffer("struct(");
	boolean first = true;
	for (Iterator<NameValuePair> iter = _pairs.iterator(); iter.hasNext();) {
	    NameValuePair pair = iter.next();

	    if (first == false)
		val.append(", ...\n\t");
	    else
		first = false;
	    val.append("'" + pair._name + "', " + pair._type.getStringValue());
	}
	val.append(")");

	return val.toString();
    }

    @Override
    public String getCodegenType() {
	return "struct";
    }

    @Override
    public String getCType() {
	return _ctype;
    }

    @Override
    public int getNumberOfElements() {
	return 1;
    }

    @Override
    public String getNioType() {
	return java.nio.ByteBuffer.class.getSimpleName();
    }

    @Override
    public int getNumberOfBytes() {
	int numBytes = 0;
	for (Iterator<NameValuePair> iter = _pairs.iterator(); iter.hasNext();) {
	    NameValuePair vp = iter.next();
	    numBytes += vp.getType().getNumberOfBytes();
	}

	return numBytes;
    }

    @Override
    public boolean isPrimitive() {
	return false;
    }

    public void display() {
	for (NameValuePair vp : _pairs) {
	    System.out.println(vp.getName() + " "
		    + vp.getType().getStringValue());
	}

    }

    public static MatlabStruct loadFromMat(String filename, String structName)
	    throws CompilerException {
	File f = new File(filename);
	MatFileFilter filter = new MatFileFilter();
	filter.addArrayName(structName);

	MatFileReader reader = null;
	try {
	    reader = new MatFileReader(f, filter);
	    MLArray data = reader.getContent().get(structName);
	    if (data == null) {
		throw new CompilerException("Could not find variable ["
			+ structName + "] to load from file");
	    }
	    if (data.isStruct() == false) {
		throw new CompilerException("Variable [" + structName
			+ "] is not a structure");
	    }

	    MLStructure struct = (MLStructure) data;
	    MatlabStruct matlabStruct = new MatlabStruct(structName);

	    for (String fieldName : struct.getKeys()) {
		MLArray field = struct.getField(fieldName);
		if (field.isDouble()) {
		    MLDoubleMatrix dm = MLDoubleMatrix.fromMLDouble(field);
		    matlabStruct.addValue(fieldName, dm);
		} else if (field.isLogical()) {
		    MLLogicalArray la = MLLogicalArray.fromMLArray((MLUint8) field);
		    matlabStruct.addValue(fieldName, la);
		} else {
		    throw new CompilerException("Failed to parse struct");
		}
	    }
	    return matlabStruct;
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new CompilerException(e);
	}
    }

}
