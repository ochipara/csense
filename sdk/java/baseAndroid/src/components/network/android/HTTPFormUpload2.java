package components.network.android;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import edu.uiowa.csense.components.network.HTMLFormMessage;
import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.ICommandHandler;
import edu.uiowa.csense.runtime.api.InputPort;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.api.Task;
import edu.uiowa.csense.runtime.api.TimerEvent;
import edu.uiowa.csense.runtime.v4.CSenseComponent;

/**
 * This allows you to upload a form. It uses events rather than a thread. This has the advantage
 * of making the state of the component more visible within the scheduler.
 * 
 * @author ochipara
 *
 */
public class HTTPFormUpload2 extends CSenseComponent{
    private class HTTPUploader extends CSenseInnerThread {
	private HttpClient httpclient;
	private HttpPost httppost;
	private HTTPFormUpload2 parent;
	public HTTPUploader(String threadName, URI uri) {
	    super(threadName);
	    httppost = new HttpPost(uri);
	    httpclient = new DefaultHttpClient();
	    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
	    parent = HTTPFormUpload2.this;
	}
	
	@Override
	public void doRun() {
	    while(!Thread.interrupted()) {
		synchronized(parent) {
		    while(_inForm == null || !isNetworkConnected()) {
			try {
			    info("wait to upload or for network connectivity");
			    parent.wait();
			} catch (InterruptedException e) {
			    info("interrupted to quit when waiting");
			    return;
			}
		    }
		}

		try {
		    upload();
		} catch (CSenseException e) {
		    info("quit uploading,", e);
		    return;
		}
	    }
	    debug("shut down HTTP client");
	    httpclient.getConnectionManager().shutdown();
	}
	
	public void quit() {
	    debug("abort HTTP post");
	    httppost.abort();
	    debug("interrupt HTTP uploader");
	    interrupt();
	    while(true) {
		try {
		    debug("join HTTP uploader");
		    join();
		    break;
		} catch (InterruptedException e) {
		    warn("interrupted? NO WAY, retry");
		}
	    }
	    debug("HTTP uploader exits");
	}
	
	/**
	 * @return true on a successful upload, false otherwise
	 * @throws CSenseException when push failed
	 */
	protected boolean upload() throws CSenseException {
	    try {
		debug("upload start");
		httppost.setEntity(_inForm.getForm());
		HttpResponse response = httpclient.execute(httppost);
		response.getEntity().consumeContent();
		if (response.getStatusLine().getStatusCode() != 200) {
		    warn("upload failed", response.getStatusLine());		
		    return false;
		} else {
		    info("upload successful");
		    _timeoutIndex = 0;
		    _outForm = _inForm;
		    _inForm = null;
		    return true;
		}		    
	    } catch (ClientProtocolException e) {
		debug("upload failed,", e);
	    } catch (IOException e) {
		debug("upload failed,", e);
	    }
	    return false;
	}
    }
    
    public final InputPort<HTMLFormMessage> in = newInputPort(this, "dataIn");
    public final OutputPort<HTMLFormMessage> out = newOutputPort(this, "dataOut");

    // state for the uploader
    private final Context _context;
    private final ConnectivityManager _manager;
    private final HTTPUploader _uploader;

    // retry mechanism
    protected final long[] _timeouts;
    protected int _timeoutIndex = 0;
    private TimerEvent checkConnectionEvent= new TimerEvent();

    // data
    HTMLFormMessage _inForm;
    HTMLFormMessage _outForm;

    // add notifier
    List<ICommandHandler> handlers = new LinkedList<ICommandHandler>();

    public HTTPFormUpload2(Context context, String uri, long[] timeoutsMs) throws CSenseException {
	try {
	    _context = context;
	    _manager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    _timeouts = timeoutsMs;
	    _uploader = new HTTPUploader(HTTPUploader.class.getSimpleName(), new URI(uri));
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    throw new CSenseException(e);
	}
    }

    protected long retry() {
	long to = _timeouts[_timeoutIndex]; 
	if (_timeoutIndex + 1 < _timeouts.length) 
	    _timeoutIndex += 1;
	
	return to;
    }

    protected boolean isNetworkConnected() {
	NetworkInfo net = _manager.getActiveNetworkInfo();
	return net != null && net.isConnected();
    }
    
    @Override
    public void onStart() throws CSenseException {
	super.onStart();
	_uploader.start();
    }
    
    @Override
    public void onStop() throws CSenseException {
	getScheduler().cancel(checkConnectionEvent);
	_uploader.quit();
	super.onStop();	
    }

    private IResult doOutput() throws CSenseException {
	if(_outForm == null) return IResult.PUSH_SUCCESS;
	IResult ret = out.push(_outForm);
	if(ret != IResult.PUSH_DROP) _outForm = null;
	return ret;
    }
    
    @Override
    public void onInput() throws CSenseException {
	if (_inForm == null && doOutput() != IResult.PUSH_DROP) {
	    _inForm = in.getFrame();
	    synchronized(this) {
		debug("incoming file to upload, wake up the uploader");
		notify();
	    }
	} else {
	    in.getFrame().drop();
	    ready();
	    accumulateResult(IResult.PUSH_FAILED);
	}
    }

    @Override
    public void doEvent(Task t) throws CSenseException {
	if(doOutput() != IResult.PUSH_DROP && isNetworkConnected()) {
	    synchronized(this) {
		debug("network available, wake up the uploader");
		notify();
	    }
	}
	
	long retry = retry();
	getScheduler().schedule(this, checkConnectionEvent, retry, TimeUnit.MILLISECONDS);
	info("next netwrok connectivity check in " + retry / 1000 + " secs");	
    }
    
    public void registerCallback(ICommandHandler handler) {
	if (handlers.contains(handler) == false) handlers.add(handler);
    }

}
