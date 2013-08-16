package net.xenopro.automagicwallpapers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.xenopro.automagicwallpapers.Constants.Extra;
import net.xenopro.automagicwallpapers.R;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImagePager extends BaseActivity {
	
	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;
	String[] imageUrls;
	int pagerposition;
	Bitmap bmap;
	ViewPager pager;
	ImageView imageView;
	String Filepath;
	String Filename;
	HttpURLConnection source; 
	private Context context;
	Boolean setWallpaper;

	/** Called when the activity is first created. */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.pager_menu, menu);
//	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//	    boolean magic = sharedPrefs.getBoolean("serviceRunning", false);
//	    menu.findItem(R.id.item_start_magic).setVisible(!magic);
//	    menu.findItem(R.id.item_end_magic).setVisible(magic);
		return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setTheme(R.style.Theme_Sherlock);
	    setContentView(R.layout.ac_image_pager);
	    getAd();
	    networkConnection = haveNetworkConnection();
	    context = this;
	    
	    Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);
		pagerposition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		Filepath = imageUrls[pagerposition];
		Filename = Filepath.substring(Filepath.lastIndexOf('/') + 1);
		String[] filnamesplit = Filename.split("\\.");
		TextView t = (TextView)findViewById(R.id.txtimageName);
		t.setText(filnamesplit[0]);

		if (savedInstanceState != null) {
			pagerposition = savedInstanceState.getInt(STATE_POSITION);
		}

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_stubblank).cacheOnDisc()
			.showImageOnFail(R.drawable.ic_stubblank)
			.imageScaleType(ImageScaleType.EXACTLY)
			.resetViewBeforeLoading()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(1000))
			.build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerposition);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
	         
	         public void onPageSelected(int position)
	         {
	     		Filepath = imageUrls[position];
	     		pagerposition = position;
	    		Filename = Filepath.substring(Filepath.lastIndexOf('/') + 1);
	    		String[] filnamesplit = Filename.split("\\.");
	    		TextView t = (TextView)findViewById(R.id.txtimageName);
	    		t.setText(filnamesplit[0]);
	         }

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

	      });
	}

	//@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		networkConnection = haveNetworkConnection();
		switch (item.getItemId()) {
			case R.id.item_download:
				if(networkConnection)
				{
			    	String downPath = Filepath.replace(" ", "%20");
			    	downPath = downPath.replace("thumbs/", "");
			    	DownloadTask downloadTask = new DownloadTask();
			    	setWallpaper = false;
			        /** Starting the task created above */
			        downloadTask.execute(downPath);
				}
				else
				{
					this.finish();
	        		grid.setVisibility(View.GONE);
	        		lblConnection.setVisibility(View.VISIBLE);
				}
			        return true;
			case R.id.item_setWallpaper:
				if(networkConnection)
				{
			    	String newPath = Filepath.replace(" ", "%20");
			    	newPath = newPath.replace("thumbs/", "");
			    	DownloadTask downloadTaskWallpaper = new DownloadTask();
			    	setWallpaper = true;
			        /** Starting the task created above */
			        downloadTaskWallpaper.execute(newPath);
				}
				else
				{
					this.finish();
	        		grid.setVisibility(View.GONE);
	        		lblConnection.setVisibility(View.VISIBLE);
				}
			        return true;
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			        return true;
			default:
				return false;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}
	
	public void ShowInfo(View view) 
    {
		Intent intent = new Intent(this, WikiInfo.class);
		startActivity(intent);
    }
    
    private Bitmap downloadUrl(String strUrl) throws IOException{
        Bitmap bitmap=null;
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);
            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            
            
            /** Connecting to url */
            urlConnection.connect();
            //urlConnection.setUseCaches(false);
            /** Reading data from url */
            iStream = urlConnection.getInputStream();
 
            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);
 
        }catch(Exception e){
        	
        }finally{
            iStream.close();
        }
        return bitmap;
    }
    
    private class DownloadTask extends AsyncTask<String, Integer, Bitmap>{
        Bitmap bitmap = null;
        private ProgressDialog pd;

        @Override

        protected void onPreExecute() {

                 pd = new ProgressDialog(context);
                 
                 if(setWallpaper)
                 {
                	 pd.setMessage("Setting Wallpaper...");
                 }
                 else
                 {
                	 pd.setMessage("Downloading Image...");
                 }

                 pd.setCancelable(false);

                 pd.setIndeterminate(true);

                 pd.show();

        }
        @Override
        protected Bitmap doInBackground(String... url) {
            try{
                bitmap = downloadUrl(url[0]);
            }catch(Exception e){
                
            }
            return bitmap;
        }
        
        
 
        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(Bitmap result) {
            /** Getting a reference to ImageView to display the
            * downloaded image
            */
        	String toastMessage = "Download Complete";
            try 
            {
//            	View currentView = pager.findViewWithTag(pager.getCurrentItem());
//            	ImageView currentImageView = (ImageView) currentView.findViewById(R.id.image);
//            	currentImageView.buildDrawingCache();
//            	Bitmap bmap = currentImageView.getDrawingCache();
            	if(result != null)
            	{
	            	 if(setWallpaper)
	            	 {
	                 	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
	            	 	int width = result.getWidth();
	            	    int height = result.getHeight();
	//            	    //get sizes of screen and scale level
	            	    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	            	    int newWidth = display.getWidth() * 2;
	            	    int newHeight = display.getHeight();
	            	    Bitmap resizedBitmap;
	            	    float scaleWidth = ((float) newWidth) / width;
	            	    float scaleHeight = ((float) newHeight) / height;
	            	    //set matrix of the scaling
	            	    Matrix matrix = new Matrix();
	            	    matrix.postScale(scaleWidth, scaleHeight);
	            	    //get new bitmap for wallpaper
	            	    resizedBitmap = Bitmap.createBitmap(result, 0, 0, width, height, matrix, true);
		            	myWallpaperManager.clear();
		                myWallpaperManager.setBitmap(resizedBitmap);
		                toastMessage = "Wallpaper Set";
	            	 }
	            	try 
	            	{
	            		File dest = new File(Environment.getExternalStoragePublicDirectory("AutoMagicWallpapers"), Filename);
	            	     FileOutputStream out = new FileOutputStream(dest);
	            	     if(Filename.matches("png"))
	            	     {
	            	    	 result.compress(Bitmap.CompressFormat.PNG, 100, out);
	            	     }
	            	     else
	            	     {
	            	    	 result.compress(Bitmap.CompressFormat.JPEG, 100, out);
	            	     }
	            	     out.flush();
	            	     out.close();
	            	     sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStoragePublicDirectory("AutoMagicWallpapers"))));
	            	     pd.dismiss();
	            	     Toast.makeText(ImagePager.this, toastMessage, Toast.LENGTH_LONG).show();
	            	} 
	            	catch (Exception e) 
	            	{
	            	     e.printStackTrace();
	            	     toastMessage = "Error Downloading Image";
	            	     Toast.makeText(ImagePager.this, toastMessage, Toast.LENGTH_LONG).show();
	            	     pd.dismiss();
	            	}
            	}
            	else
            	{
    	       	    toastMessage = "Error Downloading Image";
    	       	    Toast.makeText(ImagePager.this, toastMessage, Toast.LENGTH_LONG).show();
    	       	    pd.dismiss();
            	}
            } 
            catch (Exception e) 
            {
	       	    toastMessage = "Error Downloading Image";
	       	    Toast.makeText(ImagePager.this, toastMessage, Toast.LENGTH_LONG).show();
	       	    pd.dismiss();
            }
        }
    }
    
	public void onBackPressed() {
		imageLoader.stop();
		grid.smoothScrollToPosition(pagerposition);
		imageLoader.resume();
		super.onBackPressed();
	}
    
    

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			imageView = (ImageView) imageLayout.findViewById(R.id.image);
			imageLayout.setTag(position);
			imageLayout.setDrawingCacheEnabled(true);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);

			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}
