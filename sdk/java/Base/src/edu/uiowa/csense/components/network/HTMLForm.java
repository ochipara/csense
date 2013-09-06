package edu.uiowa.csense.components.network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.bindings.Source;
import edu.uiowa.csense.runtime.types.JavaFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class HTMLForm extends Source<HTMLFormMessage> {
    public final List<InputPort<JavaFrame<String>>> inputs;
    public final List<OutputPort<JavaFrame<String>>> outputs;
    public final OutputPort<HTMLFormMessage> form = newOutputPort(this, "form");
    protected final int _numPorts;

    //
    protected final String[] _fields; 
    // the name of the form type
    protected final String[] _types; 
    // the type of the element form (see HTML form for documentation)

    public HTMLForm(TypeInfo type, String[] fields, String[] types) throws CSenseException {
	super(type);

	_numPorts = fields.length;
	inputs = new ArrayList<InputPort<JavaFrame<String>>>(_numPorts);
	outputs = new ArrayList<OutputPort<JavaFrame<String>>>(_numPorts);
	for (int i = 0; i < _numPorts; i++) {
	    InputPort<JavaFrame<String>> in = newInputPort(this, fields[i] + "In");
	    OutputPort<JavaFrame<String>> out = newOutputPort(this, fields[i] + "Out");
	    inputs.add(in);
	    outputs.add(out);
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
	    InputPort<JavaFrame<String>> input = inputs.get(i);
	    OutputPort<JavaFrame<String>> output = outputs.get(i);

	    JavaFrame<String> m = input.getFrame();

	    if ("text".equals(type)) {
		try {
		    String s = m.unbox();
		    htmlForm.addString(name, s);
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
	    } else if ("file".equals(type)) {
		String fn = m.unbox();
		File f = new File(fn);
		if (f.exists() == false) {
		    throw new CSenseException("This should not happen");
		}
		
		if (f.isFile() == false) {
		    throw new CSenseException("This should not happen");		
		}
		
		htmlForm.addFile(name, fn);
	    } else {
		throw new CSenseException(CSenseError.ERROR, "Unsupported type");
	    }

	    output.push(m);
	}
	
	form.push(htmlForm);
    }
}
