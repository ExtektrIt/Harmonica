package com.andrey.guryanov.harmonica;

import android.media.MediaDataSource;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.RemoteController;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.media.MediaMetadataCompat;
import android.text.method.MetaKeyKeyListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.andrey.guryanov.harmonica.utils.App;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class Track implements Serializable {

    private boolean init = false;
    private final long id;
    private final String name;
    private final String path;
    private final String extension;
    private final int size;
//    private final int duration;

    private String title;
    private String artist;
    private String album;
    private ImageView poster;


    public Track(String name, String path, String ext, long id) {
        this.name = name;
        this.path = path;
        this.extension = ext;
        this.id = id;

        File file = new File(path);
        size = (int) file.length() / 1000;
        //duration = MediaPlayer.create(App.getAppContext(), getUri()).getDuration();
//        duration = App.getTrackDuration(this);
    }

//    public Track() {
//        this.name = ;
//        this.path = ;
//        this.extension = ext;
//        this.id = id;
//    }


    /** ГЕТТЕРЫ */


    public long getID() {
        return id;
    }

    public void getData() {

    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return path;
    }

    public String getExtension() {
        return extension;
    }

//    public String getDuration() {
//        return App.getPlayer().timeFormatter(duration);
//    }

    public int getSizeKB() {
        return size;
    }

    public int etSizeMB() {
        return size / 1000;
    }

    public android.net.Uri getUri() {
        return Uri.parse(path);
    }


    /**  */


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    public int geti() {
        return MediaPlayer.create(App.getAppContext(), getUri()).getDuration();
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//    public boolean isUseNow() {
//        return isUseNow;
//    }

//    public String getPath() {
//        return path;
//    }

//    public String getTitle() {
//        return title;
//    }
//
//    public String getArtist() {
//        return artist;
//    }