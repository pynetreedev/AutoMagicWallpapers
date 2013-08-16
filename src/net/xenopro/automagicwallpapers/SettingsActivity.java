package net.xenopro.automagicwallpapers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import net.xenopro.automagicwallpaperspro.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingsActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final ListPreference dataPref = (ListPreference) findPreference("wallpaperFrequency");
        final ListPreference sortPref = (ListPreference) findPreference("imageOrderSort");
        int index = dataPref.findIndexOfValue(String.valueOf(sharedPrefs.getString("wallpaperFrequency", "300")));
        //get the label of the new value selected
        if(index == -1)
        {
        	index = 0;
        }
        String label = (String) dataPref.getEntries()[index];
        dataPref.setSummary(label);
        index = sortPref.findIndexOfValue(String.valueOf(sharedPrefs.getString("imageOrderSort", "nosort")));
        //get the label of the new value selected
        if(index == -1)
        {
        	index = 2;
        }
        label = (String) sortPref.getEntries()[index];
        sortPref.setSummary(label);
        Preference clearDisc = (Preference) findPreference("clearDiscCache");
        clearDisc.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 ImageLoader.getInstance().clearDiscCache();
                    	 return true;
                     }
                 });
        CheckBoxPreference check = (CheckBoxPreference) findPreference("toastMessages");
        check.setChecked(sharedPrefs.getBoolean("toastMessages", true));
        check.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				Boolean toast = Boolean.valueOf(arg1.toString());
				SetToastMessages(toast);
				return true;
			}
        });
        Preference aboutUs = (Preference) findPreference("aboutUs");
        aboutUs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 OpenAboutUs();
                    	 return true;
                     }
                 });
        Preference contactUs = (Preference) findPreference("contactUs");
        contactUs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 ContactUs();
                    	 return true;
                     }
                 });
        Preference randomizeFiles = (Preference) findPreference("randomizeFiles");
        randomizeFiles.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                    	 RandomizeFiles();
                    	 return true;
                     }
                 });
        //get the description from the selected item
          //     
 
            //when the user choose other item the description changes too with the selected item
            dataPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    int index = dataPref.findIndexOfValue(String.valueOf(o.toString()));
                    //get the label of the new value selected
                    String label = (String) dataPref.getEntries()[index];

                    preference.setSummary(label);
                    onOptionsItemSelected(o.toString());
                    return true;
                
            }});
            
            sortPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    int index = sortPref.findIndexOfValue(String.valueOf(o.toString()));
                    //get the label of the new value selected
                    String label = (String) sortPref.getEntries()[index];
                    
                    preference.setSummary(label);
                    SetImageSort(o.toString());
                    return true;
                
            }});
    }
	
    public void onOptionsItemSelected(String Seconds) {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if(sharedPrefs.getBoolean("serviceRunning", false))
    	{
			Intent myIntent = new Intent(this, WallpaperChangerService.class);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
	        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	        //alarmManager.cancel(pendingIntent);
	        Calendar calendar = Calendar.getInstance();
	        int seconds = Integer.parseInt(Seconds);
	        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), seconds*1000, pendingIntent);
	        Editor editor = sharedPrefs.edit();
	        editor.putString("wallpaperFrequency", Seconds);
	        editor.commit();
    	}
    }
    
    public void OpenAboutUs()
    {
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
    }
    
    public void ContactUs()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@xenopro.net"});
        i.putExtra(Intent.EXTRA_SUBJECT, "AutoMagic Wallpapers Support");
        i.setType("message/rfc822");
        startActivity(i);
    }
    
    public void SetImageSort(String sort)
    {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		editor.putString("imageOrderSort", sort);
		editor.commit();
		RandomizeFiles();
    }
    
    public void SetToastMessages(Boolean toast)
    {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean("toastMessages", toast);
		editor.commit();
    }
	
	public void RandomizeFiles()
	{
		StringBuilder sb = new StringBuilder();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		try
		{
		File Filepath;
		File[] pictures;
		String[] sortedPictures;
		String randomizedFiles = "";
		if(sharedPrefs.getString("SelectedFolders", "") != null && !sharedPrefs.getString("SelectedFolders", "").isEmpty())
		{
			String[] selectedFoldersArray = sharedPrefs.getString("SelectedFolders", "").split(",");
			for(int i = 0; i < selectedFoldersArray.length; i++)
			{
				String selected = selectedFoldersArray[i];
				if(selected.equals("Camera"))
				{
					Filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
				}
				else
				{
					Filepath = new File(Environment.getExternalStorageDirectory(), selected);
				}
				pictures = Filepath.listFiles(new FilenameFilter() {
					   @Override
					   public boolean accept(File dir, String name) {
					      File f = new File(dir, name);
					      return !f.isDirectory() && f.canRead() && (f.getAbsolutePath().endsWith(".png") || f.getAbsolutePath().endsWith(".jpg"));
					   }
					});
				//Arrays.sort(pictures);
				for(int ii = 0; ii < pictures.length; ii++)
				{
					File temp = pictures[ii];
					sb.append(temp.getAbsolutePath()).append(",");
				}
				
			}
		}
		else
		{
			Filepath = new File(Environment.getExternalStorageDirectory(), "AutoMagicWallpapers");
			pictures = Filepath.listFiles(new FilenameFilter() {
				   @Override
				   public boolean accept(File dir, String name) {
				      File f = new File(dir, name);
				      return !f.isDirectory() && f.canRead() && (f.getAbsolutePath().endsWith(".png") || f.getAbsolutePath().endsWith(".jpg"));
				   }
				});
			//Arrays.sort(pictures);
			for(int ii = 0; ii < pictures.length; ii++)
			{
				File temp = pictures[ii];
				sb.append(temp.getAbsolutePath()).append(",");
			}
		}
		if(sb.length() > 0)
		{
			sb.setLength(sb.length() - 1);
		}
		randomizedFiles = sb.toString();
		sortedPictures = randomizedFiles.split(",");
		String sortType = sharedPrefs.getString("imageOrderSort", "nosort");
		if(!sortType.contains("nosort"))
		{
			sb.setLength(0);
			if(sortType.contains("random"))
			{
				shuffleArray(sortedPictures);
			}
			else
			{
				Arrays.sort(sortedPictures);
			}
			for(int i = 0; i < sortedPictures.length; i++)
			{
				sb.append(sortedPictures[i]).append(",");
			}
			sb.setLength(sb.length() - 1);
		}
		randomizedFiles = sb.toString();
		Editor editor = sharedPrefs.edit();
		editor.putString("ImageOrder", randomizedFiles);
		editor.putInt("CurrentImage", 0);
		editor.commit();
		}
		catch(Exception e)
		{
			Editor editor = sharedPrefs.edit();
			editor.putString("ImageOrder", "");
			editor.putInt("CurrentImage", 0);
			editor.putString("SelectedFolders", "");
			editor.commit();
			Toast.makeText(SettingsActivity.this, "Error Selecting Rotating Images. Please select folders again.", Toast.LENGTH_LONG).show();
		}
	}
	
	  static void shuffleArray(String[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i >= 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }

}
