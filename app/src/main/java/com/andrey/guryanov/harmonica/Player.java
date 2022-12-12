package com.andrey.guryanov.harmonica;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andrey.guryanov.harmonica.utils.App;
import com.andrey.guryanov.harmonica.utils.Queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Player implements SeekBar.OnSeekBarChangeListener {

    private final int next = 1;
    private final int prev = -1;
//    private File playListDir;                                                                       //файл с директорией плейлиста, в будущем удалю
    private MediaPlayer mediaPlayer;                                                                //АПИ встроенного проигрывателя
    private PlayList currentPlayList;                                                               //здесь хранится текущий плейлист
    //private int countTracks;

//    private ArrayList<PlayList> playLists;                                                          //список всех плейлистов //в будущем удалю

    ////private Track currentTrack;
    private int prevTrackNumber;
    private int currentTrackNumber;
    private int tracksPlayedCounter;
    private boolean isPlaying;                                                                      //состояние проигрывания плеера (тру - играет, фолс - на паузе)
    private boolean isStopped;                                                                      //состояние стопа у плеера (тру на стопе, фолс нет)
    private int loadedElapsedTime;                                                                   //используется только при загрузке состояния для возобновления воспроизведения трека //-- с того места, откуда оно приостановилось
    private int playMode;                                                                           //режим очереди (0-последовательный, 1-случайно-последовательный, 2-абсолютно случайный)
    private List<Integer> trackQueue;                                                               //очередь треков
//    private boolean canPlayDemo;

    private TrackList trackList;
    private TrackListAdapter trackListAdapter;
    //private static MediaParser songScanner;
    //private ArrayList<View> viewArgs;
    private ImageButton playPause;
    private SeekBar seekBar;

    private TextView title, artist, elapsed, duration, remainder, trackNumber, playModeView;
    private MyTimer timer;
    private boolean stateIsLoaded;                                                                  //индикатор того, загрузил ли плеер своё состояние из файла или ещё нет
    private Random random;

    public Player() {
        initPlayList();                                                                            //костыль. инициализируем плейлисты //в будущем буду загружать последний плейлист

        mediaPlayer = new MediaPlayer();
        timer = new MyTimer(0);                                                                     //инициализируем пустой таймер, чтобы не возникало NPE
        isStopped = true;
        isPlaying = false;
        stateIsLoaded = false;
        random = new Random();
        //countTracks = getCountTracksFromPlayList();

        ////currentTrack = currentPlayList.getCurrentTrack();
        currentTrackNumber = currentPlayList.getCurrentTrackNumber();
        trackQueue = currentPlayList.getQueue();
    }


    /** ИНИЦИАЛИЗАЦИЯ ПЛЕЕРА */


    public void initPlayer(ArrayList<Object> views) {                                               //метод инициализирует вьюшки Мэйна для управления ими
        title = (TextView) views.get(0);
        artist = (TextView) views.get(1);
        elapsed = (TextView) views.get(2);
        duration = (TextView) views.get(3);
        seekBar = (SeekBar) views.get(4);
        playPause = (ImageButton) views.get(5);
        remainder = (TextView) views.get(6);
        trackNumber = (TextView) views.get(7);
        playModeView = (TextView) views.get(8);

        seekBar.setOnSeekBarChangeListener(this);                                                   //вешаем слушатель изменений на Сикбар
        if ( ! stateIsLoaded) loader();                                                             //если плеер еще не загрузил своё состояние, то пусть загрузит. Иначе ничего не делать
    }                                                                                               //-- Нужно для того, чтобы при переворачивании экрана плеер не загружал состояние снова

    /** ! */
    private void loader() {                                                                         //загрузчик состояния плеера. Метод проверяет, загружено ли состояние
//        if ( ! App.isFirstLaunchApp()) {// !                                                            //если это НЕ первый запуск приложения после установки
        if (countTracks() > 0) {
            loadPlayerState();                                                                      //загружаем состояние Плеера из файла
            showPlayMode();
            if (!isStopped) {                                                                       //если должен играть либо на паузе
                preparePlayer();                                                                    //подготавливаем плеер
                if (isPlaying) {                                                                    //если должен играть
                    mediaPlayer.seekTo(loadedElapsedTime);                                           //мотаем трек до того места, откуда плеер прервался при закрытии
                    resume();                                                                       //возобновляем воспроизведение
                } else {
                    mediaPlayer.seekTo(loadedElapsedTime);                                           //иначе просто мотаем трек до момента остановки
                    setSeekBarProgress(loadedElapsedTime);                                           //и ставим Сикбар в нужное положение
                }
            }
            else seekBar.setEnabled(false);                                                   //если воспроизведение было остановлено, то выключаем Сикбар
        }
        else seekBar.setEnabled(false);
        // * //if (isStopped) seekBar.setEnabled(false);                                                   //если воспроизведение было остановлено, то выключаем Сикбар
        stateIsLoaded = true;                                                                       //ставим в известность, что загрузка состояния состоялась, чтобы при перевороте экрана
                                                                                                    //--не загружать состояние снова
    }


    /** ТОЧКИ ВЗАИМОДЕЙСТВИЯ (ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ) */                                            //в будущем сюда вынесу обертки для методов прев, некст и стоп


    public void playFromMain() {                                                                    //Исключительно для Мэйна! Тут всевозможные проверки перед воспроизведением
        if (currentTrackNumber == -1) {                                                       //если текущий трек не задан
//            if (countTracks() == 0 && !canPlayDemo) {
//            if (countTracks() == 0) {                                                               //проверяем, пустой ли плейлист
//                Toast toast = Toast.makeText(App.getAppContext(),                                   //если пуст, то в будущем заместо тоста надо запускать диалоговое окно (добавить песни?)
//                        "Плейлист пуст! Добавьте музыку.", Toast.LENGTH_SHORT);// \n" +"При следующем нажатии будет запущен стандартный трек."
//                toast.show();
//
//                //тут надо будет запускать интент на ФайлЛист для добавления песен
////                currentTrackNumber = -2;
////                canPlayDemo = true;   //demo
//                return;
//            }
//            if (canPlayDemo) {
//                playDemoTrack();
//                currentTrackNumber = -2;
//                return;
//            }
////            currentPlayList.setFirstTrackAsCurrent();                                               //принудительно задаем первый трек плейлиста как текущий
//            else setCurrentTrack(0);
            setCurrentTrack(0);
        }
        if (isPlaying) pause();
        else if ( ! isStopped) resume();
        else {
            preparePlayer();
            play();
        }
    }

    public void playFromTrackList(int position) {
        //if (playMode < 2) makeChangesToTrackQueue(position);
        setCurrentTrackFromTrackList(position);
        if (isPlaying) timer.cancel();                                                              //если идёт воспроизведение, выключаем счетчик
        mediaPlayer.stop();
        preparePlayer();
        play();
        trackList.setTrackName(getCurrentTrack().getName());
    }

    public void switchPlayMode() {                                                               //метод переключает режим воспроизведения
        if (countTracks() < 2) {
            playModeView.setText("");
            return;                                                              //если кол-во треков меньше 2 - прекращаем работу метода (из-за ненадобности)
        }
        if (playMode < 2) {
            playMode++;
            if (playMode == 1) {
                shuffleTrackQueue();
                tracksPlayedCounter = 0;
                //playModeView.setText("Псевдо-случайный режим");
            }
            //else playModeView.setText("Абсолютно случайный режим");
        }
        else {
            playMode = 0;
            sortTrackQueue();
            tracksPlayedCounter = currentTrackNumber;
            //playModeView.setText("Последовательный режим");
        }
        showTrackNumber();
        showPlayMode();

    }


    /***/

    private void setCurrentTrack(int index) {
        prevTrackNumber = currentTrackNumber;
        currentTrackNumber = index;
        ////currentTrack = getTrackFromPlayList(index);
    }

    private void sortTrackQueue() {
        Collections.sort(trackQueue);
    }

    private void shuffleTrackQueue() {
        Collections.shuffle(trackQueue);
        // $ можно заменить цикл на метод indexOf
        for (int i = 0; i < trackQueue.size(); i++) {
            if (trackQueue.get(i).equals(currentTrackNumber)) {
                trackQueue.set(i, trackQueue.get(0));
                trackQueue.set(0, currentTrackNumber);
                break;
            }
        }
    }

    private void setRandomTrack() {
        setCurrentTrack(random.nextInt(countTracks()));
    }

    private boolean itsPossible(int direction) {                                                    //метод вычисляет, можно ли переключить трек в указанном направлении, и возвращает ответ
        if (direction < 0) return tracksPlayedCounter > 0;                                          //если намерение переключить назад, то вернёт тру, если это не первый трек в очереди
        else return countTracks() > 1 && tracksPlayedCounter < (countTracks() - 1);                     //если намерение переключить вперёд, то вернёт тру, если треков > 1 и трек не последний
                                                                                                    //-- в очереди
    }



    /** УПРАВЛЕНИЕ МУЗЫКОЙ */


    private void preparePlayer() {                                                                  //метод подготавливает проигрыватель
        mediaPlayer = MediaPlayer.create(App.getAppContext(), getCurrentTrack().getUri());          //подготавливает трек перед воспроизведением (ищет его по пути и инициализирует)
    }

    private void play() {
        if (isStopped) seekBar.setEnabled(true);                                                    //если плеер был остановлен, то включаем Сикбар для пользователя
        isPlaying = true;
        isStopped = false;

        mediaPlayer.start();                                                                        //запускаем воспроизведение

        timer = new MyTimer(mediaPlayer.getDuration());                                             //создаём новый счетчик и передаём туда длительность трека
        timer.start();                                                                              //запускаем счетчик

//        if ( !canPlayDemo) showTrackInfo();
        showTrackInfo();                                                                            //отображаем информацию о треке (название, длительность, остаток времени, и тд)
        changePlayButton();                                                                         //меняем иконку кнопки Плей (будет отображаться иконка паузы)
    }

    private void pause() {
        isPlaying = false;
        timer.cancel();                                                                             //выключаем счетчик

        mediaPlayer.pause();                                                                        //приостанавливаем воспроизведение

        changePlayButton();                                                                         //меняем кнопку
    }

    private void resume() {
        isPlaying = true;
        mediaPlayer.start();                                                                        //возобновляем воспроизведение (метод Старт и запускает и продолжает)

        timer = new MyTimer(mediaPlayer.getDuration()                                               //создаем новый счетчик и передаём туда оставшееся время (длительность - пройденное)
                - mediaPlayer.getCurrentPosition());
        timer.start();                                                                              //запускаем счетчик
        changePlayButton();                                                                         //меняем кнопку
    }

    public void stop() {
        timer.cancel();                                                                             //выключаем счетчик

        mediaPlayer.reset();                                                                        //сбрасываем проигрыватель

        isPlaying = false;
        isStopped = true;
        seekBar.setEnabled(false);                                                                  //выключаем взаимодействие с Сикбаром для пользователя
        changePlayButton();                                                                         //меняем кнопку
//        if (canPlayDemo) {
//            stopDemoTrack();
//            return;
//        }
        resetElapsedTime();                                                                         //сбрасываем пройденное время и Сикбар до 0
        resetRemainderTime();                                                                           //сбрасываем оставшееся время
    }

    public void next() {
//        if (playMode == 2) {
//            setRandomTrack();
//            switchTrack();
//            return;
//        }

//        if (changeTrack(next)) {                                                        //если переключение возможно (ещё есть треки далее в очереди)             //задаём следующий трек из очереди как текущий
        if (canSwitchTrack(next)) {
            prepareSwitchTrack(next);
            switchTrack();                                                                          //тут происходит процесс переключения трека
        } else if ( ! isStopped) stop();                                                            //иначе, если невозможно переключить и плеер не на стопе, то остановить воспроизведение
    }

    public void prev() {                                                                            //всё то же самое, что и для метода Некст
//        if (playMode == 2) {
//            setRandomTrack();
//            switchTrack();
//            return;
//        }

//        if (changeTrack(prev)) {                                                      //если переключение возможно (есть треки позади в очереди)
        if (canSwitchTrack(prev)) {
            prepareSwitchTrack(prev);
            switchTrack();
        } else if ( ! isStopped) stop();
    }

    private boolean canSwitchTrack(int direction) {                                                 //метод проверяет, можно ли переключить трек
        if (playMode == 2) return true;                                                             //если включён случайный режим, то всегда разрешено
        if (direction == next) return countTracks() > 1                                             //если намерение переключить вперёд, то можно, при условии, что кол-во треков > 1
                && tracksPlayedCounter < (countTracks() - 1);                                       //-- и кол-во проигранных треков меньше, чем кол-во всех треков - 1
        else return tracksPlayedCounter > 0;                                                        //если намерение переключить назад, то можно, если кол-во проигранных треков > 0
    }

    private void prepareSwitchTrack(int direction) {
        if (playMode == 2) {
            setRandomTrack();
        }
        else {
            tracksPlayedCounter += direction;
            setCurrentTrack(trackQueue.get(tracksPlayedCounter));
        }
    }

    private void setCurrentTrackFromTrackList(int pos) {
        if (playMode == 1) {
            tracksPlayedCounter++;
            Integer o = pos;
            trackQueue.remove(o);
            trackQueue.add(tracksPlayedCounter, o);
        }
        else {
            tracksPlayedCounter = pos;
        }
        setCurrentTrack(pos);
    }

    private void switchTrack() {                                                                    //метод переключает трек
        timer.cancel();                                                                             //останавливаем счетчик
        mediaPlayer.stop();                                                                         //останавливаем проигрыватель
        preparePlayer();                                                                            //подготавливаем проигрыватель
        if (isPlaying) {                                                                            //если перед переключением проигрыватель играл, то сразу запускаем проигрывание
            play();
        } else {                                                                                    //иначе, если пауза
            if ( ! isStopped) {
                resetElapsedTime();                                                                 //сбрасываем проигранное время и Сикбар
                resetRemainderTime();                                                                   //сбрасываем оставшееся время
            }
            showTrackInfo();                                                                        //после всего этого отображаем инфо о включаемом треке
        }
        if (App.getCurrentLocation().equals("TrackList")) {
            trackList.setTrackName(getCurrentTrack().getName());
            trackList.changePlayButton();
            trackListAdapter.reselectHolder();
            trackListAdapter.scrollToTrack();

        }
        //if (trackList != null) trackList.scrollToPosition(getCurrentTrackNumber() - (trackList.getHeight() / trackListAdapter.getHolderHeight()) / 2);//trackList.scrollToPosition(getCurrentTrackNumber());
//        if (trackList != null) trackList.scrollBy(0, (trackList.computeVerticalScrollExtent() / -2)     //-835
//                + (trackListAdapter.getHolderHeight()           //108
//                * (getCurrentTrackNumber() - getPrevTrackNumber()  )) //
//                - (trackListAdapter.getHolderHeight() / 2));

//        if (trackList != null) trackList.scrollBy(0, (trackListAdapter.getHolderHeight()
//                * (getCurrentTrackNumber() - getPrevTrackNumber()  )));
//        if (trackList != null) trackList.scrollToPosition(currentTrackNumber);
//        if (trackList != null) trackList.scroll();
//        if (trackListAdapter != null) reselectTrack();
    }

    public void rewindTrackTo10Sec(int direction) {                                                 //метод перематывает трек на 10 секунд вперёд или назад, в зависимости от аргумента
        int mSecs = 10000;
        if (direction == next && getRemainderTime() > mSecs) {
            seekTrackTo(mediaPlayer.getCurrentPosition() + mSecs);
        }
        if (direction == prev) {
            if (mediaPlayer.getCurrentPosition() > mSecs) {
                seekTrackTo(mediaPlayer.getCurrentPosition() - mSecs);
            }
            else seekTrackTo(0);
        }
    }



    /** ЗАГРУЗКА И СОХРАНЕНИЕ СОСТОЯНИЯ */


    public void loadPlayerState() {                                                                 //метод загружает состояние плеера, сохранённое в память при выходе из приложения
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    App.getPlayerStatePath()));

            ////currentTrack = (Track) ois.readObject();
//            if (currentPlayList.getCountTracks() <= 0) return;
            currentTrackNumber = ois.readInt();
            tracksPlayedCounter = ois.readInt();
            isPlaying = ois.readBoolean();
            isStopped = ois.readBoolean();
            loadedElapsedTime = ois.readInt();
            playMode = ois.readInt();

            Queue queue = (Queue) ois.readObject();
            trackQueue = queue.get();

            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("player", "error: PlayerState is not loaded! " + e.getMessage());
        }
    }

    public void savePlayerState() {                                                                 //метод сохраняет состояние плеера в память при выходе из приложения
//        if (currentPlayList.getCountTracks() <= 0) return;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    App.getPlayerStatePath()));

            ////oos.writeObject(currentTrack);
            oos.writeInt(currentTrackNumber);
            oos.writeInt(tracksPlayedCounter);
            oos.writeBoolean(isPlaying);
            oos.writeBoolean(isStopped);
            oos.writeInt(getElapsedTime());
            oos.writeInt(playMode);
            oos.writeObject(new Queue(trackQueue));

            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("player", "error: PlayerState is not saved! " + e.getMessage());
        }
    }


    /** РАБОТА С ПЛЕЙЛИСТАМИ */


    private void initPlayList() {                                                                  //метод инициализирует плейлист при создании объекта Плеер (при включении приложения)
        if (App.isFirstLaunch()) {                                                               //если приложение было запущено впервые с момента установки
            createDefaultPlayList();                                                                //создаем базовый первичный дефолтный плейлист
        } else {
            loadPlayList("DEFAULT");                                                                        //иначе загружаем плейлисты //пока что все плейлисты сохраняются в 1 общий файл
//            currentPlayList = playLists.get(0);                                                     //тест //ставим первый плейлист как текущий
        }
        //getInfoFromPlayList();
    }

    private void loadPlayList(String name) {                                                                  //метод загружает плейлист из памяти устройства
        String path = App.getPlayListsDir() + "/" + name + ".hpl";
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));

            currentPlayList = (PlayList) ois.readObject();

            ois.close();
            Log.e("player", "PlayList is loaded successfully! ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("player", "error: PlayList is not loaded! " + e.getMessage());
        }
    }

    public void savePlayList(PlayList playList) {                                                                   //метод сохраняет плейлист в память устройства
        String path = App.getPlayListsDir() + "/" + playList.getName() + ".hpl";
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));

            oos.writeObject(playList);

            oos.close();
            Log.e("player", "PlayList is saved successfully! ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("player", "error: PlayList is not saved! " + e.getMessage());
        }
    }

    public void createPlayList(String name) {                                                       //метод создаёт новый плейлист
        PlayList newPlayList = new PlayList(name);
        savePlayList(newPlayList);
    }

    private void createDefaultPlayList() {                                                          //метод создаёт первичный плейлист (при первом запуске приложения после установки)
//        playListDir.mkdir();                                                                        //создаем директорию с плейлистами в папке приложения
//        createPlayList("DEFAULT");                                                                  //демонстративно обзываем его дефолтным
        currentPlayList = new PlayList("DEFAULT");                                                  //демонстративно обзываем его дефолтным
        savePlayList(currentPlayList);
    }







    public void changePlayList() {
        //записываем состояние плеера в текущий плейлист (трек, суррентПлейКаунт, элапседТайм, //исСтоппед-фолс)
        //сохраняем текущий плейлист в памяти устройства
        //переключаем плейлист (загружаем нужный и ставим как текущий)
    }

    public void deletePlayList() {
        //удаляем нужный плейлист из памяти устройства
    }

    private void getInfoFromPlayList() {
        Object[] info = currentPlayList.getInfo();
        //name = (String) info[0];
        //currentTrack = (Track) info[1];
        currentTrackNumber = (int) info[2];
        loadedElapsedTime = (int) info[3];
        isPlaying = (boolean) info[4];
        isStopped = (boolean) info[5];
        tracksPlayedCounter = (int) info[6];
        playMode = (int) info[7];

        trackQueue = currentPlayList.getQueue();
    }

    /** ГЕТТЕРЫ */


    public PlayList getCurrentPlayList() {
        return currentPlayList;
    }

    public Track getCurrentTrack() {
        ////return currentTrack;
        return getTrackFromPlayList(currentTrackNumber);
    }

    public int getCurrentTrackNumber() {
        return currentTrackNumber;
    }

    public int getPrevTrackNumber() {
        return prevTrackNumber;
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


    /** СЕТТЕРЫ */


//    public void setCurrentTrack(Track track) {
//        currentTrack = track;
//    }

    public void setCurrentTrackNumber(int number) {
        currentTrackNumber = number;
    }

    public void changeCurrentPlayList(PlayList pl) {
        currentPlayList = pl;
    }

    public void setIsPlaying(boolean state) {
        isPlaying = state;
    }

    public void setIsStopped(boolean state) {
        isStopped = state;
    }

    public void setTrackListData(TrackListAdapter trackListAdapter, TrackList trackList) {//костыль и моветон
        this.trackListAdapter = trackListAdapter;
        this.trackList = trackList;
    }


    /** ПУБЛИЧНЫЕ СЛУЖЕБНЫЕ МЕТОДЫ */


    public void showTrackInfo() {                                                                   //метод отображает данные трека (название, длительность и тд) на экране Мейна
        title.setText(getCurrentTrack().getName());
        artist.setText(getCurrentTrack().getAttributes());
        showDuration();
        showTrackNumber();
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

    public void changePlayButton() {                                                                //метод меняет иконку кнопки Плей в зависимости от состояния проигрывателя
        if (isPlaying) playPause.setImageResource(android.R.drawable.ic_media_pause);
        else playPause.setImageResource(android.R.drawable.ic_media_play);
//        if (App.getCurrentLocation().equals("TrackList")) trackList.changePlayButton();
//        else {
//            if (isPlaying) playPause.setImageResource(android.R.drawable.ic_media_pause);
//            else playPause.setImageResource(android.R.drawable.ic_media_play);
//        }
    }

//    public void changePlayButton(ImageButton ib) {                                                                //метод меняет иконку кнопки Плей в зависимости от состояния проигрывателя
//        if (isPlaying) ib.setImageResource(android.R.drawable.ic_media_pause);
//        else ib.setImageResource(android.R.drawable.ic_media_play);
//    }

//    public void reselectTrack() {
////        trackListAdapter.getSelectedHolder().unSelect();
////        trackListAdapter.reselectHolder();
//    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


//    private void playDemoTrack() {
////        getCurrentPlayList().addTrack("EXTEKTR - Harmonica", R.raw.demo_song, "mp3");
//        mediaPlayer = MediaPlayer.create(App.getAppContext(), R.raw.demo_song);
//        title.setText("EXTEKTR - Harmonica");
//        artist.setText("Demo song");
//        //duration.setText("2:04");
//        play();
//        showDuration();
//    }
//
//    private void stopDemoTrack() {
//        canPlayDemo = false;
//        currentTrackNumber = -1;
////        String s = "--:--";
//        title.setText("Title");
//        artist.setText("Artist");
//        elapsed.setText("--:--");
//        remainder.setText("--:--");
//        duration.setText("--:--");
//        seekBar.setProgress(0);
//    }

    private int getCurrentTrackNumberFromQueue() {
        return trackQueue.get(tracksPlayedCounter);
    }

    private int countTracks() {
        return currentPlayList.getCountTracks();
    }

    private Track getTrackFromPlayList(int index) {
        return currentPlayList.getTracks().get(index);
    }

    private void showTrackNumber() {                                                                //метод отображает номер текущего трека в плейлисте и позицию в очереди
        String s;
        if (playMode == 2) {
            s = "[#" + (currentTrackNumber + 1) + "]";
        }
        else {
            s = "[#" + (currentTrackNumber + 1) + "] :: ["
                    + (tracksPlayedCounter + 1) + "/"
                    + countTracks() + "]";
        }
        trackNumber.setText(s);
    }

    private void showDuration() {                                                                   //метод показывает длительность текущего трека
        int secs = mediaPlayer.getDuration() / 1000;
        duration.setText(timeFormatter(secs));
    }

    private void showPlayMode() {
        if (playMode == 0) playModeView.setText("Последовательный режим");
        else if (playMode == 1) playModeView.setText("Псевдо-случайный режим");
        else playModeView.setText("Абсолютно случайный режим");
    }

    private void seekTrackTo(int mSec) {
        if (isStopped) return;
        mediaPlayer.seekTo(mSec);
        //setSeekBarProgress(mediaPlayer.getCurrentPosition());
        if (isPlaying) {
            timer.cancel();
            startTimer();
        }
        else setSeekBarProgress(mediaPlayer.getCurrentPosition());
    }

    private void setSeekBarProgress(int position) {                                                 //метод передвигает ползунок Сикбара относительно проигранного времени текущего трека
        long progress = position * 1000L / mediaPlayer.getDuration();
        seekBar.setProgress((int) progress);
    }

        /** ! */
    private void setElapsedTime() {                                                                 //метод получает и отображает пройденное время текущего трека
        //int secs = mediaPlayer.getCurrentPosition() / 1000;
//        elapsed.setText(timeFormatter(msecs / 1000));
//        int eSecs = pos / 1000;
//        int rSecs = rem / 1000;
        int eSecs = mediaPlayer.getCurrentPosition() / 1000;
        int rSecs = mediaPlayer.getDuration() / 1000 - eSecs;
        elapsed.setText(timeFormatter(eSecs));
        remainder.setText(timeFormatter(rSecs));
    }

    private void resetElapsedTime() {                                                               //сбрасывает значения пройденного и оставшегося времени до нулей
        elapsed.setText(timeFormatter(0));
        seekBar.setProgress(0);
    }

        /** ! */
    private void changeElapsedTime(int seekPos) {                                                   //метод отображает пройденное время трека относительно позиции Сикбара
//        int secs = mediaPlayer.getDuration() / 1000 * seekPos / 1000;
//        elapsed.setText(timeFormatter(secs));
        int eSecs = mediaPlayer.getDuration() / 1000 * seekPos / 1000;
        int rSecs = mediaPlayer.getDuration() / 1000 - eSecs;
        elapsed.setText(timeFormatter(eSecs));
        remainder.setText(timeFormatter(rSecs));
    }

    private int getRemainderTime() {
        return mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
    }

    private void resetRemainderTime() {                                                                 //метод сбрасывает оставшееся время до стандартного значения (неопределённость)
        remainder.setText("--:--");
    }

    private void startTimer() {
        //timer = new MyTimer(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition());
        timer = new MyTimer(getRemainderTime());
        timer.start();
    }


    /** УПРАВЛЕНИЕ SEEKBAR'ом */


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {                //работа сикБара при передвигании ползунка
        changeElapsedTime(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {                                             //работа Сикбара в момент начала изменения позиции ползунка
        timer.cancel();
        //resetRemainderTime();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {                                              //работа Сикбара в момент отпускания ползунка после его перемещения
        mediaPlayer.seekTo(mediaPlayer.getDuration() / 1000 * seekBar.getProgress());
        //setElapsedTime(mediaPlayer.getCurrentPosition());

        if (isPlaying) {
            startTimer();
//            timer = new MyTimer(mediaPlayer.getDuration()
//                    - mediaPlayer.getCurrentPosition());
//            timer.start();
        }
    }


    /** СЕКУНДНЫЙ СЧЁТЧИК ДЛЯ ОБНОВЛЕНИЯ ОТОБРАЖЕНИЯ ПРОШЕДШЕГО ВРЕМЕНИ И ПЕРЕКЛЮЧЕНИЯ ТРЕКОВ */


    private class MyTimer extends CountDownTimer {                                                  //таймер обратного отсчета, приспособленный для счётчика:) Надо в будущем заменить
//        int interval;
        int remainedTime;
        int currentPosition;
        int duration;

        public MyTimer(int millisInFuture) {                                                        //надо ему либо большое число передавать, либо останавливать счетчик при паузе
            super(millisInFuture, 500L);
//            interval = 500;
//            remainedTime = millisInFuture;
            duration = mediaPlayer.getDuration();
        }

        @Override
        public void onFinish() {                                                                    //когда счётчик (таймер) достигает нуля
            next();                                                                                 //переключаем трек на следующий
        }

        @Override
        public void onTick(long millisUntilFinished) {                                              //сам счётчик, выполняет команды раз в секунду
            //long progress = mediaPlayer.getCurrentPosition() * 1000L / mediaPlayer.getDuration();
            //seekBar.setProgress((int) progress);

//            setElapsedTime();                                                                       //меняем пройденное время
//            remainder.setText(timeFormatter((int) millisUntilFinished / 1000));                     //меняем оставшееся время
//            setSeekBarProgress(mediaPlayer.getCurrentPosition());                                   //меняем позицию Сикбара

            //задать элапсед тайм //надо отредачить метод
            setElapsedTime();
            remainedTime = (int) millisUntilFinished;
            currentPosition = duration - remainedTime;
            //setElapsedTime(currentPosition, remainedTime);
            //remainder.setText(timeFormatter((int) millisUntilFinished / 1000));
            setSeekBarProgress(currentPosition);
        }
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//        if ( ! playListDir.mkdir()) {                                                               //если папка с плейлистами по каким-то причинам не создалась
//            Toast toast = Toast.makeText(App.getAppContext(),
//                    "Ошибка при создании папки для плейлистов.", Toast.LENGTH_SHORT);
//            toast.show();
//            System.exit(0);                                                                         //принудительно закроет приложение, если папка не создастся. Но такого быть не должно
//        }

//    public void next() {
//        if (currentPlayList.itsPossible(1)) {                                                       //если переключение возможно (ещё есть треки далее в очереди)
//            currentPlayList.setNextTrack();                                                         //задаём следующий трек из очереди как текущий
//            changeTrack();                                                                          //тут происходит процесс переключения трека
//        } else if ( ! isStopped) stop();                                                            //иначе, если невозможно переключить и плеер не на стопе, то остановить воспроизведение
//    }

//
//    private boolean changeTrack(int direction) {                                                     //метод переключает трек согласно очереди и направления (-1 - предыдущий, 1 - следующий)
//        if (itsPossible(direction)) {                                                               //если переключение трека в указанном направлении возможно
//            //if (currentTrack == getTrackFromPlayList(currentTrackNumber - 1 + direction)) {       //если текущий трек равен тому треку, на который хотим переключить
//            if (currentTrackNumber == trackQueue.get(currentTrackNumber + direction)) {
//                tracksPlayedCounter += direction;                                                   //тогда меняем счётчик воспроизведённых треков на 1 согласно направления, чтобы потом
//                //-- переключиться на другой трек согласно очереди, а не включать текущий трек повторно.
//                //в будущем привяжу всё к очереди                                                   //-- Актуально потому, что треки можно переключать ещё и из ТрекЛиста //можно улучшить
//                if (tracksPlayedCounter == 0 || tracksPlayedCounter == countTracks() - 1) {        //если счётчик стал равен недопустимым значениям
//                    return false;                                                                   //сообщаем о том, что переключить трек нельзя
//                }
//            }
//            //currentTrack = getTrackFromPlayList(currentTrackNumber - 1 + direction);              //берём очередной трек и ставим его как текущий (переключаем)
//            tracksPlayedCounter += direction;                                                       //меняем счетчик воспроизведённых треков на 1 в зависимости от направления переключения
//            currentTrackNumber = trackQueue.get(tracksPlayedCounter);    //
//            return true;                                                                            //возвращаем результат о том, что всё переключилось как надо
//        }
//        else return false;                                                                          //иначе даём знать о том, что трек не переключился
//    }

//    private boolean newChangeTrack(int direction) {
//        if (playMode == 2) {
//            setRandomTrack();
//            return true;
//        }
//        else {
//            if ( ! itsPossible(direction)) return false;
//            if (playMode == 0) {
//
//            }
//            else {
//
//            }
//            return true;
//        }
//    }

//    public boolean isFirstLaunchApp() {
//        return ! playListDir.exists();
//    }

//        if (direction == prev && mediaPlayer.getCurrentPosition() > mSecs) {
//            rewindTrack(-mSecs);
//        }
//        if (mediaPlayer.getCurrentPosition() > 10000 && getRemainderTime() > 10000) {
//            timer.cancel();
//            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + (10000 * direction));
//            setSeekBarProgress(mediaPlayer.getCurrentPosition());
//            startTimer();
//        }

//        if (direction == next) {
//            if (getRemainderTime() >= 10000) {
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
//                setSeekBarProgress(mediaPlayer.getCurrentPosition());
//            }
//        }
//        else {
//            if (mediaPlayer.getCurrentPosition() > 10000){
//                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
//                setSeekBarProgress(mediaPlayer.getCurrentPosition());
//            }
//        }

//    private void initPlayLists() {                                                                  //метод инициализирует плейлист при создании объекта Плеер (при включении приложения)
//        playLists = new ArrayList<>();                                                              //инициализируем массив плейлистов
//        playListDir = new File(App.getPlayListsDir());                                              //инициализирует директорию плейлистов
//        if (App.isFirstLaunchApp()) {                                                               //если приложение было запущено впервые с момента установки
//            createDefaultPlayList();                                                                //создаем базовый первичный дефолтный плейлист
//        } else {
//            loadPlayLists();                                                                        //иначе загружаем плейлисты //пока что все плейлисты сохраняются в 1 общий файл
//            currentPlayList = playLists.get(0);                                                     //тест //ставим первый плейлист как текущий
//        }
//        //getInfoFromPlayList();
//    }
//
//    private void loadPlayLists() {                                                                  //метод загружает плейлист из памяти устройства
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//                    playListDir.getPath() + "/playlists.hpl"));
//
//            int playListsCount = ois.readInt();
//            for (int i = 0; i < playListsCount; i++) {                                              //загружаем все плейлисты из файла //костыль, надо загружать каждый плейлист отдельно
//                playLists.add((PlayList) ois.readObject());
//            }
//
//            ois.close();
//            Log.e("player", "PlayList is loaded successfully! ");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("player", "error: PlayList is not loaded! " + e.getMessage());
//        }
//    }
//
//    public void savePlayLists() {                                                                   //метод сохраняет плейлист в память устройства
//        String path = playListDir.getPath() + "/playlists.hpl";
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream( path));
//            //playListDir.getPath() + "/playlists.hpl"));
//
//            oos.writeInt(playLists.size());
//
//            for (PlayList pl : playLists) {                                                         //загружаем все плейлисты из файла //костыль, надо загружать каждый плейлист отдельно
//                //здесь присваиваем переменным плейлиста текущие значения
//                oos.writeObject(pl);                                                                //сохраняем плейлист
//            }
//
//            oos.close();
//            Log.e("player", "PlayLists is saved successfully! ");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("player", "error: PlayLists is not saved! " + e.getMessage());
//        }
//    }
//
//    public void createPlayList(String name) {                                                       //метод создаёт новый плейлист
//        PlayList newPlayList = new PlayList(name);
//        playLists.add(newPlayList);                                                                 //костыль, в будущем этого списка не будет
//        currentPlayList = newPlayList;                                                              //временно задаем новй плейлист как текущий, в будущем этого не будет
////        if (playLists.size() == 1) {
////            currentPlayList = newPlayList;
////        }
////        savePlaylist(newPlayList, name);
//        savePlayLists();
//    }
//
//    private void createDefaultPlayList() {                                                          //метод создаёт первичный плейлист (при первом запуске приложения после установки)
//
//        playListDir.mkdir();                                                                        //создаем директорию с плейлистами в папке приложения
//        createPlayList("DEFAULT");                                                                  //демонстративно обзываем его дефолтным
//    }

//        playLists.add(newPlayList);                                                                 //костыль, в будущем этого списка не будет
//        currentPlayList = newPlayList;                                                              //временно задаем новй плейлист как текущий, в будущем этого не будет
//        if (playLists.size() == 1) {
//            currentPlayList = newPlayList;
//        }
//        savePlaylist(newPlayList, name);