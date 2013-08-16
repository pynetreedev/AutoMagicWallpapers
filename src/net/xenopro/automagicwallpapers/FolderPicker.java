package net.xenopro.automagicwallpapers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.xenopro.automagicwallpaperspro.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FolderPicker extends Activity {

	ListView listView;
	ArrayAdapter<String> adapter;
	String[] imageFolders;
	String[] selectedFoldersArray;
	//ArrayList<Folders> folders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder_picker);
		listView = (ListView) findViewById(R.id.FolderlistView);
		File dirs = Environment.getExternalStorageDirectory();
//		folders = new ArrayList<Folders>();
//		getGridItemsList(dirs);
		imageFolders = dirs.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean imagedir = false;
				File f = new File(dir, name);   
				try{
					if (f.isDirectory()) {
						for (File fi : f.listFiles()) 
						{
							if(fi.getAbsolutePath().endsWith(".png") || fi.getAbsolutePath().endsWith(".jpg"))
							{
								imagedir = true;
							}
						}
						if(imagedir)
						{
							return f.isDirectory() && !f.isHidden() && imagedir;
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}
				}
				catch  (Exception e)
				{
					return false;
				}
			}
		});
		Arrays.sort(imageFolders);
		ArrayList<String> entries = new ArrayList<String>(Arrays.asList(imageFolders));
		File DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
		if(hasPhoto(DCIM))
		{
			entries.add("Camera");
		}
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, entries);
		//adapter.add("Camera");
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(sharedPrefs.getString("SelectedFolders", "") != "" && sharedPrefs.getString("SelectedFolders", "") != null)
		{
			selectedFoldersArray = sharedPrefs.getString("SelectedFolders", "").split(",");
			for(int i = 0; i < selectedFoldersArray.length; i++)
			{
				String selected = selectedFoldersArray[i];
				for(int ii = 0; ii < entries.size(); ii++)
				{
					String folder = entries.get(ii);
					if(selected.equals(folder))
					{
						listView.setItemChecked(ii, true);
					}
				}
			}
		}
	

	}
	
	public void SaveFolderClick(View view) 
	{
		SparseBooleanArray checked = listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }
 
        String[] outputStrArr = new String[selectedItems.size()];
 
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }
		StringBuilder sb = new StringBuilder();
		for(int i = 0; outputStrArr.length > i ; i++) 
		{
	        sb.append(outputStrArr[i]).append(",");
		}
		sb.setLength(sb.length() - 1);
		String selectedFolders = sb.toString();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		editor.putString("SelectedFolders", selectedFolders);
		editor.commit();
		RandomizeFiles();
		this.finish();
	}
	
	public void CancelClick(View view) 
	{
		this.finish();
	}
	
	public void getGridItemsList(File file) 
	{
	    File list[] = file.listFiles();
	    if (list.length == 0) {
	        return;
	    }
	    for (int i = 0; i < list.length; i++) {
	        File temp_file = new File(file.getAbsolutePath(), list[i].getName());
	        if (hasPhoto(temp_file)) {
	        	if(temp_file.isDirectory() && !temp_file.isHidden() && !temp_file.getAbsolutePath().contains("cache"))
	        	{
	        		//folders.add(new Folders(temp_file.getName(), temp_file.getAbsolutePath(), false));
	        		//getGridItemsList(temp_file);
	        	}
	            //Log.i(TAG,"added"+list[i].getName());
	        }
	    }

	}

	private boolean hasPhoto(File temp_file) {
	    //Log.i(TAG,temp_file.getName());

	    if (temp_file.listFiles() == null) {
	        if (temp_file.getName().toUpperCase().endsWith(("JPG")) || temp_file.getName().toUpperCase().endsWith(("PNG"))) {
	            //Log.i(TAG,temp_file.getName()+ "is a photo");
	            return true;
	        } else
	            return false;

	    }
	    else{
	        File list[] = temp_file.listFiles();

	        for (int i = 0; i < list.length; i++) {
	            if(hasPhoto(list[i]))
	                return true;
	        }

	    }

	    return false;
	}
	
	public void RandomizeFiles()
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		try
		{
		StringBuilder sb = new StringBuilder();
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
			if(sb.length() > 0)
			{
				sb.setLength(sb.length() - 1);
			}
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
			Toast.makeText(FolderPicker.this, "Error Selecting Rotating Images. Please select folders again.", Toast.LENGTH_LONG).show();
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
