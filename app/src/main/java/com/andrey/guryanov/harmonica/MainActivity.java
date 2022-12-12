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
    private TextView title, artist, elapsed, duration, remainder, trackNumber, playMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.setCurrentLocation(this.getLocalClassName());
        initViews();
        initPlayer();

        Log.e("main","main reCreated!");

        playPauseLongListener();
        nextLongListener();
        prevLongListener();
    }

    @Override
    protected void onStop() {                                                                       //переопределил для сохранения состояния плеера (текущий трек, состояние и т.д.) в файл
        Log.e("main", "main stopped!");
        if (player.getCurrentPlayList().getCountTracks() > 0) player.savePlayerState();                            //сохраняет состояние плеера в файл
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
        trackNumber = findViewById(R.id.tv_track_number);
        playMode = findViewById(R.id.tv_play_mode);

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
        viewArray.add(trackNumber); //7
        viewArray.add(playMode);    //8

        player.initPlayer(viewArray);                                                               //отправляем список Вью в Плеер, тем самым инициализируя его

        if (player.getCurrentTrackNumber() >= 0) {                                                //если текущий трек в Плеере задан
            player.showTrackInfo();                                                                 //отображает данные текущего трека на экране
            player.changePlayButton();                                                              //метод меняет иконку кнопки Плей в зависимости от состояния Плеера (на паузе или играет)
        }
    }


    /** СЛУШАТЕЛИ */


    private void playPauseLongListener() {
        playPause.setOnLongClickListener(v -> {
            if (!player.isStopped()) {
                player.stop();
            }
            return true;
        });
    }

    private void nextLongListener() {
        next.setOnLongClickListener(v -> {
            player.rewindTrackTo10Sec(1);
            return true;
        });
    }

    private void prevLongListener() {
        prev.setOnLongClickListener(v -> {
            player.rewindTrackTo10Sec(-1);
            return true;
        });
    }


    /** НАВИГАЦИЯ И УПРАВЛЕНИЕ */



    public void goToTrackList(View v) {                                                             //переход к списку треков
        Intent intent = new Intent(this, TrackList.class);
        startActivity(intent);
    }

    public void changePlayMode(View v) {                                                            //меняет режим воспроизведения
        player.switchPlayMode();
    }

    public void playTrack(View v) {                                                                 //запускаем либо приостанавливаем трек
        if (player.getCurrentPlayList().getCountTracks() < 1) {
            DialogFragmentAddTracks dialog = new DialogFragmentAddTracks();
            dialog.show(getSupportFragmentManager(), "007");
            return;
        }
        player.playFromMain();                                                                      //метод для запуска/паузы песни из Мейна, внутри которого встроены проверки состояния
    }

    public void nextTrack(View v) {
        player.next();
    }

    public void prevTrack(View v) {
        player.prev();
    }

    //тест
    public void openTestFunc(View v) {
        Intent intent = new Intent(this, testFunction.class);
        startActivity(intent);
    }


}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


