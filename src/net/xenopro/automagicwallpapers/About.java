package net.xenopro.automagicwallpapers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.xenopro.automagicwallpapers.R;

public class About extends Activity {
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		 
		webView = (WebView) findViewById(R.id.webAbout);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl("file:///android_asset/aboutus.html");
		webView.setWebViewClient(new WebViewClient(){
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        if (url != null && (url.startsWith("http://") || (url.startsWith("https://")))) {
		            view.getContext().startActivity(
		                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		            return true;
		        } else if (url.startsWith("mailto:")) {
		            url = url.replaceFirst("mailto:", "");
		            url = url.trim();
		            Intent i = new Intent(Intent.ACTION_SEND);
		            i.putExtra(Intent.EXTRA_EMAIL, new String[]{url});
		            i.putExtra(Intent.EXTRA_SUBJECT, "AutoMagic Wallpapers Support");
		            i.setType("message/rfc822");
		            startActivity(i);
		            return true;
		        }
		        else {
		            return false;
		        }
		    }
		});
	}
	
	public void CloseAbout(View view) 
	{
		this.finish();
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate( R.menu.wiki_info, menu);
//		return true;
//	}

}
