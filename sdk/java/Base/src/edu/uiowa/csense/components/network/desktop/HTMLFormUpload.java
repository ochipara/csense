package edu.uiowa.csense.components.network.desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.uiowa.csense.components.network.HTMLFormMessage;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

/**
 * This allows you to upload a form
 * 
 * @author ochipara
 * 
 */
public class HTMLFormUpload extends CSenseComponent {
    public final InputPort<HTMLFormMessage> in = newInputPort(this, "in");
    public final OutputPort<HTMLFormMessage> out = newOutputPort(this, "out");

    // state for the uploader
    protected final URI _uri;

    // retry mechanism
    protected final long[] _timeouts = { 30, 60, 120, 300, 900 };
    protected int _timeoutIndex = 0;

    // data
    HTMLFormMessage _form = null;

    public HTMLFormUpload(String uri) throws CSenseException {
	try {
	    _uri = new URI(uri);
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}
    }

    private long retry() {
	if (_timeoutIndex + 1 < _timeouts.length) {
	    _timeoutIndex += 1;
	}

	return _timeouts[_timeoutIndex] * 1000;
    }

    @Override
    public void onInput() throws CSenseException {
	while (true) {
	    if (_form == null) {
		_form = in.getFrame();
	    }

	    debug("connected");
	    try {
		HttpPost httppost = new HttpPost(_uri);
		HttpClient httpclient = new DefaultHttpClient();

		httppost.setEntity(_form.getForm());
		HttpResponse response;

		response = httpclient.execute(httppost);

		int code = response.getStatusLine().getStatusCode();

		if (code != 200) {
		    warn("uploader-thread", "upload failed",
			    response.getStatusLine());
		} else {
		    info("uploader-thread", "upload successful");

		    out.push(_form);
		    _form = null;
		    return;
		}
	    } catch (ClientProtocolException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    // sleep as necessary
	    try {
		Thread.sleep(retry());
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }
}
