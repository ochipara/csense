package edu.uiowa.csense.components.android.network;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import edu.uiowa.csense.components.network.HTMLFormMessage;
import edu.uiowa.csense.runtime.api.CSenseError;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.bindings.Component;

/**
 * Implements a simple HTTP upload component.
 * The component is *SYNCHRONOUS* and should be probably be executed in a different domain.
 * 
 * @author ochipara
 *
 */
public class HTTPFormUpload3 extends Component {
    public final InputPort<HTMLFormMessage> in = newInputPort(this, "dataIn");
    public final OutputPort<HTMLFormMessage> out = newOutputPort(this, "dataOut");

    // state for the uploader
    private final Context _context;
    private final ConnectivityManager _manager;
    private final URI _uri;

    // retry mechanism
    protected final long[] _timeouts;
    protected int _timeoutIndex = 0;    
    private DefaultHttpClient httpclient;

    public HTTPFormUpload3(Context context, String uri, long[] timeoutsMs) throws CSenseException {
	try {
	    _context = context;
	    _manager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    _timeouts = timeoutsMs;
	    _uri = new URI(uri);

	    httpclient = new DefaultHttpClient();
	    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);

	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}
    }


    /**
     * @return true on a successful upload, false otherwise
     * @throws CSenseException when push failed
     */
    protected boolean upload(MultipartEntity entity) throws CSenseException {
	try {
	    debug("upload start");

	    HttpPost httppost = new HttpPost(_uri);
	    httppost.setEntity(entity);
	    HttpResponse response = httpclient.execute(httppost);
	    response.getEntity().consumeContent();
	    if (response.getStatusLine().getStatusCode() != 200) {
		warn("upload failed", response.getStatusLine());		
		return false;
	    } else {
		info("upload successful");
		_timeoutIndex = 0;		
		return true;
	    }		    
	} catch (ClientProtocolException e) {
	    debug("upload failed,", e);
	} catch (IOException e) {
	    debug("upload failed,", e);
	}
	return false;
    }

    protected long retry() {
	long to = _timeouts[_timeoutIndex]; 
	if (_timeoutIndex + 1 < _timeouts.length) 
	    _timeoutIndex += 1;

	return to;
    }

    @Override
    public void onInput() throws CSenseException {
	HTMLFormMessage form = in.getFrame();
	MultipartEntity entity = form.unbox();

	while(true) {
	    if (isNetworkConnected()) {
		boolean success = upload(entity);
		if (success) {
		    info("upload successful");
		    break;
		} else {
		    debug("upload failed");
		}
	    }
	    
	    long toMs = retry();
	    try {
		debug("sleeping for " + toMs + " ms");
		Thread.sleep(toMs);
	    } catch (InterruptedException e) {		
		e.printStackTrace();
		throw new CSenseException(CSenseError.INTERRUPTED_OPERATION, e);
	    }
	}
	out.push(form);

    }
    
    protected boolean isNetworkConnected() {
	NetworkInfo net = _manager.getActiveNetworkInfo();
	return net != null && net.isConnected();
    }
}
