package com.andrey.guryanov.harmonica.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.andrey.guryanov.harmonica.Player;

import java.io.File;

public class App extends Application {                                                              //Класс Апп. Объект этого класса создаётся только один раз. Его назначение - доступ
                                                                                                    //-- к Плееру из любой точки приложения через вызов статического метода
    private static App[] app;                                                                       //статический массив, где хранится ссылка на этот объект. Массив нужен для того, чтобы
                                                                                                    //-- передавать нестатический объект этого класса в статическом методе
    private static Context[] context;                                                               //статический массив с контекстом приложения. Нужен по тем же причинам, что и выше.
    private Player player;                                                                          //Объект класса Плеер. Создаётся в приложении только один раз (синглтон)


    @Override
    public void onCreate() {
        super.onCreate();

        init();                                                                                     //инициализируем статические массивы, чтобы объекты в них можно было вызывать из любой
    }                                                                                               //-- точки приложения


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private void init() {
        context = new Context[]{getApplicationContext()};                                           //заносим в массив контекст приложения
        app = new App[]{this};                                                                      //заносим в массив ссылку на этот объект (this App)
        player = new Player();                                                                      //создаём синглтон-объект Плеера

        Log.e("App", "App инициализировался!"); //тест
    }

    private Player returnPlayer() {                                                                 //метод возвращает объект Плеера статическому геттеру. Нужен только для этого
        return player;
    }


    /** СТАТИЧЕСКИЕ ГЕТТЕРЫ */


    public static App getApp() {
        return app[0];
    }

    public static Context getAppContext() {
        return context[0];
    }

    public static String getAppDir() {
        return context[0].getApplicationInfo().dataDir + "/files";
    }

    public static String getPlayListsDir() {
        return getAppDir() + "/PlayLists";
    }

    public static String getPlayerStatePath() {                                                     //возвращает путь в папке приложения к файлу, в котором сохраняется состояние плеера
        return getAppDir() + "/playerState.hps";
    }

    public static boolean isFirstLaunchApp() {                                                      //метод проверяет, запускалось ли это приложение когда-нибудь, или это первый запуск
        return !new File(getPlayerStatePath()).exists();
    }

    public static Player getPlayer() {
        return getApp().returnPlayer();
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//            /** $
//    // надо попробовать инициализировать Плеер прямиком в методе онКриейт */
//    private void createPlayer() {
//        player = new Player();
//    }

//        if (!initPlayer) {                                                                          //если Плеер ещё не создан
//            createPlayer();                                                                         //создаём объект Плеера
//            initPlayer = true;                                                                      //говорим маркеру, что Плеер создан
//        }
//private boolean initPlayer;                                                                     //маркер, показывающий, создался ли уже объект класса Плеер. Нужен для того, чтобы
//-- создавать объект Плеера только один раз