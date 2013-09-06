package edu.uiowa.csense.components.android.sensors;

import edu.uiowa.csense.runtime.api.CSenseException;
import edu.uiowa.csense.runtime.api.Frame;
import edu.uiowa.csense.runtime.api.OutputPort;
import edu.uiowa.csense.runtime.types.JavaFrame;
import edu.uiowa.csense.runtime.types.TypeInfo;
import edu.uiowa.csense.runtime.v4.CSenseSource;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class GPSComponent extends CSenseSource<JavaFrame<GPSMessage>> implements LocationListener {
    public final OutputPort<JavaFrame<GPSMessage>> out = newOutputPort(this, "out");	
    private static LocationManager _locationManager = null;
    private static Location _location = null;
    private static Context _context;
    private static String _bestProvider = null;
    private static final int TWO_MINUTES = 1000 * 60 * 2;


    public GPSComponent(TypeInfo type, Context context) throws CSenseException {
	super(type);
	_context = context;		

	_locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void onLocationChanged(Location newLocation) {
	if (isBetterLocation(newLocation, _location)) {
	    _location = newLocation;
	}
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	if (currentBestLocation == null) {
	    // A new location is always better than no location
	    return true;
	}

	// Check whether the new location fix is newer or older
	long timeDelta = location.getTime() - currentBestLocation.getTime();
	boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	boolean isNewer = timeDelta > 0;

	// If it's been more than two minutes since the current location, use the new location
	// because the user has likely moved
	if (isSignificantlyNewer) {
	    return true;
	    // If the new location is more than two minutes older, it must be worse
	} else if (isSignificantlyOlder) {
	    return false;
	}

	// Check whether the new location fix is more or less accurate
	int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	boolean isLessAccurate = accuracyDelta > 0;
	boolean isMoreAccurate = accuracyDelta < 0;
	boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	// Check if the old and new location are from the same provider
	boolean isFromSameProvider = isSameProvider(location.getProvider(),
		currentBestLocation.getProvider());

	// Determine location quality using a combination of timeliness and accuracy
	if (isMoreAccurate) {
	    return true;
	} else if (isNewer && !isLessAccurate) {
	    return true;
	} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	    return true;
	}
	return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
	if (provider1 == null) {
	    return provider2 == null;
	}
	return provider1.equals(provider2);
    }
    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }


    @Override
    public void onStart() throws CSenseException {
	super.onStart();	
	_bestProvider = _locationManager.getBestProvider(new Criteria(), true);
	if (_bestProvider != null) {	    
	    _location = _locationManager.getLastKnownLocation(_bestProvider);
	    //using the best GPS Provider
	    _locationManager.requestLocationUpdates(_bestProvider, 0, 0, this, Looper.getMainLooper());
	}
    }


    @Override
    public void onStop() throws CSenseException {
	super.onStop();
	_locationManager.removeUpdates(this);
    }


    @Override
    public Frame onPoll(OutputPort<? extends Frame> port) throws CSenseException {		
	if (_location != null) { 
	    JavaFrame<GPSMessage> jm = getNextMessageToWriteInto();
	    GPSMessage m = jm.unbox();
	    m.update(_location);
	    
	    return jm;
	} else {
	    info("no location");
	}
	return null;
    }
}
