package com.baseAndroid;

import compatibility.Log;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class DetermineGPSLocation implements LocationListener{
	
	private static LocationManager mLocationManager;
	private static Location mLocation;
	private static Context mContext;
	private static Criteria mCriteria;
	private static double Longi=0, Lati=0;
	private static String BestProvider;
	public DetermineGPSLocation(Context context){
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mCriteria = new Criteria();
		BestProvider = mLocationManager.getBestProvider(mCriteria, true);
		mLocation = mLocationManager.getLastKnownLocation(BestProvider);
		//using the best GPS provider
		mLocationManager.requestLocationUpdates(BestProvider, 0, 0, this);
	}
	@Override
	public void onLocationChanged(Location location) {
//		location = mLocation;
		Longi = location.getLongitude();
		Lati = location.getLatitude();
		Log.i("Longitude:"+Longi+",Latitude:"+Lati);
	}
	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	/**
	 * Getter for Longitude
	 * 
	 * @return Longitude , double value
	 * */
	public double getLong(){
		return Longi;
	}
	/**
	 * Getter for Latitude
	 * 
	 * @return Latitude, double value
	 * */
	public double getLat(){
		return Lati;
	}
	
	/**
	 * Stops the GPS update for this app. This will keep the 
	 * */
	public void StopGPSUpdate(){	
		mLocationManager.removeUpdates(this);	
		mLocationManager = null;
		Log.i("Done with removing updates");
	}
	
	/**
	 * Start the GPS for this app to give updates
	 * */
	public void StartGPSUpdate(){
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(BestProvider);
		//using the best GPS Provider
		mLocationManager.requestLocationUpdates(BestProvider, 0, 0, this);
	}
}
