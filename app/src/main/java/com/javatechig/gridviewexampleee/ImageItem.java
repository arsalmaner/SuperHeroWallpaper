package com.javatechig.gridviewexampleee;

import android.graphics.Bitmap;

public class ImageItem {

    private Bitmap image;
    private String id;
    private String name;
    private String url;

    public ImageItem() {
    }

    public ImageItem(Bitmap image, String id, String name, String url) {
        super();
        this.image = image;
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
