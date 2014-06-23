package com.laranover.locationservice;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**Class that controls and manages the device's location*/

public class LocationHelper{

	private LocationManager lm;
	private Location location;
	private String provider;
	private LocationListener locationListener;
	private String fineProvider, coarseProvider;
	private boolean network_enabled, gps_enabled;
	
	private static final String TAG = "LocationHelper";
	
	public LocationHelper(Context context) {
		super();
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
			
	/**Method stops location updates.*/
	public void stopUpdates(){
		if(lm!=null)
			lm.removeUpdates(getLocationListener());
	}
		
	/**Method that defines location providers*/
	public void providers(){
		network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	/**Check if device's location is enabled.*/
	public boolean isLocationEnabled(){
		providers();
		if(!gps_enabled && !network_enabled)
			return false;
		else{
			checkLocationValues();
			if(location == null)
				return false;
			else
				return true;
		}
	}
		
	public void checkLocationValues(){
		getProviderEnabled();
		if(fineProvider!=null && coarseProvider==null){
			Log.d(TAG,"solo gps");
			location = requestUpdatesFromProvider(fineProvider);
			provider = fineProvider;
		}else if(coarseProvider!=null && fineProvider==null){
			Log.d(TAG,"solo network");
			location = requestUpdatesFromProvider(coarseProvider);
			provider = coarseProvider;
		}else if(fineProvider==null && coarseProvider==null)
			location = null;
		else if(fineProvider!=null && coarseProvider!=null)
			checkBestLocation();
	}
	
	/**Method that updates the Location Manager as the provider is active and returns the last location obtained
	 * @param provider Provider name
	 * @return Location Last location obtained.*/
	private Location requestUpdatesFromProvider(String provider){
		lm.requestLocationUpdates(provider, 0, 0, getLocationListener());
		return lm.getLastKnownLocation(provider);
	}
	
	/**Method defines the available location provider. 
	 * If GPS and Network providers are both enabled, 
	 * creates a criteria to obtain the most optimal.*/
	private void getProviderEnabled(){
		if(gps_enabled && !network_enabled)
			fineProvider = LocationManager.GPS_PROVIDER;
		else if(network_enabled && !gps_enabled)
			coarseProvider = LocationManager.NETWORK_PROVIDER;
		else if(gps_enabled && network_enabled){
			Log.d(TAG,"ambos activados");
			Criteria c = new Criteria();
			c.setAltitudeRequired(false);
			c.setBearingRequired(false);
			c.setCostAllowed(false);
			c.setPowerRequirement(Criteria.POWER_LOW); 
			
			c.setAccuracy(Criteria.ACCURACY_FINE);
			fineProvider = lm.getBestProvider(c, true);
			Log.d(TAG,"fineProvider: "+fineProvider);
			
			c.setAccuracy(Criteria.ACCURACY_COARSE);
			coarseProvider = lm.getBestProvider(c, true);
			Log.d(TAG,"CoarseProvider: "+coarseProvider);
		}
	}
	
	/**Method that checks the locations obtained by GPS and Network, and defines the Location object.*/
	public void checkBestLocation(){
		Location gpsLocation = requestUpdatesFromProvider(fineProvider);
		Location networkLocation = requestUpdatesFromProvider(coarseProvider);
	
		if(gpsLocation!=null && networkLocation!=null)
			location = getBetterLocation(gpsLocation, networkLocation);
		else if(gpsLocation!=null && networkLocation==null)
			location = gpsLocation;
		else if(gpsLocation==null && networkLocation!=null)
			location = networkLocation;
		else
			location = null;
	}
	
	/**Method that compares two given locations and returns the most convenient according 
	 * to the time it takes to cool off and the precision obtained.
	 * @param gpsLocation GPS location
	 * @param networkLocation Network location*/
	private Location getBetterLocation(Location gpsLocation, Location networkLocation){
		if (networkLocation == null) 
			return gpsLocation;
		
		long timeDelta = gpsLocation.getTime() - networkLocation.getTime();
		boolean isNewer = timeDelta > 0;
				
		int accuracyDelta = (int) (gpsLocation.getAccuracy() - networkLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		boolean isFromSameProvider = isSameProvider(gpsLocation.getProvider(), networkLocation.getProvider());
		if(isMoreAccurate)
			return gpsLocation;
		else if(isNewer && !isLessAccurate)
			return gpsLocation;
		else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return gpsLocation;

		return networkLocation;
	}
	
	/** Check if two providers are the same.*/
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) 
	      return provider2 == null;
	    
	    return provider1.equals(provider2);
	}
	
	/**Method returns a LocationListener object*/
	public LocationListener getLocationListener() {
		locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
			
			@Override
			public void onLocationChanged(Location location) {
				location.getLatitude();
				location.getLongitude();
			}
		};
		return locationListener;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isNetwork_enabled() {
		return network_enabled;
	}

	public boolean isGps_enabled() {
		return gps_enabled;
	}
	
	public String getProveedor(){
		return provider;	
	}
}
