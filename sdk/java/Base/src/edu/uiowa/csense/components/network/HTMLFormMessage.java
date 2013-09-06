package edu.uiowa.csense.components.network;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.JavaFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class HTMLFormMessage extends JavaFrame<MultipartEntity> {

    public HTMLFormMessage(FramePool pool,
	    TypeInfo type) throws CSenseException {
	super(pool, type);
    }

    @Override
    public void initialize() {
	super.initialize();
	data = new MultipartEntity();
    }

    public static TypeInfo type() {
	return new TypeInfo(HTMLFormMessage.class);
    }

    public void addString(String name, String value)
	    throws UnsupportedEncodingException {
	data.addPart(name, new StringBody(value));
    }

    public void addFile(String name, String fileName) {
	data.addPart(name, new FileBody(new File(fileName), "application/octet-stream"));
    }

    public MultipartEntity getForm() {
	return data;
    }

}
