package com.andrey.guryanov.harmonica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andrey.guryanov.harmonica.utils.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Player implements SeekBar.OnSeekBarChangeListener {

//    private List<Song> songList;
    //int count = 0;

    //private final Context context;
    //private App app = App.getAppArgs().get(0);//костыль

    private File playListDir;
    private MediaPlayer mediaPlayer;
    private PlayList currentPlayList;
    private ArrayList<PlayList> playLists;
    private boolean isStopped = true;
    private boolean isPlaying = false;
    //private static MediaParser songScanner;
    //private ArrayList<View> viewArgs;
    private ImageButton playPause;
    private SeekBar seekBar;
    private int loadCurrentPos;
    private TextView title, artist, elapsed, duration, remainder;
    private TextView trackInfo;
    private MyTimer timer;

    public Player() {
        initPlayLists();

        mediaPlayer = new MediaPlayer();
        timer = new MyTimer(0);

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (currentPlayList.itsPossible(1)) next();
//                else stop();
//            }
//        });

    }


/** ИНИЦИАЛИЗАЦИЯ ПЛЕЕРА **/


    public void initPlayer(ArrayList<Object> views) {
    title = (TextView) views.get(0);
    artist = (TextView) views.get(1);
    elapsed = (TextView) views.get(2);
    duration = (TextView) views.get(3);
    seekBar = (SeekBar) views.get(4);
    playPause = (ImageButton) views.get(5);
    remainder = (TextView) views.get(6);

    trackInfo = (TextView) views.get(7);

    loader();
    }

    public void loader() {
        seekBar.setOnSeekBarChangeListener(this);
        if ( ! App.isFirstLaunchApp()) {
            loadPlayerState();
            if ( ! isStopped) {
                preparePlayer();
                if (isPlaying) {
                    mediaPlayer.seekTo(loadCurrentPos);
                    resume();
                }
                else {
                    mediaPlayer.seekTo(loadCurrentPos);
                    setSeekBarProgress(loadCurrentPos);
                }
            }
        }
        if (isStopped) seekBar.setEnabled(false);
    }


/** ТОЧКИ ВЗАИМОДЕЙСТВИЯ (ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ) **/
//в будущем сюда вынесу обертки для методов прев, некст и стоп


    public void playFromMain() {                                                                    //Исключительно для Мэйна! Тут всевозможные проверки перед воспроизведением
        if (getCurrentSong().getID() == 0) {                                                        //если текущая песня не задана
            if (currentPlayList.getCountSongs() == 0) {                                             //проверяем, пустой ли плейлист
                Toast toast = Toast.makeText(App.getAppContext(),                                   //если пуст, то в будущем заместо тоста надо запускать диалоговое окно (добавить песни?)
                        "Плейлист пуст! Добавьте музыку",Toast.LENGTH_SHORT);
                toast.show();
                //тут надо будет запускать интент на ФайлЛист для добавления песен
                return;
            }
            currentPlayList.setFirstSongAsCurrent();                                                //принудительно задаем первую песню плейлиста как текущую
        }
        if (isPlaying) pause();
        else if(!isStopped) resume();
        else {
            //isPlaying = true;
            //isStopped = false;
            preparePlayer();
            play();
        }
    }

    public void playFromSongList() {
        if (isPlaying) timer.cancel();
        mediaPlayer.stop();
        preparePlayer();
        play();
    }


/** УПРАВЛЕНИЕ МУЗЫКОЙ **/
//в будущем все методы сделаю приватными


    public void preparePlayer() {
        mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentSong().getUri());
    }

    public void play() {
//
//        if (isPlaying) mediaPlayer.stop();
//        else isPlaying = true;
        if (isStopped) seekBar.setEnabled(true);
        isPlaying = true;
        isStopped = false;
        //mediaPlayer.stop();

        //mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentSong().getUri());
        mediaPlayer.start();

        timer = new MyTimer(mediaPlayer.getDuration());
        timer.start();

        showSongInfo();
        changePlayButton();
        //playPause.setImageResource(android.R.drawable.ic_media_pause);

    }

    public void pause() {
        isPlaying = false;
        //isPaused = true;
        timer.cancel();

        mediaPlayer.pause();

        changePlayButton();
        //playPause.setImageResource(android.R.drawable.ic_media_play);
    }

    public void resume() {
        isPlaying = true;
        mediaPlayer.start();

        timer = new MyTimer(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition());
        timer.start();//
        ////elapsed.setText();
        changePlayButton();
        //playPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void stop() {
        isPlaying = false;
        isStopped = true;

        timer.cancel();

        mediaPlayer.reset();//

        seekBar.setEnabled(false);
        elapsed.setText(timeFormatter(0));
        seekBar.setProgress(0);
        resetRemainder();
        changePlayButton();
        //playPause.setImageResource(android.R.drawable.ic_media_play);
    }

    public void next() {
        if (currentPlayList.itsPossible(1)) {
            currentPlayList.setNextSong();
            changeSong();
//            timer.cancel();
//
//            mediaPlayer.stop();
//            preparePlayer();
//
//            if (isPlaying) {
//                play();
//            }
//            else {
//                isStopped = true;                                                                   //нужно для того, чтобы при нажатии на плей, выполнялся метод Плей, а не Резьюм
//                showSongInfo();
//            }

//            if (isPlaying) {
//                timer.cancel();//
//                mediaPlayer.stop();
//                preparePlayer();
//                play();
//            }
//            else {
//                stop();
//                preparePlayer();
//                //mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentSong().getUri());   //нужно для того, чтобы получить данные новой песни
//            }
//            //showSongInfo();
        }
        else if (!isStopped) stop();
    }

    public void prev() {
        if (currentPlayList.itsPossible(-1)) {
            currentPlayList.setPrevSong();
            changeSong();
//            if (isPlaying) {
//                mediaPlayer.stop();
//                preparePlayer();
//                play();
//            }
//            else {
//                stop();
//                preparePlayer();
//                //mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentSong().getUri());
//            }
//            //showSongInfo();
        }
        else if (!isStopped) stop();
    }

    public void changeSong() {
        timer.cancel();
        mediaPlayer.reset();//
        preparePlayer();
        if (isPlaying) {
            play();
        }
        else {
            if ( ! isStopped) {                                                                     //если пауза
                elapsed.setText(timeFormatter(0));
                seekBar.setProgress(0);
                //setElapsedTime();
                resetRemainder();   //pause
            }
            showSongInfo();
        }
    }


/** ЗАГРУЗКА И СОХРАНЕНИЕ СОСТОЯНИЯ **/


    public void loadPlayerState() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    App.getPlayerDataPath()));

            currentPlayList.setCurrentSong((Song) ois.readObject());
            currentPlayList.setSongsPlayedCount(ois.readInt());
            isPlaying = ois.readBoolean();
            isStopped = ois.readBoolean();
            loadCurrentPos = ois.readInt();

            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("main","error: " + e.getMessage());
        }

    }

    public void savePlayerState() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    App.getPlayerDataPath()));

//            int playListsCount = ois.readInt();
//            for (int i = 0; i < playListsCount; i++) {
//                playLists.add((PlayList) ois.readObject());
//            }
            //oos.writeObject(player);
            oos.writeObject(getCurrentSong());
            oos.writeInt(currentPlayList.getSongsPlayedCount());
            oos.writeBoolean(isPlaying);
            oos.writeBoolean(isStopped);
            oos.writeInt(getElapsedTime());


            oos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


/** РАБОТА С ПЛЕЙЛИСТАМИ **/


    public void initPlayLists() {
        playLists = new ArrayList<>();                                                              //инициализируем массив плейлистов
        //URI uri = URI.create();

        playListDir = new File(App.getPlayListsDir());                                              //инициализирует директорию плейлистов
        //new File(context.getFilesDir(),"PlayLists");
        if (App.isFirstLaunchApp()) {
            createDefaultPlayList();//если папки с плейлистами не существует, то, очевидно, это первый запуск приложения и нужно создать дефолтный плейлист с папкой
        }
        else {
            loadPlayLists();
            currentPlayList = playLists.get(0);                             //тест
        }
        //for ()
//            playLists = new ArrayList<>();
//            for (int i = 0; i < files.length; i++) {
//                //playLists.add(files[i]);
//
//            }
    }

    public void loadPlayLists() {
        //File[] files = playListDir.listFiles();

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    playListDir.getPath() + "/playlists.hpl"));

            int playListsCount = ois.readInt();
            for (int i = 0; i < playListsCount; i++) {
                playLists.add((PlayList) ois.readObject());
            }

            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlayLists() {                                                                   //в будущем надо сохранять и загружать каждый плейлист отдельно


        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    playListDir.getPath() + "/playlists.hpl"));
            oos.writeInt(playLists.size());

            for (PlayList pl : playLists) {
                oos.writeObject(pl);
            }

            oos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPlayList(String name) {
        PlayList newPlayList = new PlayList(name);
        //if (name.equalsIgnoreCase("default"))
        playLists.add(newPlayList);
        currentPlayList = newPlayList;
//        if (playLists.size() == 1) {
//            currentPlayList = newPlayList;
//        }
        //savePlaylist(newPlayList, name);
        savePlayLists();


    }

    public void createDefaultPlayList() {
//        if (!playListDir.mkdir()) { //если папка с плейлистами не создалась
//            Toast toast = Toast.makeText(this,"Ошибка при создании папки для плейлистов", Toast.LENGTH_SHORT);  //ЗДЕСЬ НУЖЕН КОНТЕКСТ
//            toast.show();
//            System.exit(0); //принудительно закроет приложение, если папка не создастся. Но такого быть не должно
//        }
        playListDir.mkdir();
        //String name = "DEFAULT";
        createPlayList("DEFAULT");

    }

    public void addPlayList() {

    }

    public void refreshPlayList() {

    }

    public void deletePlayList() {}

//    public void savePlaylist(PlayList playList, String name) {
//        //String playListPath = playListDir.getPath() + "/" + name + "hpl";
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(playListDir.getPath() + "/" + name + ".hpl"));
//            oos.writeObject(playList);
//
//            oos.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }



//    public boolean isFirstLaunchApp() {
//        return ! playListDir.exists();
//    }
/** ГЕТТЕРЫ **/


    public PlayList getCurrentPlayList() {
        return currentPlayList;
    }

    public Song getCurrentSong() {
        return currentPlayList.getCurrentSong();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public int getElapsedTime() {
//        if (mediaPlayer.isPlaying())
            return mediaPlayer.getCurrentPosition();
//        else return 0;
    }




/** СЕТТЕРЫ **/


    public void changeCurrentPlayList(PlayList pl) {
        currentPlayList = pl;
    }

    public void setIsPlaying(boolean state) {
        isPlaying = state;
    }

    public void setIsStopped(boolean state) {
        isStopped = state;
    }


/** СЛУЖЕБНЫЕ МЕТОДЫ **/


    public void showSongInfo() {
        title.setText(getCurrentSong().getName());
        artist.setText(getCurrentSong().getAttributes());
        //elapsed.setText(getTime(mediaPlayer.getCurrentPosition()));
        showDuration();
        //trackInfo.setText(getTrackInfo());
        //duration.setText(timeFormatter(mediaPlayer.getDuration()));
    }

    public void changePlayButton() {
        if (isPlaying) playPause.setImageResource(android.R.drawable.ic_media_pause);
        else playPause.setImageResource(android.R.drawable.ic_media_play);
    }

    @SuppressLint("DefaultLocale")
    public String timeFormatter(int totalSec) {
        int min = totalSec / 60;
        int sec = totalSec % 60;
        if (min < 60) {
            return String.format("%d:%02d", min, sec);
        }
        else {
            int hour = totalSec / 3600;
            return String.format("%d:%02d:%02d", hour, min, sec);
        }
    }

    public void showDuration() {
        int secs = mediaPlayer.getDuration() / 1000;
        duration.setText(timeFormatter(secs));
    }

    public void setElapsedTime() {
        int secs = mediaPlayer.getCurrentPosition() / 1000;
        elapsed.setText(timeFormatter(secs));
    }

    public void setSeekBarProgress(int position) {
        long progress = position * 1000L/mediaPlayer.getDuration();
        //remainder.setText(timeFormatter((int) millisUntilFinished/1000));                               //test
        seekBar.setProgress((int) progress);
    }

    public void changeElapsedTime(int seekPos) {
        int secs = mediaPlayer.getDuration() / 1000 * seekPos / 1000;
        elapsed.setText(timeFormatter(secs));
    }

    public void resetRemainder() {
        remainder.setText("--:--");
    }


/** УПРАВЛЕНИЕ SEEKBAR'ом **/


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        changeElapsedTime(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        timer.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(mediaPlayer.getDuration() / 1000 * seekBar.getProgress());
        setElapsedTime();

        if (isPlaying) {
            timer = new MyTimer(mediaPlayer.getDuration()
                    - mediaPlayer.getCurrentPosition());
            timer.start();
        }
        //artist.setText(String.valueOf(seekBar.getProgress()));
    }


/** СЕКУНДНЫЙ СЧЁТЧИК ДЛЯ ОБНОВЛЕНИЯ ОТОБРАЖЕНИЯ ПРОШЕДШЕГО ВРЕМЕНИ И ПЕРЕКЛЮЧЕНИЯ ТРЕКОВ **/


    public class MyTimer extends CountDownTimer {                                                   //таймер обратного отсчета, приспособленный для счетчика:) Надо в будущем заменить

        public MyTimer(int millisInFuture) {                                                        //надо ему либо большое число передавать, либо останавливать счетчик при паузе
            super(millisInFuture, 1000L);
        }

        @Override
        public void onFinish() {
            next();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long progress = mediaPlayer.getCurrentPosition()*1000L/mediaPlayer.getDuration();
            remainder.setText(timeFormatter((int) millisUntilFinished/1000));
            seekBar.setProgress((int) progress);
            setElapsedTime();

        }
    }


/** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) **/


    //    @Override
//    public void onCompletion(MediaPlayer mp) {
//        if (currentPlayList.itsPossible(1)) next(mp);
//        else stop();
//    }

    //    public String getTrackInfo() {
//        if (isPlaying) {
//            MediaPlayer.TrackInfo[] arr = mediaPlayer.getTrackInfo();
//            StringBuilder result = new StringBuilder()
//                    .append(mediaPlayer.getAudioSessionId())
//                    .append("\n");
//            for (MediaPlayer.TrackInfo info : arr) {
//                result.append(info.describeContents())
//                        .append(info.getTrackType())
//                        .append(info.getFormat().toString())
//                        .append(info.getLanguage())
//                        .append("\n");
//            }
//            return result.toString();
//        }
//        else return "";
//    }

    //    public void initViewsToPlayer(ArrayList<Object> views) {
//        title = (TextView) views.get(0);
//        artist = (TextView) views.get(1);
//        elapsed = (TextView) views.get(2);
//        duration = (TextView) views.get(3);
//        seekBar = (SeekBar) views.get(4);
//        playPause = (ImageButton) views.get(5);
//        remainder = (TextView) views.get(6);
//
//        trackInfo = (TextView) views.get(7);
//
//        seekBar.setOnSeekBarChangeListener(this);
//
//        loader();
//
//        if (isStopped) seekBar.setEnabled(false);
//
//    }


}
