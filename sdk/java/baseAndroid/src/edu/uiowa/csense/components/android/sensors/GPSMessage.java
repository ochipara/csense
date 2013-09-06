package edu.uiowa.csense.components.android.sensors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.FramePool;
import edu.uiowa.csense.runtime.types.JavaFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import android.location.Location;

public class GPSMessage extends JavaFrame<GPSMessage> {
    protected double longitude;
    protected double latitude;
    protected double accuracy;

    public GPSMessage(FramePool pool, TypeInfo type) throws CSenseException {
	super(pool, type);
    }

    public double getLongitude() {
	return longitude;
    }

    public double getLatitude() {
	return latitude;
    }

    public double getAccuracy() {
	return accuracy;
    }

    @Override
    public void initialize() {
	super.initialize();
	longitude = 0;
	latitude = 0;
	accuracy = 0;
    }

    public void update(Location location) {
	longitude = location.getLongitude();
	latitude = location.getLatitude();
	accuracy = location.getAccuracy();				
    }

}
