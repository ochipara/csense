package edu.uiowa.csense.components.network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.CharVector;
import edu.uiowa.csense.runtime.types.FilenameType;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;

public class HTMLForm<T extends Frame> extends CSenseSource<HTMLFormMessage> {
    public final List<InputPort<Frame>> inputs;
    public final List<OutputPort<Frame>> outputs;
    public final OutputPort<HTMLFormMessage> form = newOutputPort(this, "form");
    protected final int _numPorts;

    //
    protected final String[] _fields; 
    // the name of the form type
    protected final String[] _types; 
    // the type of the element form (see HTML form for documentation)

    public HTMLForm(TypeInfo<HTMLFormMessage> type, String[] fields, String[] types) throws CSenseException {
	super(type);

	_numPorts = fields.length;
	inputs = new ArrayList<InputPort<Frame>>(_numPorts);
	outputs = new ArrayList<OutputPort<Frame>>(_numPorts);
	for (int i = 0; i < _numPorts; i++) {
	    inputs.add(newInputPort(this, fields[i] + "In"));
	    outputs.add(newOutputPort(this, fields[i] + "Out"));
	}


	_fields = fields;
	_types = types;
    }

    @Override
    public void onStart() throws CSenseException {
	boolean supportsPull = true;
	for (int i = 0; i < _numPorts; i++) {
	    if (inputs.get(i).getSupportsPoll() == false) {
		supportsPull = false;
		break;
	    }
	}
	form.setSupportPull(supportsPull);
    }

    @Override
    public void onInput() throws CSenseException {
	HTMLFormMessage htmlForm = getNextMessageToWriteInto();

	for (int i = 0; i < _fields.length; i++) {
	    String name = _fields[i];
	    String type = _types[i];
	    InputPort<Frame> input = inputs.get(i);
	    OutputPort<Frame> output = outputs.get(i);

	    Frame m = input.getFrame();

	    if ("text".equals(type)) {
		try {
		    CharVector m2 = (CharVector) m;
		    htmlForm.addString(name, m2.getString());
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
	    } else if ("file".equals(type)) {
		FilenameType m2 = (FilenameType) m;
		String fn = m2.getString();
		File f = new File(fn);
		if (f.exists() == false) {
		    throw new CSenseException("This should not happen");
		}
		
		if (f.isFile() == false) {
		    throw new CSenseException("This should not happen");		
		}
		
		htmlForm.addFile(name, fn);
	    } else
		throw new CSenseException(CSenseErrors.ERROR,
			"Unsupported type");

	    output.push(m);
	}
	
	form.push(htmlForm);
    }
}
