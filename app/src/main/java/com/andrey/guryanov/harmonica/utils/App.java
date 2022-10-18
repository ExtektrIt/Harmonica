package com.andrey.guryanov.harmonica.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.andrey.guryanov.harmonica.Player;

import java.io.File;

public class App extends Application {

    private static App[] app;
    private static Context[] context;
    private boolean initPlayer;
    private Player player;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }


    /**
     * СЛУЖЕБНЫЕ МЕТОДЫ
     **/


    private void init() {
        context = new Context[]{getApplicationContext()};
        app = new App[]{this};

        Log.e("App", "App инициализировался!");
    }

    private Player returnPlayer() {
        if (!initPlayer) {
            createPlayer();
            initPlayer = true;
        }
        //Log.e("App","Player инициализировался!");
        return player;
    }

    private void createPlayer() {
        player = new Player();
    }


    /**
     * СТАТИЧЕСКИЕ ГЕТТЕРЫ
     **/


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

    public static String getPlayerDataPath() {
        return getAppDir() + "/playerState.hps";
    }

    public static boolean isFirstLaunchApp() {
        return !new File(getPlayerDataPath()).exists();
    }

    public static Player getPlayer() {
        return getApp().returnPlayer();
    }

}


    /**
     * СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ)
     **/


//    public static void initViewsToPlayer(ArrayList<Object> viewsList) {
//        getPlayer().initPlayer(viewsList);
//    }

//    public boolean playerIsInit() {
//        return initPlayer;
//    }

//    public static Player getPlayerOLD() {
//        //Log.e("App","Player инициализировался!");
//        if (initPlayer) return Loader.player;
//        else {
//            if (isFirstLaunch()) Loader.initPlayer();
//            else Loader.loadPlayer();
//            return Loader.player;
//        }
//
//    }

//    public static Player getPlayer() {
//        if ( ! initPlayer) {
//            if (isFirstLaunch()) createPlayer();
//            else loadPlayer();
//            initPlayer = true;
//        }
//        //Log.e("App","Player инициализировался!");
//        return player;
//
//    }

//    public void createViewArray() {
//        viewArray = new ArrayList<>();
//    }
//
//    public ArrayList<Object> getViews() {
//        return viewArray;
//    }
//
//    public void giveViewsToPlayer() {
//        player.initViewsToPlayer(viewArray);
//    }
//
//    public void showInfo() {
//        player.showSongInfo();
//    }

//    public void loadPlayerState() {
//        //player =
//        // Player tempPlayer;
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//                    getPlayerDataPath()));
//
////            int playListsCount = ois.readInt();
////            for (int i = 0; i < playListsCount; i++) {
////                playLists.add((PlayList) ois.readObject());
////            }
//            //player = (Player) ois.readObject();
//
//            player.getCurrentPlayList().setCurrentSong((Track) ois.readObject());
//            player.getCurrentPlayList().setSongsPlayedCount(ois.readInt());
//            player.setIsPlaying(ois.readBoolean());
//            player.setIsStopped(ois.readBoolean());
//            player.seekTo(ois.readInt());
//
//            ois.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            Log.e("main","error: " + e.getMessage());
//        }
//
//    }

//    public void savePlayerState() {
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
//                    getPlayerDataPath()));
//
////            int playListsCount = ois.readInt();
////            for (int i = 0; i < playListsCount; i++) {
////                playLists.add((PlayList) ois.readObject());
////            }
//            //oos.writeObject(player);
//            oos.writeObject(player.getCurrentSong());
//            oos.writeInt(player.getCurrentPlayList().getSongsPlayedCount());
//            oos.writeBoolean(player.isPlaying());
//            oos.writeBoolean(player.isStopped());
//            oos.writeInt(player.getElapsedTime());
//
//
//            oos.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static Activity[] changeCurrentActivity() {
//        return currentActivity;
//    }


//
//
//
//    private static class Loader {
//        @SuppressLint("StaticFieldLeak")
//        private static final Player player = new Player();
//        //private static final ArrayList<Object> viewArray;
//        //private static final ArrayList<Object> viewArray = new ArrayList<>();
//
//        static void initPlayer() {
//            player = new Player();
//        }
//
//        static void loadPlayer() {
//            //player =
//            try {
//                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//                        playListDir.getPath() + "/playlists.hpl"));
//
//                int playListsCount = ois.readInt();
//                for (int i = 0; i < playListsCount; i++) {
//                    playLists.add((PlayList) ois.readObject());
//                }
//
//                ois.close();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


