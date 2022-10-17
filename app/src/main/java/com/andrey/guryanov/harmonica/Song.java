package com.andrey.guryanov.harmonica;

import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class Song implements Serializable {

    private final long id;
    private final String name;
    private final String path;
//    private boolean isUseNow;
    private boolean init = false;
    private String title;
    private String artist;
    private String album;
    private ImageView poster;

    public Song(String name, String path, long id) {
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

    public void stop() {

        //File file = new File("kk");


    }

//    public void use() {
//        isUseNow = true;
//
//    }

    public void pause() {

    }

    public void resume() {

    }

    public void getData() {

    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return path;
    }

    public String getPath() {
        return path;
    }

    public android.net.Uri getUri() {
        return Uri.parse(path);
    }

//    public boolean isUseNow() {
//        return isUseNow;
//    }

    public void playAndStop() {

    }
}
