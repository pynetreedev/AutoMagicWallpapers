package net.xenopro.automagicwallpapers;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import net.xenopro.automagicwallpapers.R;


public class AdPopUp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_pop_up);
		//Find the ad view for reference
		MMAdView adViewFromXml = (MMAdView) findViewById(R.id.adView);

		//MMRequest object
		MMRequest request = new MMRequest();

		adViewFromXml.setMMRequest(request);

		adViewFromXml.getAd();
	}
	
	public void CloseAdClick(View view) {
		this.finish();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    // If we've received a touch notification that the user has touched
	    // outside the app, finish the activity.
	    if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
	      return false;
	    }
	    return false;
	}
	
	@Override
	public void onBackPressed() {
		
	}

}
