package net.xenopro.automagicwallpapers;

public class Category {
    public int icon;
    public String title;
    public String fname;
    public Category(){
        super();
    }
    // fname stands for folder name
    public Category(int icon, String title, String fname) {
        super();
        this.icon = icon;
        this.title = title;
        this.fname = fname;
    }
}