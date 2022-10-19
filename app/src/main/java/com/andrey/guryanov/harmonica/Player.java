package com.andrey.guryanov.harmonica;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;

public class Player implements SeekBar.OnSeekBarChangeListener {

    private File playListDir;                                                                       //файл с директорией плейлиста, в будущем удалю
    private MediaPlayer mediaPlayer;                                                                //АПИ встроенного проигрывателя
    private PlayList currentPlayList;                                                               //здесь хранится текущий плейлист
    private ArrayList<PlayList> playLists;                                                          //список всех плейлистов //в будущем удалю
    private boolean isStopped;                                                                      //состояние стопа у плеера (тру на стопе, фолс нет)
    private boolean isPlaying;                                                                      //состояние плея плеера (тру - плей, фолс - пауза)
    //private static MediaParser songScanner;
    //private ArrayList<View> viewArgs;
    private ImageButton playPause;
    private SeekBar seekBar;
    private int loadCurrentPos;
    private TextView title, artist, elapsed, duration, remainder;
    private TextView trackInfo;
    private MyTimer timer;
    private boolean stateIsLoaded;                                                                  //индикатор того, загрузил ли плеер своё состояние из файла или ещё нет

    public Player() {
        initPlayLists();                                                                            //костыль. инициализируем плейлисты //в будущем буду загружать последний плейлист

        mediaPlayer = new MediaPlayer();
        timer = new MyTimer(0);

        isStopped = true;
        isPlaying = false;

        stateIsLoaded = false;
    }


    /**
     * ИНИЦИАЛИЗАЦИЯ ПЛЕЕРА
     **/


    public void initPlayer(ArrayList<Object> views) {
        title = (TextView) views.get(0);
        artist = (TextView) views.get(1);
        elapsed = (TextView) views.get(2);
        duration = (TextView) views.get(3);
        seekBar = (SeekBar) views.get(4);
        playPause = (ImageButton) views.get(5);
        remainder = (TextView) views.get(6);

        trackInfo = (TextView) views.get(7);

        seekBar.setOnSeekBarChangeListener(this);                                                   //вешаем слушатель изменений на Сикбар
        if ( ! stateIsLoaded) loader();                                                             //если плеер еще не загрузил своё состояние, то пусть загрузит. Иначе ничего не делать.
    }                                                                                               //-- Нужно для того, чтобы при переворачивании экрана плеер не загружал состояние снова

    public void loader() {
        if (!App.isFirstLaunchApp()) {                                                              //если это первый запуск приложения после установки
            loadPlayerState();                                                                      //загружаем состояние Плеера из файла
            if (!isStopped) {                                                                       //если должен играть либо на паузе
                preparePlayer();                                                                    //подготавливаем плеер
                if (isPlaying) {                                                                    //если должен играть
                    mediaPlayer.seekTo(loadCurrentPos);                                             //мотаем трек до того места, откуда плеер прервался при закрытии
                    resume();                                                                       //возобновляем воспроизведение
                } else {
                    mediaPlayer.seekTo(loadCurrentPos);                                             //иначе просто мотаем трек до момента остановки
                    setSeekBarProgress(loadCurrentPos);                                             //и ставим Сикбар в нужное положение
                }
            }
        }
        if (isStopped) seekBar.setEnabled(false);                                                   //если воспроизведение было остановлено, то выключаем Сикбар

        stateIsLoaded = true;                                                                       //ставим в известность, что загрузка состояния состоялась, чтобы при перевороте экрана
                                                                                                    //--не загружать состояние снова
    }


    /**
     * ТОЧКИ ВЗАИМОДЕЙСТВИЯ (ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ)
     **/
//в будущем сюда вынесу обертки для методов прев, некст и стоп


    public void playFromMain() {                                                                    //Исключительно для Мэйна! Тут всевозможные проверки перед воспроизведением
        if (getCurrentTrack().getID() == 0) {                                                       //если текущий трек не задан (нулевой ИД значит, что реальный трек не задан)
            if (currentPlayList.getCountTracks() == 0) {                                            //проверяем, пустой ли плейлист
                Toast toast = Toast.makeText(App.getAppContext(),                                   //если пуст, то в будущем заместо тоста надо запускать диалоговое окно (добавить песни?)
                        "Плейлист пуст! Добавьте музыку", Toast.LENGTH_SHORT);
                toast.show();
                //тут надо будет запускать интент на ФайлЛист для добавления песен
                return;
            }
            currentPlayList.setFirstTrackAsCurrent();                                               //принудительно задаем первый трек плейлиста как текущий
        }
        if (isPlaying) pause();
        else if (!isStopped) resume();
        else {
            //isPlaying = true;
            //isStopped = false;
            preparePlayer();
            play();
        }
    }

    public void playFromTrackList() {
        if (isPlaying) timer.cancel();                                                              //выключаем счетчик
        mediaPlayer.stop();
        preparePlayer();
        play();
    }


    /**
     * УПРАВЛЕНИЕ МУЗЫКОЙ
     **/
//в будущем все методы сделаю приватными


    public void preparePlayer() {
        mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentTrack().getUri());          //подготавливает трек перед воспроизведением (ищет его по пути и инициализирует)
    }

    public void play() {
        if (isStopped) seekBar.setEnabled(true);                                                    //если плеер был остановлен, то включаем Сикбар для пользователя
        isPlaying = true;
        isStopped = false;

        mediaPlayer.start();                                                                        //запускаем воспроизведение

        timer = new MyTimer(mediaPlayer.getDuration());                                             //создаём новый счетчик и передаём туда длительность трека
        timer.start();                                                                              //запускаем счетчик

        showTrackInfo();                                                                            //отображаем информацию о треке (название, длительность, остаток времени, и тд)
        changePlayButton();                                                                         //меняем иконку кнопки Плей (будет отображаться иконка паузы)
    }

    public void pause() {
        isPlaying = false;
        timer.cancel();                                                                             //выключаем счетчик

        mediaPlayer.pause();                                                                        //приостанавливаем воспроизведение

        changePlayButton();                                                                         //меняем кнопку
    }

    public void resume() {
        isPlaying = true;
        mediaPlayer.start();                                                                        //возобновляем воспроизведение (метод Старт и запускает и продолжает)

        timer = new MyTimer(mediaPlayer.getDuration()                                    //создаем новый счетчик и передаём туда оставшееся время (длительность - пройденное)
                - mediaPlayer.getCurrentPosition());
        timer.start();                                                                              //запускаем счетчик
        changePlayButton();                                                                         //меняем кнопку
    }

    public void stop() {
        isPlaying = false;
        isStopped = true;

        timer.cancel();                                                                             //выключаем счетчик

        mediaPlayer.reset();                                                                        //сбрасываем проигрыватель

        seekBar.setEnabled(false);                                                                  //выключаем взаимодействие с Сикбаром для пользователя
        resetElapsedTime();                                                                         //сбрасываем пройденное время и Сикбар до 0
        resetRemainder();                                                                           //сбрасываем оставшееся время
        changePlayButton();                                                                         //меняем кнопку
    }

    public void next() {
        if (currentPlayList.itsPossible(1)) {                                                 //если переключение возможно (есть песни далее)
            currentPlayList.setNextTrack();                                                         //метод задаёт следующий трек как текущий
            changeTrack();                                                                          //тут происходит процесс для переключения песни
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
        } else if (!isStopped) stop();                                                              //иначе, если невозможно переключить и плеер не на стопе, то остановить воспроизведение
    }

    public void prev() {                                                                            //всё то же самое, что и для метода Некст
        if (currentPlayList.itsPossible(-1)) {
            currentPlayList.setPrevTrack();
            changeTrack();
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
        } else if (!isStopped) stop();
    }

    public void changeTrack() {
        timer.cancel();                                                                             //останавливает счетчик
        mediaPlayer.stop();                                                                         //останавливает проигрыватель
        preparePlayer();                                                                            //подготавливает проигрыватель
        if (isPlaying) {                                                                            //если перед переключением проигрыватель играл, то сразу запускаем проигрывание
            play();
        } else {                                                                                    //иначе, если пауза
            if (!isStopped) {
                resetElapsedTime();                                                                 //сбрасываем проигранное время и Сикбар
                resetRemainder();                                                                   //сбрасываем оставшееся время
            }
            showTrackInfo();                                                                        //после всего этого отображаем инфо о включаемом треке
        }
    }


    /**
     * ЗАГРУЗКА И СОХРАНЕНИЕ СОСТОЯНИЯ
     **/


    public void loadPlayerState() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    App.getPlayerStatePath()));

            currentPlayList.setCurrentTrack((Track) ois.readObject());
            currentPlayList.setTracksPlayedCount(ois.readInt());
            isPlaying = ois.readBoolean();
            isStopped = ois.readBoolean();
            loadCurrentPos = ois.readInt();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("main", "error: " + e.getMessage());
        }

    }

    public void savePlayerState() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    App.getPlayerStatePath()));

            oos.writeObject(getCurrentTrack());
            oos.writeInt(currentPlayList.getTracksPlayedCount());
            oos.writeBoolean(isPlaying);
            oos.writeBoolean(isStopped);
            oos.writeInt(getElapsedTime());

            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * РАБОТА С ПЛЕЙЛИСТАМИ
     **/


    public void initPlayLists() {
        playLists = new ArrayList<>();                                                              //инициализируем массив плейлистов
        playListDir = new File(App.getPlayListsDir());                                              //инициализирует директорию плейлистов
        if (App.isFirstLaunchApp()) {                                                               //если приложение было запущено впервые с момента установки
            createDefaultPlayList();                                                                //создаем базовый первичный дефолтный плейлист
        } else {
            loadPlayLists();                                                                        //иначе загружаем плейлисты //пока что все плейлисты сохраняются в 1 общий файл
            currentPlayList = playLists.get(0);                                                     //тест //ставим первый плейлист как текущий
        }
    }

    public void loadPlayLists() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    playListDir.getPath() + "/playlists.hpl"));

            int playListsCount = ois.readInt();
            for (int i = 0; i < playListsCount; i++) {                                              //загружаем все плейлисты из файла //костыль, надо загружать каждый плейлист отдельно
                playLists.add((PlayList) ois.readObject());
            }

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlayLists() {                                                                   //в будущем надо сохранять и загружать каждый плейлист отдельно
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    playListDir.getPath() + "/playlists.hpl"));
            oos.writeInt(playLists.size());

            for (PlayList pl : playLists) {                                                         //загружаем все плейлисты из файла //костыль, надо загружать каждый плейлист отдельно
                oos.writeObject(pl);
            }

            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPlayList(String name) {
        PlayList newPlayList = new PlayList(name);
        playLists.add(newPlayList);                                                                 //костыль, в будущем этого списка не будет
        currentPlayList = newPlayList;                                                              //временно задаем новй плейлист как текущий, в будущем этого не будет
//        if (playLists.size() == 1) {
//            currentPlayList = newPlayList;
//        }
        //savePlaylist(newPlayList, name);
        savePlayLists();


    }

    public void createDefaultPlayList() {
//        if (!playListDir.mkdir()) {                                                                 //если папка с плейлистами по каким-то причинам не создалась
//            Toast toast = Toast.makeText(this,"Ошибка при создании папки для плейлистов.",
//            Toast.LENGTH_SHORT);  //ЗДЕСЬ НУЖЕН КОНТЕКСТ
//            toast.show();
//            System.exit(0);                                                                         //принудительно закроет приложение, если папка не создастся. Но такого быть не должно
//        }
        playListDir.mkdir();                                                                        //создаем директорию с плейлистами в папке приложения
        //String name = "DEFAULT";
        createPlayList("DEFAULT");                                                            //демонстративно обзываем его дефолтным

    }

    public void addPlayList() {

    }

    public void refreshPlayList() {

    }

    public void deletePlayList() {

    }

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

    /**
     * ГЕТТЕРЫ
     **/


    public PlayList getCurrentPlayList() {
        return currentPlayList;
    }

    public Track getCurrentTrack() {
        return currentPlayList.getCurrentTrack();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public int getElapsedTime() {
        return mediaPlayer.getCurrentPosition();
    }


    /**
     * СЕТТЕРЫ
     **/


    public void changeCurrentPlayList(PlayList pl) {
        currentPlayList = pl;
    }

    public void setIsPlaying(boolean state) {
        isPlaying = state;
    }

    public void setIsStopped(boolean state) {
        isStopped = state;
    }


    /**
     * СЛУЖЕБНЫЕ МЕТОДЫ
     **/


    public void showTrackInfo() {                                                                   //метод отображает данные трека (название, длительность и тд) на экране Мейна
        title.setText(getCurrentTrack().getName());
        artist.setText(getCurrentTrack().getAttributes());
        //elapsed.setText(getTime(mediaPlayer.getCurrentPosition()));
        showDuration();
        //trackInfo.setText(getTrackInfo());
        //duration.setText(timeFormatter(mediaPlayer.getDuration()));
    }

    public void changePlayButton() {                                                                //метод меняет иконку кнопки Плей в зависимости от состояния проигрывателя
        if (isPlaying) playPause.setImageResource(android.R.drawable.ic_media_pause);
        else playPause.setImageResource(android.R.drawable.ic_media_play);
    }

    @SuppressLint("DefaultLocale")
    public String timeFormatter(int totalSec) {                                                     //метод преображает милисекунды в привычный для человека формат (например, 3:08)
        int min = totalSec / 60;
        int sec = totalSec % 60;
        if (min < 60) {
            return String.format("%d:%02d", min, sec);
        } else {
            int hour = totalSec / 3600;
            return String.format("%d:%02d:%02d", hour, min, sec);
        }
    }

    public void showDuration() {                                                                    //метод показывает длительность текущего трека
        int secs = mediaPlayer.getDuration() / 1000;
        duration.setText(timeFormatter(secs));
    }

    public void setElapsedTime() {                                                                  //метод показывает/обновляет пройденное время текущего трека
        int secs = mediaPlayer.getCurrentPosition() / 1000;
        elapsed.setText(timeFormatter(secs));
    }

    public void setSeekBarProgress(int position) {                                                  //метод передвигает ползунок Сикбара относительно проигранного времени текущего трека
        long progress = position * 1000L / mediaPlayer.getDuration();
        seekBar.setProgress((int) progress);
    }

    public void changeElapsedTime(int seekPos) {                                                    //метод перематывает трек относительно позиции Сикбара
        int secs = mediaPlayer.getDuration() / 1000 * seekPos / 1000;
        elapsed.setText(timeFormatter(secs));
    }

    public void resetRemainder() {                                                                  //метод сбрасывает оставшееся время до стандартного значения (неопределённость)
        remainder.setText("--:--");
    }

    public void rewindTrackTo(int position) {                                                       //метод перематывает трек на 10 секунд

    }

    public void resetElapsedTime() {                                                                //сбрасывает значения пройденного и оставшегося времени до нулей
        elapsed.setText(timeFormatter(0));
        seekBar.setProgress(0);
    }


    /**
     * УПРАВЛЕНИЕ SEEKBAR'ом
     **/


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                //работа сикБара при передвигании ползунка
        changeElapsedTime(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {                                             //работа Сикбара в момент начала изменения позиции ползунка
        timer.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {                                              //работа Сикбара в момент отпускания ползунка после его перемещения
        mediaPlayer.seekTo(mediaPlayer.getDuration() / 1000 * seekBar.getProgress());
        setElapsedTime();

        if (isPlaying) {
            timer = new MyTimer(mediaPlayer.getDuration()
                    - mediaPlayer.getCurrentPosition());
            timer.start();
        }
        //artist.setText(String.valueOf(seekBar.getProgress()));
    }


    /**
     * СЕКУНДНЫЙ СЧЁТЧИК ДЛЯ ОБНОВЛЕНИЯ ОТОБРАЖЕНИЯ ПРОШЕДШЕГО ВРЕМЕНИ И ПЕРЕКЛЮЧЕНИЯ ТРЕКОВ
     **/


    public class MyTimer extends CountDownTimer {                                                   //таймер обратного отсчета, приспособленный для счётчика:) Надо в будущем заменить

        public MyTimer(int millisInFuture) {                                                        //надо ему либо большое число передавать, либо останавливать счетчик при паузе
            super(millisInFuture, 1000L);
        }

        @Override
        public void onFinish() {                                                                    //когда счётчик (таймер) достигает нуля
            next();                                                                                 //переключаем трек на следующий
        }

        @Override
        public void onTick(long millisUntilFinished) {                                              //сам счётчик, выполняет команды раз в секунду
            //long progress = mediaPlayer.getCurrentPosition() * 1000L / mediaPlayer.getDuration();
            //seekBar.setProgress((int) progress);
            setElapsedTime();                                                                       //меняем пройденное время
            remainder.setText(timeFormatter((int) millisUntilFinished / 1000));              //меняем оставшееся время
            setSeekBarProgress(mediaPlayer.getCurrentPosition());                                   //меняем позицию Сикбара
        }
    }

}


    /**
     * СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ)
     **/


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

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (currentPlayList.itsPossible(1)) next();
//                else stop();
//            }
//        });
