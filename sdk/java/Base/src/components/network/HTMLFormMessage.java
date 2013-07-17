package components.network;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import messages.TypeInfo;
import api.CSenseException;
import api.IMessagePool;
import api.Message;

public class HTMLFormMessage extends Message {
    protected MultipartEntity _entity = null;

    public HTMLFormMessage(IMessagePool<? extends Message> pool,
	    TypeInfo<? extends Message> type) throws CSenseException {
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
