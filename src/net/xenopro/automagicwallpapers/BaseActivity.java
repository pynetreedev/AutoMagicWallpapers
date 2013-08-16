package net.xenopro.automagicwallpapers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import net.xenopro.automagicwallpapers.R;

public class BaseActivity extends SherlockActivity {

	public ImageLoader imageLoader = ImageLoader.getInstance();
	protected AbsListView listView;
	public static String[] imageUrls;
	public static String[] imagethumbUrls;
	public static String categoryMain = "photography";
	public static DisplayImageOptions options;
	public static ImageAdapter GridImageAdapter;
	public static GridView grid;
	public static PendingIntent pendingIntent;
	public static Context homecontext;
	public static boolean networkConnection;
	public static TextView lblConnection;
	//Constants for tablet sized ads (728x90)
	public static final int IAB_LEADERBOARD_WIDTH = 728;
	public static final int IAB_LEADERBOARD_HEIGHT = 90;

	public static final int MED_BANNER_WIDTH = 480;
	public static final int MED_BANNER_HEIGHT = 60;

	//Constants for phone sized ads (320x50)
	public static final int BANNER_AD_WIDTH = 320;
	public static final int BANNER_AD_HEIGHT = 50;
	
	
	public void startImageGridActivity(String category) 
	{
		JSONParsor jparsor = new JSONParsor();
		imageUrls = jparsor.getImages(category);
	}
	
	public class ImageAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			inflater = getLayoutInflater();
			View imageLayout = inflater.inflate(R.layout.item_grid_image, parent, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			ImageView imageViewTablet = (ImageView) imageLayout.findViewById(R.id.imageTablet);
			boolean isTablet = isTablet(homecontext);
			if(isTablet)
			{
				imageView.setVisibility(View.GONE);
				imageView = imageViewTablet;
			}
			else
			{
				imageViewTablet.setVisibility(View.GONE);
			}
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			imageLoader.displayImage(imageUrls[position], imageView, options, new SimpleImageLoadingListener() {
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
//			imageLoader.loadImage(imageUrls[position + 1], null);
//			imageLoader.loadImage(imageUrls[position + 2], null);
			return imageLayout;
		}
	
	}
	public boolean haveNetworkConnection() {

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo i = cm.getActiveNetworkInfo();
	    if (i == null)
	      return false;
	    if (!i.isConnected())
	      return false;
	    if (!i.isAvailable())
	      return false;
	    return true;
	}
	
    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        return (xlarge);
    }
    
    public boolean canFit(int adWidth) 
    {
        int adWidthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adWidth, getResources().getDisplayMetrics());
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.widthPixels >= adWidthPx;
    }
    
    public void getAd()
    {
		MMAdView adViewFromXml = (MMAdView) findViewById(R.id.adView);

		//MMRequest object
		MMRequest request = new MMRequest();

		adViewFromXml.setMMRequest(request);
		int placementWidth = BANNER_AD_WIDTH;
		int placementHeight = BANNER_AD_HEIGHT;

		//Finds an ad that best fits a users device.
		if(canFit(IAB_LEADERBOARD_WIDTH)) {
		    placementWidth = IAB_LEADERBOARD_WIDTH;
		    placementHeight = IAB_LEADERBOARD_HEIGHT;
		} else if(canFit(MED_BANNER_WIDTH)) {
		    placementWidth = MED_BANNER_WIDTH;
		    placementHeight = MED_BANNER_HEIGHT;
		}
		adViewFromXml.setWidth(placementWidth);
		adViewFromXml.setHeight(placementHeight);
		int layoutWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, placementWidth, getResources().getDisplayMetrics());
		int layoutHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, placementHeight, getResources().getDisplayMetrics());

		//Create the layout parameters using the calculated adView width and height.
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);

		//This positions the banner.
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		adViewFromXml.setLayoutParams(layoutParams);

		adViewFromXml.getAd();
    }
	
}
