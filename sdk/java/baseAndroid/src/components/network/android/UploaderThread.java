package components.network.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.IComponent;
import edu.uiowa.csense.runtime.api.IScheduler;
import edu.uiowa.csense.runtime.compatibility.Log;
import edu.uiowa.csense.runtime.v4.CSenseComponent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class UploaderThread extends Thread {
	protected HttpURLConnection connection = null;
	protected final byte[] _buf = new byte[1024 * 4];
	protected final long[] _timeouts = {30, 60, 120, 300, 900};
	protected int _timeoutIndex = 3;

	public static final int INIT = 0;
	public static final int HAS_FILE = 1;
	public static final int HAS_NETWORK = 2;
	public static final int UPLOADING = 3;
	public static final int READY = 4;
	public static final int STOP = 5;

	protected boolean _running = true;
	protected ConnectivityManager _manager;
	protected final URI _uploadUrl;
	protected final MultipartEntity _form;
	protected NetworkInfo _net = null;
	protected final IScheduler _scheduler;
	protected final CSenseComponent _component;

	public UploaderThread(ConnectivityManager connMgr, URI uploadUrl, MultipartEntity form, IScheduler scheduler, CSenseComponent componet)  {		
		super("uploader");
		_manager = connMgr;
		_uploadUrl = uploadUrl;
		_form = form;
		_scheduler = scheduler;
		_component = componet;
	}

	/**
	 * 
	 * @return the timeout for the operation
	 */
	private long retry() {
		if (_timeoutIndex + 1 < _timeouts.length) {
			_timeoutIndex += 1;
		}

		return _timeouts[_timeoutIndex] * 1000;
	}

	private boolean checkNetwork() {
		if (_net == null) _net = _manager.getActiveNetworkInfo();
		if (_net != null) {
			return _net.isConnected();
		}
		return false;
	}

	private boolean upload() {
		try {
			HttpPost httppost = new HttpPost(_uploadUrl);
			HttpClient httpclient = new DefaultHttpClient();


			httppost.setEntity(_form);
			HttpResponse response;

			response = httpclient.execute(httppost);

			int code = response.getStatusLine().getStatusCode();

			if (code != 200) {
				Log.e("uploader-thread", "upload failed", response.getStatusLine());
				return false;
			} else {
				Log.i("uploader-thread", "upload successful");
				return true;
			}			
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		
		return false;
	}

	@Override
	public void run() {
		while(_running) {
			try {
				Thread.sleep(retry());
			} catch (InterruptedException e1) {						 
				// do nothing
				if (_running == false) break;
			}
			
			if (checkNetwork()) {
				if (upload()) break;				
			} 
		}
		
		if (_running) {
			_scheduler.schedule(_component, _component.asTask());
		}
	}
}