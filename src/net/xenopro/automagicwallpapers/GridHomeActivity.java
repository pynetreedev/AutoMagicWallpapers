package net.xenopro.automagicwallpapers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Calendar;

import net.xenopro.automagicwallpapers.Constants.Extra;
import net.xenopro.automagicwallpaperspro.R;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class GridHomeActivity extends BaseActivity {
	
	public Dialog mSplashDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getOverflowMenu();
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        networkConnection = haveNetworkConnection();
		setContentView(R.layout.activity_gridhome);
		try
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			homecontext = this;
	        lblConnection = (TextView) findViewById(R.id.lblNoConnection);
			grid = (GridView) findViewById(R.id.gridview);
//            mSplashDialog = new Dialog(homecontext, R.style.Theme_Splash);
//            mSplashDialog.setContentView(R.layout.activity_splashscreen);
//            mSplashDialog.setCancelable(false);
//            mSplashDialog.show();
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = sharedPrefs.edit();
			if(sharedPrefs.getBoolean("firstTime", true))
			{
				AssetManager assets = getResources().getAssets();
				String[] files = assets.list("Images");
				Bitmap bmap = null;
				for(int i = 0; i < files.length; i++)
				{
					String Filename = files[i];
					InputStream input = assets.open("Images/" + Filename);
					bmap = BitmapFactory.decodeStream(input);
					if(bmap != null)
					{
						File dest = new File(Environment.getExternalStoragePublicDirectory("AutoMagicWallpapers"), Filename);
		           	     FileOutputStream out = new FileOutputStream(dest);
		           	     if(files[i].matches("png"))
		           	     {
		           	    	 bmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		           	     }
		           	     else
		           	     {
		           	    	 bmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		           	     }
		           	     out.flush();
		           	     out.close();
					}
	           	    bmap = null;
				}
				editor.putBoolean("firstTime", false);
			}
			if(sharedPrefs.getBoolean("serviceRunning", false))
			{
				editor.putBoolean("serviceRunning", true);
			}
			else
			{
				editor.putBoolean("serviceRunning", false);
			}
			editor.commit();
			
			if(networkConnection)
			{
				lblConnection.setVisibility(View.GONE);
		    	DownloadTask downloadTask = new DownloadTask();
		        /** Starting the task created above */
		        downloadTask.execute();
			}
			else
			{
				lblConnection.setVisibility(View.VISIBLE);
				//mSplashDialog.dismiss();
			}
		}
		 catch (Exception e) 
		 {
			 lblConnection.setVisibility(View.VISIBLE);
		 }
		 

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    boolean magic = sharedPrefs.getBoolean("serviceRunning", false);
	    menu.findItem(R.id.item_start_magic).setVisible(!magic);
	    menu.findItem(R.id.item_end_magic).setVisible(magic);
//	    sv = ShowcaseView.insertShowcaseViewWithType(ShowcaseView.ITEM_ACTION_ITEM, R.id.item_art, this,
//                "ShowcaseView & action items", "Try touching action items to showcase them", mOptions);
		return true;
	}

	//@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		switch (item.getItemId()) {
			case R.id.item_start_magic:
					int seconds = Integer.parseInt(sharedPrefs.getString("wallpaperFrequency", "300"));
					
					editor.putBoolean("serviceRunning", true);
					editor.commit();
				   Intent myIntent = new Intent(this, WallpaperChangerService.class);
				   pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

			        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			        Calendar calendar = Calendar.getInstance();
			        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), seconds*1000, pendingIntent);
			        supportInvalidateOptionsMenu();
			        return true;
			case R.id.item_end_magic:
				int FileOrder = sharedPrefs.getInt("CurrentImage", 0);
				editor.putBoolean("serviceRunning", false);	
				editor.putInt("CurrentImage", FileOrder + 1);
				editor.commit();
				Intent endIntent = new Intent(this, WallpaperChangerService.class);
				pendingIntent = PendingIntent.getService(this, 0, endIntent, 0);
		        AlarmManager alarmManagerstop = (AlarmManager)getSystemService(ALARM_SERVICE);
	            alarmManagerstop.cancel(pendingIntent);
	            supportInvalidateOptionsMenu();
			        return true;
			case R.id.item_art:
//				sv.setShowcaseItem(ShowcaseView.ITEM_ACTION_ITEM, R.id.item_art, this);
				Intent artintent = new Intent(this, ImageListActivity.class);
				artintent.putExtra(Extra.CATEGORYNAME, "art");
				imageLoader.stop();
				startActivity(artintent);
			        return true;
			case R.id.item_photography:
				Intent photointent = new Intent(this, ImageListActivity.class);
				photointent.putExtra(Extra.CATEGORYNAME, "photo");
				imageLoader.stop();
				startActivity(photointent);
			        return true;
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			        return true;
			default:
				return false;
		}
	}
	
	private void getOverflowMenu() {
		 
	    try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	         Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	         if (menuKeyField != null) {
	             menuKeyField.setAccessible(true);
	             menuKeyField.setBoolean(config, false);
	         }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePager.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		finish();
	}
	
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog pd;
        

        protected void onPreExecute() {
        	//startSplashScreen();
                 pd = new ProgressDialog(homecontext);
                 pd.setMessage("Loading Images...");
                 pd.setCancelable(false);
                 pd.setIndeterminate(true);
                 pd.show();

        }
        protected String doInBackground(String... params) {
			String result = "Test";
        	try{
            	JSONParsor jparsor = new JSONParsor();
				imageUrls = jparsor.getImages(categoryMain);
            }catch(Exception e){
                
            }
			return result;
        }
       
 
        protected void onPostExecute(String result) {
        	try
        	{
	        	if(imageUrls != null)
	        	{
					imageLoader.handleSlowNetwork(true);
					GridImageAdapter = new ImageAdapter();
					// Bundle bundle = getIntent().getExtras();
					// imageUrls = bundle.getStringArray(Extra.IMAGES);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.ic_stubblank).cacheOnDisc()
							.showImageOnFail(R.drawable.ic_stubblank)
							.imageScaleType(ImageScaleType.EXACTLY)
							.displayer(new FadeInBitmapDisplayer(1000))
							.bitmapConfig(Bitmap.Config.RGB_565).build();
					
					grid.setAdapter(GridImageAdapter);
					grid.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							startImagePagerActivity(position);
						}
					});
		        }
	        	else
	        	{
	        		lblConnection.setVisibility(View.VISIBLE);
	        	}
				 
        	}
        	catch (Exception e)
        	{
        		lblConnection.setVisibility(View.VISIBLE);
        	}
        	pd.dismiss();
        }
    }

}
