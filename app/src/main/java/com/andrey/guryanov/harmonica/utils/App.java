package com.andrey.guryanov.harmonica.utils;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.andrey.guryanov.harmonica.PlayList;
import com.andrey.guryanov.harmonica.Player;
import com.andrey.guryanov.harmonica.R;
import com.andrey.guryanov.harmonica.Track;
import com.andrey.guryanov.harmonica.Views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class App extends Application {                                                              //Класс Апп. Объект этого класса создаётся только один раз. Его назначение - доступ
                                                                                                    //-- к Плееру из любой точки приложения через вызов статического метода
    private static App[] app;                                                                       //статический массив, где хранится ссылка на этот объект. Массив нужен для того, чтобы
                                                                                                    //-- передавать нестатический объект этого класса в статическом методе
    private static Context[] context;                                                               //статический массив с контекстом приложения. Нужен по тем же причинам, что и выше.
    private Player player;                                                                          //Объект класса Плеер. Создаётся в приложении только один раз (синглтон)
    private static String currentLocation;
    private Views views;
    private boolean initViews;

    private File appDir, playListDir;

//    private static int countHolders = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        init();                                                                                     //инициализируем статические массивы, чтобы объекты в них можно было вызывать из любой
    }                                                                                               //-- точки приложения


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private void init() {
        context = new Context[]{getApplicationContext()};                                           //заносим в массив контекст приложения
        app = new App[]{this};                                                                      //заносим в массив ссылку на этот объект (this App)

        appDir = new File(getAppContext().getFilesDir().getPath());
        playListDir = new File(appDir,"playList");
        playListDir.mkdir();
//        views = new Views();
        player = new Player();                                                                      //создаём синглтон-объект Плеера

        Log.e("App", "App инициализировался!"); //тест
    }

    private Player returnPlayer() {                                                                 //метод возвращает объект Плеера статическому геттеру. Нужен только для этого
        return player;
    }

    private String returnAppDir() {
        return appDir.getPath();
    }

    private String returnPlayListDir() {
        return playListDir.getPath();
    }


    /** СТАТИЧЕСКИЕ ГЕТТЕРЫ */


    public static App getApp() {
        return app[0];
    }

    public static Context getAppContext() {
        return context[0];
    }

    public static String getAppDir() {
        return getApp().returnAppDir();
    }
    //context[0].getApplicationInfo().dataDir + "/files";

    public static String getPlayListsDir() {
        return getApp().returnPlayListDir();
    }
    //return getAppDir() + "/PlayLists";

    public static String getPlayerStatePath() {                                                     //возвращает путь в папке приложения к файлу, в котором сохраняется состояние плеера
        return getAppDir() + "/playerState.hps";
    }

    public static boolean isFirstLaunch() {                                                      //метод проверяет, запускалось ли это приложение когда-нибудь, или это первый запуск
        return !new File(getPlayerStatePath()).exists();
    }

    public static Player getPlayer() {
        return getApp().returnPlayer();
    }

    public static String getCurrentLocation() {
        return currentLocation;
    }



    public static void setCurrentLocation(String location) {
        currentLocation = location;
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//    public static void setCountHolders(int num) {//
//        countHolders = num;
//    }

//    public static int getCountHolders() {//
//        return countHolders;
//    }

//    public int computeHolderHeight() {
////        ConstraintLayout trackViewHolder = new ConstraintLayout(getAppContext());
////        trackViewHolder.findViewById(R.id.l_track_list);
////        trackViewHolder.reso;
////        trackViewHolder.forceLayout();
////        trackViewHolder.getHeight();
//        View track = new View(getAppContext());
//        track.findViewById(R.id.l_track_list);
//        return track.getMeasuredHeight();
//    }

//    public static int getTrackDuration(Track track) {
//        MediaPlayer mediaPlayer = MediaPlayer.create(context[0], track.getUri());
//        int result = mediaPlayer.getDuration();
//        return result;
//    }