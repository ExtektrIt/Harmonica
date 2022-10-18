package com.andrey.guryanov.harmonica;

import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class Track implements Serializable {

    private final long id;
    private final String name;
    private final String path;
    private boolean init = false;
    private String title;
    private String artist;
    private String album;
    private ImageView poster;

    public Track(String name, String path, long id) {
        this.name = name;
        this.path = path;
        this.id = id;
    }

    public long getID() {
        return id;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public String getArtist() {
//        return artist;
//    }

    public void getData() {

    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return path;
    }

//    public String getPath() {
//        return path;
//    }

    public android.net.Uri getUri() {
        return Uri.parse(path);
    }

//    public boolean isUseNow() {
//        return isUseNow;
//    }

}
