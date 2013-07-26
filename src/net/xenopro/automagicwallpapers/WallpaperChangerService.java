package net.xenopro.automagicwallpapers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class WallpaperChangerService extends Service {
	
	public int FileOrder = 0;
	public File[] pictures;
	public String[] imageOrder;
	public File Filepath;
	public String randomizedFiles;
	public boolean isImages = true;
	
	@Override
	public void onCreate() {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
	 // TODO Auto-generated method stub
	 super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
	 // TODO Auto-generated method stub
	 super.onStart(intent, startId);
	 SetWallpaper();
	}

	@Override
	public boolean onUnbind(Intent intent) {
	 // TODO Auto-generated method stub
	 return super.onUnbind(intent);
	}
	
	public String getImagePath() 
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		FileOrder = sharedPrefs.getInt("CurrentImage", 0);
		if(imageOrder.length - 1 < FileOrder)
		{
			FileOrder = 0;
		}
		String path = imageOrder[FileOrder];
		if((path.toLowerCase(Locale.US).contains(".jpg") || path.toLowerCase(Locale.US).contains(".png")) && !path.isEmpty())
		{
			FileOrder = FileOrder + 1;
			Editor editor = sharedPrefs.edit();
			editor.putInt("CurrentImage", FileOrder);
			editor.commit();
		}
		else
		{
			FileOrder = FileOrder + 1;
			Editor editor = sharedPrefs.edit();
			editor.putInt("CurrentImage", FileOrder);
			editor.commit();
			path = getImagePath();
		}
			
		return path;
	}
	
	public void RandomizeFiles()
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		try
		{
		Editor editor = sharedPrefs.edit();
		StringBuilder sb = new StringBuilder();
		String[] sortedPictures;
		if(sharedPrefs.getString("SelectedFolders", "") != null && !sharedPrefs.getString("SelectedFolders", "").isEmpty())
		{
			String[] selectedFoldersArray = sharedPrefs.getString("SelectedFolders", "").split(",");
			//Arrays.sort(selectedFoldersArray);
			for(int i = 0; i < selectedFoldersArray.length; i++)
			{
				String selected = selectedFoldersArray[i];
				if(selected.equals("Camera"))
				{
					Filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
				}
				pictures = Filepath.listFiles(new FilenameFilter() {
					   @Override
					   public boolean accept(File dir, String name) {
					      File f = new File(dir, name);
					      return !f.isDirectory() && f.canRead() && (f.getAbsolutePath().endsWith(".png") || f.getAbsolutePath().endsWith(".jpg"));
					   }
					});
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
			if(Filepath != null)
			{
				pictures = Filepath.listFiles(new FilenameFilter() {
					   @Override
					   public boolean accept(File dir, String name) {
					      File f = new File(dir, name);
					      return !f.isDirectory() && f.canRead() && (f.getAbsolutePath().endsWith(".png") || f.getAbsolutePath().endsWith(".jpg"));
					   }
					});
				if(pictures.length > 0)
				{
					for(int ii = 0; ii < pictures.length; ii++)
					{
						File temp = pictures[ii];
						sb.append(temp.getAbsolutePath()).append(",");
					}
				}
			}
				
		}
		randomizedFiles = sb.toString();
		if(sb.length() > 0)
		{
			sb.setLength(sb.length() - 1);
		}
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
		imageOrder = randomizedFiles.split(",");
		if(imageOrder.length > 0 && randomizedFiles != "")
		{
			randomizedFiles = sb.toString();
			imageOrder = randomizedFiles.split(",");
			editor.putString("ImageOrder", randomizedFiles);
			editor.putInt("CurrentImage", 0);
			editor.commit();
		}
		else
		{
			isImages = false;
		}
		}
		catch(Exception e)
		{
			Editor editor = sharedPrefs.edit();
			editor.putString("ImageOrder", "");
			editor.putInt("CurrentImage", 0);
			editor.putString("SelectedFolders", "");
			editor.commit();
			Toast.makeText(WallpaperChangerService.this, "Error Selecting Rotating Images. Please select folders again.", Toast.LENGTH_LONG).show();
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
	
	public void GetImages()
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String Imageorder = sharedPrefs.getString("ImageOrder", randomizedFiles);
		if(Imageorder != null && !Imageorder.isEmpty())
		{
			imageOrder = sharedPrefs.getString("ImageOrder", randomizedFiles).split(",");
		}
		else
		{
			RandomizeFiles();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void SetWallpaper()
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
	    try 
	    {
	    	GetImages();
	    	if(isImages)
	    	{
		    	WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
		    	myWallpaperManager.setWallpaperOffsetSteps(1, 1);
		    	String path = getImagePath();
				Bitmap bmap = BitmapFactory.decodeFile(path);
				Uri baseURI = Uri.parse("file://" + path);
				if(bmap != null && (path != null && !path.isEmpty()))
				{					
		           	int width = bmap.getWidth();
		     	    int height = bmap.getHeight();
		     	    //get sizes of screen and scale level
		     	    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		     	    int newWidth = display.getWidth() * 2;
		     	    int newHeight = display.getHeight();
            	    //Bitmap resizedBitmap;
            	    float scaleWidth = ((float) newWidth) / width;
            	    float scaleHeight = ((float) newHeight) / height;
            	    //set matrix of the scaling
            	    Matrix matrix = new Matrix();
            	    matrix.postScale(scaleWidth, scaleHeight);
            	    //get new bitmap for wallpaper
            	    Bitmap resizedBitmap = Bitmap.createBitmap(bmap, 0, 0, width, height, matrix, true);
            	    bmap = null;
    				Matrix newmatrix = new Matrix();
    				float rotation = rotationForImage(getApplicationContext(), baseURI);
    				if (rotation != 0f) 
    				{
    				     newmatrix.preRotate(rotation);
    				}

    				Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(),  resizedBitmap.getHeight(), newmatrix, true);
		     	    myWallpaperManager.setBitmap(rotatedBitmap);
					if(sharedPrefs.getBoolean("toastMessages", true))
					{
						Toast.makeText(WallpaperChangerService.this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
					}
					this.stopSelf();
				}
				else
				{
					RandomizeFiles();
					SetWallpaper();
				}
	    	}
	    	else
	    	{
				editor.putBoolean("serviceRunning", false);
				editor.putInt("CurrentImage", 0);
				editor.putString("ImageOrder", "");
				editor.commit();
				Intent endIntent = new Intent(this, WallpaperChangerService.class);
				PendingIntent pendingIntent = PendingIntent.getService(this, 0, endIntent, 0);
		        AlarmManager alarmManagerstop = (AlarmManager)getSystemService(ALARM_SERVICE);
	            alarmManagerstop.cancel(pendingIntent);
				Toast.makeText(WallpaperChangerService.this, "No images to display please select folders with images in them then start magic again.", Toast.LENGTH_LONG).show();
				this.stopSelf();
	    	}

	    }
	    catch (Exception e) 
	    {
	    	Toast.makeText(WallpaperChangerService.this, "Error Setting Wallpaper", Toast.LENGTH_SHORT).show();
	    }
	}
	
	public int FileCount()
	{
		int filecount;
		File file = new File(Environment.getExternalStorageDirectory(), "AutoMagicWallpapers");
		File[] files = file.listFiles();
		filecount = files.length - 1;
		return filecount;
	}
	
	public static float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
        String[] projection = { Images.ImageColumns.ORIENTATION };
        Cursor c = context.getContentResolver().query(
                uri, projection, null, null, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
    } else if (uri.getScheme().equals("file")) {
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            int rotation = (int)exifOrientationToDegrees(
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL));
            return rotation;
        } catch (IOException e) {
            //Log.e(TAG, "Error checking exif", e);
        }
    }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
        return 90;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
        return 180;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
        return 270;
    }
    return 0;
}

	

}
