package com.andrey.guryanov.harmonica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayList implements Serializable {

    private static final long serialVersionUID = 8906945642254162505L;
    private String name;                                                                            //название плейлиста

    private Track currentTrack;                                                                     //текущий трек в этом плейлисте
    private int duration;                                                                           //длительность текущего трека
    private int elapsed;                                                                            //пройденное время текущего трека
//    private boolean isPlaying;                                                                      //
    private boolean isStopped;                                                                      //состояние плейлиста перед переключением (тру-остановлен, фолс-на паузе)

    private final List<Track> tracks;                                                               //список треков этого плейлиста
    private int countTracks;                                                                        //количество треков в плейлисте
    private long lastUsedID;                                                                        //нужен для того, чтобы каждой добавляемой песне присваивать уникальный ID, который
                                                                                                    //-- никогда не использовался и уже никому не присвоится
//    private int currentTrackID;
//    private int prevTrackNumber;
    private int currentTrackNumber;
//    private int nextTrackNumber;
    private int tracksPlayedCounter;                                                                //счетчик воспроизведённых треков, нужен для управления очередью

    private List<Integer> trackQueue;                                                               //очередь треков
    private byte queueMode;                                                                         //режим очереди (0-последовательный, 1-случайно-последовательный, 2-абсолютно случайный)


    public PlayList(String name) {
        tracks = new ArrayList<>();
        this.name = name;
        init();
    }

    private void init() {                                                                           //метод инициализирует базовые переменные
        countTracks = 0;
        lastUsedID = 0;
        tracksPlayedCounter = 0;
        currentTrack = new Track("", "", 0);                                                        //задаём пустой трек как текущий, нужен для корректной работы с плейлистом
        trackQueue = new ArrayList<>();
    }


    /** ЗАГРУЗКА И СОХРАНЕНИЕ СОСТОЯНИЯ */


    public void saveState() {                                                                       //метод сохраняет состояние плеера в плейлист перед переключением на другой плейист

    }

    public void loadState() {                                                                       //метод загружает состояние плейлиста в плеер при переключении с другого плейлиста

    }


    /** УПРАВЛЕНИЕ ТРЕКАМИ */


    public void addTrack(String name, String path) {                                                //метод создаёт новый объект Трек на основе аргументов и добавляет его в список треков
        countTracks++;                                                                              //инкрементирует количество треков в плейлисте
        tracks.add(new Track(name, path, generateNewId()));                                         //создаёт и добавляет трек в список треков
        trackQueue.add(countTracks);                                                                //добавляет в очередь порядковый номер добавленного трека
    }

    public void deleteTrack(int position) {                                                         //метод удаляет трек из плейлиста
        Integer track = position;                                                                   //делаем
        tracks.remove(position - 1);
        trackQueue.remove(track);
        countTracks--;
    }

    public void setNextTrack() {                                                                    //метод задаёт следущий трек как текущий
        tracksPlayedCounter++;
        currentTrack = tracks.get(trackQueue.get(tracksPlayedCounter) - 1);
    }

    public void setPrevTrack() {                                                                    //метод задаёт предыдущий трек как текущий
        tracksPlayedCounter--;
        currentTrack = tracks.get(trackQueue.get(tracksPlayedCounter) - 1);
    }


    /** ГЕТТЕРЫ */


    public int getCountTracks() {
        return countTracks;
    }

    public int getTracksPlayedCount() {
        return tracksPlayedCounter;
    }

    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public int getCurrentTrackNumber() {
        return currentTrackNumber;
    }


    /** СЕТТЕРЫ */


    public void rename(String newName) {
        this.name = newName;
    }

    public void setTracksPlayedCount(int count) {
        tracksPlayedCounter = count;
    }

    public void setCurrentTrack(Track track) {
        currentTrack = track;
    }

    public void setFirstTrackAsCurrent() {
        currentTrack = tracks.get(0);
    }


    /** ПУБЛИЧНЫЕ СЛУЖЕБНЫЕ МЕТОДЫ */


    public boolean itsPossible(byte direction) {                                                    //метод вычисляет, можно ли переключить трек в указанном направлении, и возвращает ответ
        //return tracksPlayedCounter != 0 && tracksPlayedCounter != (countTracks - 1);
        if (direction == -1) return tracksPlayedCounter != 0;                                       //если намерение переключить назад, то вернёт ТРУ, если это не первый трек в очереди
        else return countTracks > 1 && tracksPlayedCounter != (countTracks - 1);                    //если намерение переключить вперёд, то вернёт ТРУ, если треков > 1 и трек не последний
                                                                                                    //-- в очереди
    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private long generateNewId() {                                                                  //метод инкрементирует последний использованный ИД и возвращает его
        lastUsedID++;
        return lastUsedID;
    }

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//    public void addTestTrack() {
//        int count = 100;    //необходимое количество тестовых песен
//        for (int i = 0; i < count; i++) {
//            addTrack("Песня № " + (i + 1),"/test");
//        }
//    }

//    public int getCurrentSongID() {
//        return currentSongID;
//    }

//    public void addTrackOLD(Track track) {
//        countTracks++;
//        tracks.add(song);
//
//    }