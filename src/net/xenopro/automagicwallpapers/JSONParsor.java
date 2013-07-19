package net.xenopro.automagicwallpapers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

public class JSONParsor extends AsyncTask<String, String, String>
{
	
	public String[] getImages(String category)
    {
		String[] imagePaths = null;
        String imageUrl = "http://www.xenopro.net/wallpapers/thumbs/" + category + "/";
        //String imageUrl = "https://googledrive.com/host/0B2u0cNotjAT4ZUFMLTJpVTE5NDQ/" + category + "/";
        try 
        {
//        	String url = imageUrl + "json.php";
//        	HttpClient client = new DefaultHttpClient();
//
//        	try {
//        	  client.execute(new HttpGet(url));
//        	} catch(IOException e) {
//        	  //do something here
//        	}
            URL xenoproWallpers = new URL(imageUrl + "images.json");
            URLConnection tc = xenoproWallpers.openConnection();
            tc.setUseCaches(false);
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) 
            {
                JSONArray ja = new JSONArray(line);
                imagePaths = new String[ja.length()];
                for (int i = 0; i < ja.length(); i++) 
                {
                    String imagePath = ja.getString(i);
                    imagePaths[i] = imageUrl + imagePath;
                }
            }
            in.close();
        } 
//        catch (MalformedURLException e) 
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } 
//        catch (IOException e) 
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } 
//        catch (JSONException e) 
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        catch (Exception e)
        {
        	imagePaths = null;
        }
        return imagePaths;
    }
	
	public String[] getImageThumbs(String category)
    {
		String[] imagePaths = null;
        String imageUrl = "http://www.xenopro.net/wallpapers/thumbs/" + category + "/";
        //String imageUrl = "https://googledrive.com/host/0B2u0cNotjAT4ZUFMLTJpVTE5NDQ/" + category + "/";
        try 
        {
            URL xenoproWallpers = new URL(imageUrl + "/images.json");
            URLConnection tc = xenoproWallpers.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) 
            {
                JSONArray ja = new JSONArray(line);
                imagePaths = new String[ja.length()];
                for (int i = 0; i < ja.length(); i++) 
                {
                    String imagePath = ja.getString(i);
                    imagePaths[i] = imageUrl + imagePath;
                }
            }
        } 
        catch (MalformedURLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (JSONException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imagePaths;
    }
	
	public String[] getImageInfoPaths(String category)
    {
		String[] imageNames = null;
        String imageUrl = "http://www.xenopro.net/wallpapers/thumbs" + category + "/";
        try 
        {
            URL xenoproWallpers = new URL(imageUrl + "/images.json");
            URLConnection tc = xenoproWallpers.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) 
            {
                JSONArray ja = new JSONArray(line);
                imageNames = new String[ja.length()];
                for (int i = 0; i < ja.length(); i++) 
                {
                    String imageName = ja.getString(i);
                    imageNames[i] = imageName;
                }
            }
        } 
        catch (MalformedURLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (JSONException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageNames;
    }

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
