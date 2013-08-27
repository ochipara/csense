package edu.uiowa.csense.components.network;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.TypeInfo;

public class HTMLFormMessage extends Frame {
    protected MultipartEntity _entity = null;

    public HTMLFormMessage(FramePool<? extends Frame> pool,
	    TypeInfo<? extends Frame> type) throws CSenseException {
	super(pool, type);
    }

    @Override
    public void initialize() {
	super.initialize();
	_entity = new MultipartEntity();
    }

    public static TypeInfo<HTMLFormMessage> type() {
	return new TypeInfo<HTMLFormMessage>(HTMLFormMessage.class);
    }

    public void addString(String name, String value)
	    throws UnsupportedEncodingException {
	_entity.addPart(name, new StringBody(value));
    }

    public void addFile(String name, String fileName) {
	_entity.addPart(name, new FileBody(new File(fileName),
		"application/octet-stream"));
    }

    public MultipartEntity getForm() {
	return _entity;
    }

}
