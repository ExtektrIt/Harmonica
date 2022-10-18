package com.andrey.guryanov.harmonica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.App;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /** ПРОБЛЕМЫ
     -
     **/


    /** ТуДу
     -
     **/

    //private App app;
    private ImageButton prev, playPause, next;
    private SeekBar seekBar;
    private Player player;
    //private PlayList playList;
    private TextView title, artist, elapsed, duration, remainder;
    private TextView trackInfo;

    private ArrayList<Object> viewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initApp();
        initViews();
        initPlayer();


        Log.e("main","main reCreated!");

        playPause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!player.isStopped()) {
                    player.stop();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
//        Toast t = Toast.makeText(this, "Плеер закрыт!", Toast.LENGTH_LONG);
//        t.show();
        Log.e("main", "main destroyed!");
//        System.exit(0);
        //app.savePlayer();
        player.savePlayerState();
        super.onStop();
    }
//
//    protected void initApp() {
//        app = App.getApp();
//    }

    protected void initViews() {
        prev = findViewById(R.id.ib_prevTrack_button);
        playPause = findViewById(R.id.ib_playTrack_button);
        next = findViewById(R.id.ib_nextTrack_button);
        seekBar = findViewById(R.id.sb_seekBar);
        title = findViewById(R.id.tv_title);
        artist = findViewById(R.id.tv_artist);
        elapsed = findViewById(R.id.tv_current_play_time);
        duration = findViewById(R.id.tv_duration);
        remainder = findViewById(R.id.tv_remainder);

        trackInfo = findViewById(R.id.tv_test_track_info);

        title.setSelected(true);

        Log.e("main","title = " + title);
        Log.e("main","initViews");

    }

    protected void initPlayer() {
        player = App.getPlayer();
        //buildViewArray();

        viewArray = new ArrayList<>();
        viewArray.add(title);       //0
        viewArray.add(artist);      //1
        viewArray.add(elapsed);     //2
        viewArray.add(duration);    //3
        viewArray.add(seekBar);     //4
        viewArray.add(playPause);   //5
        viewArray.add(remainder);   //6
        viewArray.add(trackInfo);   //7     test

        player.initPlayer(viewArray);

        if (player.getCurrentTrack().getID() != 0) {                                                //если текущая песня задана
            player.showTrackInfo();                                                                 //отображает данные текущей песни на экране
            player.changePlayButton();                                                              //нужен для того, чтобы при перевороте экрана кнопка плей отображалась корректно
        }
    }

//    protected void buildViewArray() {
//
//        viewArray = new ArrayList<>();
//        viewArray.add(title);       //0
//        viewArray.add(artist);      //1
//        viewArray.add(elapsed);     //2
//        viewArray.add(duration);    //3
//        viewArray.add(seekBar);     //4
//        viewArray.add(playPause);   //5
//        viewArray.add(remainder);   //6
//
//        viewArray.add(trackInfo);   //7
////
////            App.initMain = true;
////            player.initViewsToPlayer(viewArray);
////            Log.e("main","buildMain");
////        }
//        app.createViewArray();                                                                      //создает массив Вьюшек, чтобы их потом отправить в плеер для взаимодействия
//        app.getViews().add(title);          //0
//        app.getViews().add(artist);         //1
//        app.getViews().add(elapsed);        //2
//        app.getViews().add(duration);       //3
//        app.getViews().add(seekBar);        //4
//        app.getViews().add(playPause);      //5
//        app.getViews().add(remainder);      //6
//
//        app.getViews().add(trackInfo);      //7
//        app.giveViewsToPlayer();                                                                    //отправляет заполненный массив с Вьюшками в плеер
//
//        if (player.getCurrentTrack().getID() != 0) {                                                 //если текущая песня задана
//            app.showInfo();                                                                         //отображает данные текущей песни на экране
//            player.changePlayButton();                                                              //нужен для того, чтобы при перевороте экрана кнопка плей отображалась корректно
//        }
//
//    }


    public void goToFileList(View v) {
        Intent intent = new Intent(this, FileList.class);
        startActivity(intent);
    }

    public void goToTrackList(View v) {
        Intent intent = new Intent(this, TrackList.class);
        startActivity(intent);
    }


    public void playTrack(View v) {
        player.playFromMain();
    }

    public void nextTrack(View v) {
        player.next();
    }

    public void prevTrack(View v) {
        player.prev();
    }

}