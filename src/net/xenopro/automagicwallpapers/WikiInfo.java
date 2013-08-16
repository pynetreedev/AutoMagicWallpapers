package net.xenopro.automagicwallpapers;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.xenopro.automagicwallpapers.R;

public class WikiInfo extends Activity {
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wiki_info);
		
		 
		webView = (WebView) findViewById(R.id.wikiInfowebView);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl("http://en.wikipedia.org/wiki/Portrait_of_Giovanna_Tornabuoni");
	}

}
