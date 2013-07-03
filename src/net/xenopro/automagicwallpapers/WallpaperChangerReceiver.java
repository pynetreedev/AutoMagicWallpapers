package net.xenopro.automagicwallpapers;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WallpaperChangerReceiver extends BroadcastReceiver 
{
	
	@Override
	public void onReceive(Context context, Intent arg1) 
	{
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    Intent i = new Intent(context, WallpaperChangerService.class);
		    PendingIntent pending = PendingIntent.getService(context, 0, i,PendingIntent.FLAG_CANCEL_CURRENT);
		    Calendar cal = Calendar.getInstance();
		    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		    if(sharedPrefs.getBoolean("serviceRunning", false))
		    {
			    int seconds = Integer.parseInt(sharedPrefs.getString("wallpaperFrequency", "300"));
		        cal.add(Calendar.SECOND, seconds);
			    service.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), seconds*1000, pending);
		    }
	}

}
