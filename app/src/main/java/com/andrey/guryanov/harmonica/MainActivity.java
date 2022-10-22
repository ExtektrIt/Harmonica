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

    private ImageButton prev, playPause, next;
    private SeekBar seekBar;
    private Player player;
    private TextView title, artist, elapsed, duration, remainder;
    private TextView trackInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    protected void onStop() {                                                                       //переопределил для сохранения состояния плеера (текущий трек, состояние и т.д.) в файл
        Log.e("main", "main destroyed!");
        player.savePlayerState();                                                                   //сохраняет состояние плеера в файл
        super.onStop();
    }


    /** ИНИЦИАЛИЗАЦИЯ */


    protected void initViews() {                                                                    //поиск и присваивание элементов Вью
        prev = findViewById(R.id.ib_prevTrack_button);
        playPause = findViewById(R.id.ib_playTrack_button);
        next = findViewById(R.id.ib_nextTrack_button);
        seekBar = findViewById(R.id.sb_seekBar);
        title = findViewById(R.id.tv_title);
        artist = findViewById(R.id.tv_artist);
        elapsed = findViewById(R.id.tv_current_play_time);
        duration = findViewById(R.id.tv_duration);
        remainder = findViewById(R.id.tv_remainder);

        trackInfo = findViewById(R.id.tv_test_track_info);                                          //тест пока

        title.setSelected(true);                                                                    //нужно для того, чтобы запускалась прокрутка текста с названием трека

        Log.e("main","title = " + title);
        Log.e("main","initViews");
    }

    protected void initPlayer() {
        player = App.getPlayer();                                                                   //получение ссылки на Плеер

        ArrayList<Object> viewArray = new ArrayList<>();                                            //создаём список элементов Вью для отправки их в Плеер для взаимодействия с ними оттуда
        viewArray.add(title);       //0
        viewArray.add(artist);      //1
        viewArray.add(elapsed);     //2
        viewArray.add(duration);    //3
        viewArray.add(seekBar);     //4
        viewArray.add(playPause);   //5
        viewArray.add(remainder);   //6
        viewArray.add(trackInfo);   //7     test

        player.initPlayer(viewArray);                                                               //отправляем список Вью в Плеер, тем самым инициализируя его

        if (player.getCurrentTrack().getID() != 0) {                                                //если текущий трек в Плеере задан
            player.showTrackInfo();                                                                 //отображает данные текущего трека на экране
            player.changePlayButton();                                                              //метод меняет иконку кнопки Плей в зависимости от состояния Плеера (на паузе или играет)
        }
    }


    /** НАВИГАЦИЯ И УПРАВЛЕНИЕ */


    public void goToFileList(View v) {                                                              //переход в проводник для добавления треков в плейлист
        Intent intent = new Intent(this, FileList.class);
        startActivity(intent);
    }

    public void goToTrackList(View v) {                                                             //переход к списку треков
        Intent intent = new Intent(this, TrackList.class);
        startActivity(intent);
    }

    public void playTrack(View v) {                                                                 //запускаем либо приостанавливаем трек
        player.playFromMain();                                                                      //метод для запуска/паузы песни из Мейна, внутри которого встроены проверки состояния
    }

    public void nextTrack(View v) {
        player.next();
    }

    public void prevTrack(View v) {
        player.prev();
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


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
//        if (player.getCurrentTrack().getID() != 0) {                                                //если текущая песня задана
//            app.showInfo();                                                                         //отображает данные текущей песни на экране
//            player.changePlayButton();                                                              //нужен для того, чтобы при перевороте экрана кнопка плей отображалась корректно
//        }
//
//    }