package com.laranover.locationservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LocationService extends Service{

	private Looper serviceLooper;
	private ServiceHandler serviceHandler;
	private Location location;
	private LocationHelper locationHelper;
	
	private String provider;
	
	private static final String TAG = "LocationService";
	
private final class ServiceHandler extends Handler{
		
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d(TAG, "handleMessage");
			locationHelper = new LocationHelper(getBaseContext());
			locationHelper.providers();
			if(!locationHelper.isGps_enabled() && !locationHelper.isNetwork_enabled())
				provider = "";
			else{
				locationHelper.checkLocationValues();
				location = locationHelper.getLocation();
				if(location!=null)
					provider = location.getProvider();
				else
					provider = locationHelper.getProveedor();
			}
			if(location!=null)
				serviceHandler.post(new MakeToast("Location: "+location.getLatitude() +" , "+location.getLongitude()+" - Type: "+provider));
			if(location==null && provider.length()!=0)
				serviceHandler.post(new MakeToast("Could not determine the location of the device"));
			if(provider.length()==0)
				serviceHandler.post(new MakeToast("Please turn on location services mobile"));
			stopSelf(msg.arg1);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "OnCreate");
		HandlerThread thread = new HandlerThread("ServiceStartArguments",android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		serviceHandler.sendMessage(msg);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		//Stop LocationManager updates
		if(locationHelper!=null)
			locationHelper.stopUpdates();
	}
	
	private class MakeToast implements Runnable {
		String txt;
		public MakeToast(String text){
			txt = text;
		}
		public void run(){
		    Log.d(TAG,"Run: "+txt); 
	    	Utils.showToast(getBaseContext(), txt);
		}
	}
}
