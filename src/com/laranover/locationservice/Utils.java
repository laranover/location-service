package com.laranover.locationservice;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public final class Utils {
	
	private static final int START_DELAY = 10;
	private static final long UPDATE_INTERVAL = 5 * 60 * 1000; //5 MINUTES
	
	private static final String TAG = "Utils";
	
	
	public static void startService(Context context) {
		Log.d(TAG, "Start service...");
   	    Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, START_DELAY);
        Intent intent = new Intent(context, LocationReceiver.class);
        PendingIntent tracking = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), UPDATE_INTERVAL, tracking);
	}
	
	public static void stopService(Context context){
		Log.d(TAG, "Stop service...");
	   	Intent intent = new Intent(context, LocationReceiver.class);
	   	PendingIntent tracking = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	   	AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(tracking);
	}
	
	public static void showToast(Context context, String message){
	   	Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
