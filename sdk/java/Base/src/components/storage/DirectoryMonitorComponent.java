package components.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import compatibility.Environment;

import api.CSenseErrors;
import api.CSenseException;
import api.CSenseSource;
import api.IOutPort;
import api.TimerTask;

import messages.TypeInfo;
import messages.fixed.CharVector;

public class DirectoryMonitorComponent extends CSenseSource<CharVector> {
    public IOutPort<CharVector> fileName = newOutputPort(this, "fileName");
    public static final int maxPathLength = 1024;
    public final Map<File, Boolean> fileList = new HashMap<File, Boolean>();

    private TimerTask _monitorTimerTask = new TimerTask() {
	@Override
	public void run() {
	    if (_dir.exists() && _lastModified < _dir.lastModified()) {
		File[] files = _dir.listFiles();
		long lastModified = _lastModified;
		for (int i = 0; i < files.length; i++) {
		    File f = files[i];

		    if (_lastModified < files[i].lastModified()) {
			if (matches(f.getName())) {
			    if (fileList.containsKey(f) == false) {
				fileList.put(f, false);
			    }
			    lastModified = lastModified < files[i]
				    .lastModified() ? files[i].lastModified()
				    : lastModified;
			}
		    }
		}
		_lastModified = lastModified;
	    }

	    for (File f : fileList.keySet()) {
		if (fileList.get(f) == false) {
		    CharVector msg = getNextMessageToWriteInto();
		    msg.put(f.getPath());
		    msg.flip();
		    try {
			fileName.push(msg);
			fileList.put(f, true);
		    } catch (CSenseException e) {
			if (e.error() != CSenseErrors.QUEUE_FULL) {
			    e.printStackTrace();
			}
			msg.free();
			break;
		    }

		}
	    }

	    getScheduler().schedule(getOwner(), this, 1000);
	}

	boolean matches(String name) {
	    if (_pattern == null)
		return true;
	    if (_pattern == "")
		return true;
	    return name.matches(_pattern);
	}
    };

    private File _dir;
    private long _lastModified;
    private String _pattern;

    public DirectoryMonitorComponent(String dir, String pattern)
	    throws CSenseException {
	super(TypeInfo.newCharVector(maxPathLength));
	_dir = new File(Environment.environment.getExternalStorageDirectory(),
		dir);
	_pattern = pattern;
    }

    @Override
    public void onStart() throws CSenseException {
	getScheduler().schedule(this, _monitorTimerTask, 1000);
    }

    @Override
    public void onStop() {
	// getScheduler().cancel(_monitorTimerTask);
    }

}
