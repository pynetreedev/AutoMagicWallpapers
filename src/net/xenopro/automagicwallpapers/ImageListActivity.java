
package net.xenopro.automagicwallpapers;

import net.xenopro.automagicwallpapers.Constants.Extra;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tapfortap.Interstitial;
import com.tapfortap.TapForTap;

public class ImageListActivity extends BaseActivity {

	
    private ListView listView1;
    Category[] cat; 
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.Theme_Sherlock);
        TapForTap.initialize(this, "YOUR_TAPFORTAP_API_KEY");
        setContentView(R.layout.category);
        Bundle bundle = getIntent().getExtras();
        // Below the inputs are image name, in app title name, website title name
        final Category photo_category_data[] = new Category[]
        {
            new Category(R.drawable.aurora_pixabay_icon, "Scenery","scenery"),
            new Category(R.drawable.cactus_pixabay_icon, "Flora","flora"),
            new Category(R.drawable.pyramid_icon, "Buildings","buildings"),
            new Category(R.drawable.turtles_pixabay_icon, "Fauna","fauna"),
            new Category(R.drawable.astronaut_pixabay_icon, "Miscelaneous","otherstuff"),
           
        };
        final Category art_category_data[] = new Category[]
        {
            new Category(R.drawable.st_michael_1504_icon, "1425-1657","1425-1657"),
            new Category(R.drawable.bombardment_of_algiers_1820_icon, "1658-1849","1658-1849"),
            new Category(R.drawable.landers_peak_1863_icon, "1850-1888","1850-1888"),
            new Category(R.drawable.the_sleeping_gypsy_1897_icon, "1889-1930","1889-1930"),
           
        };
        
        final Category category_data[] = new Category[]
        {
            new Category(R.drawable.st_michael_1504_icon, "Art","art"),
            new Category(R.drawable.deep_blue_pixabay_icon, "Photography","photo"),
           
        };
        
        String CategoryName = bundle.getString(Extra.CATEGORYNAME);
        //View header = (View)getLayoutInflater().inflate(R.layout.category_header, null);
        //TextView ViewText= (TextView) header.findViewById(R.id.txtHeader);
        
        if (CategoryName.matches("art"))
        {
        	cat = art_category_data;
        	setTitle("Art Wallpapers");
            
        }
        else if (CategoryName.matches("photo"))
        {
        	cat = photo_category_data;
        	setTitle("Photo Wallpapers");
            
        }
        else
        {
        	cat = category_data;
        	setTitle("Category");
        }
        
        CategoryAdapter adapter = new CategoryAdapter(this, R.layout.item_list_image, cat);
    
        

        listView1 = (ListView)findViewById(R.id.listView1);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
        	
        	 
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		String name = cat[position].fname;
        		if(name.matches("art") || name.matches("photo"))
        		{
        			startCategoryActivity(name);
        		}
        		else
        		{
	        		startAdActivity(name);
        		}
        		}
        });
        
    }
    
	private void startAdActivity(String name) 
	{
		this.finish();
        Interstitial.prepare(this);
        // then later...
        Interstitial.show(this);
		networkConnection = haveNetworkConnection();
//		if(networkConnection)
//		{
//			Intent intent = new Intent(this, AdPopUp.class);
//			startActivity(intent);
//		}
		categoryMain = name; 
    	DownloadTask downloadTask = new DownloadTask();
        /** Starting the task created above */
        downloadTask.execute();
	}
	
	private void startCategoryActivity(String name)
	{
		this.finish();
		Intent intent = new Intent(this, ImageListActivity.class);
		intent.putExtra(Extra.CATEGORYNAME, name);
		startActivity(intent);
	}
	
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog pd;

        protected void onPreExecute() {

                 pd = new ProgressDialog(homecontext);
                 pd.setMessage("Loading Images...");
                 pd.setCancelable(false);
                 pd.setIndeterminate(true);
                 pd.show();

        }
        protected String doInBackground(String... params) {
			String result = "Test";
        	try{
        		if(networkConnection)
        		{
	            	JSONParsor jparsor = new JSONParsor();
					imageUrls = jparsor.getImages(categoryMain);
        		}
            }catch(Exception e){
                
            }
			return result;
        }
        
        
 
        protected void onPostExecute(String result) {
        	if(networkConnection && imageUrls != null)
        	{
	    		GridImageAdapter.notifyDataSetChanged();
	     		grid.smoothScrollToPosition(0);
	    		imageLoader.resume();
        	}
        	else
        	{
        		grid.setVisibility(View.GONE);
        		lblConnection.setVisibility(View.VISIBLE);
        	}
			pd.dismiss();
        }
    }
   
}     
    