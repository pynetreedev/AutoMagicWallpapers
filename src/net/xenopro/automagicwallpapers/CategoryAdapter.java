package net.xenopro.automagicwallpapers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.xenopro.automagicwallpaperspro.R;

public class CategoryAdapter extends ArrayAdapter<Category>{

    Context context; 
    int layoutResourceId;    
    Category data[] = null;
    
    public CategoryAdapter(Context context, int layoutResourceId, Category[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new CategoryHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.image);
            holder.txtTitle = (TextView)row.findViewById(R.id.text);
            
            row.setTag(holder);
        }
        else
        {
            holder = (CategoryHolder)row.getTag();
        }
        
        Category Category = data[position];
        holder.txtTitle.setText(Category.title);
        holder.imgIcon.setImageResource(Category.icon);
        
        return row;
    }
    
    static class CategoryHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}