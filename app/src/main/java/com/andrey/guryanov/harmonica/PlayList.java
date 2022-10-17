package com.andrey.guryanov.harmonica;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayList implements Serializable {

    private static final long serialVersionUID = 8906945642254162505L;
    private String name;

    private Song currentSong;
    private int duration;
    private int elapsed;
    private boolean isPlaying;
    private boolean isStopped;

    private final List<Song> songs = new ArrayList<>();
    private int countSongs;
    private long lastUsedID;                                                                        //нужен для того, чтобы каждой добавляемой песне присваивать уникальный ID, который
                                                                                                    //никогда не использовался и уже никому не присвоится
//    private int currentSongID;
    private int prevSongNumber;
    private int currentSongNumber;
    private int nextSongNumber;
    private int songsPlayedCounter;

    private List<Integer> songQueue;

    public PlayList(String name) {
        this.name = name;

        initVariable();
        //addTestSongs();
    }

    private void initVariable() {
        countSongs = 0;
        lastUsedID = 0;
        currentSong = new Song("", "", 0);                                            //пустая песня, нужна для корректной работы с плейлистом
        songQueue = new ArrayList<>();                                                              //очередь воспроизведения песен. Через неё
    }


//    public void addSongOLD(Song song) {
//        countSongs++;
//        songs.add(song);
//
//    }

    public void addSong(String name, String path) {
        countSongs++;
        songs.add(new Song(name, path, generateNewId()));
        songQueue.add(countSongs);

    }

    public int getCountSongs() {
        return countSongs;
    }

    public int getSongsPlayedCount() {
        return songsPlayedCounter;
    }

    public void setSongsPlayedCount(int count) {
        songsPlayedCounter = count;
    }

    public long generateNewId() {
        lastUsedID++;
        return lastUsedID;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

//    public int getCurrentSongID() {
//        return currentSongID;
//    }

    public int getCurrentSongNumber() {
        return currentSongNumber;
    }

    public void setCurrentSong(Song song) {
        currentSong = song;
    }

    public void setFirstSongAsCurrent() {
        currentSong = songs.get(0);
    }

    public void saveState() {

    }

    public void loadState() {

    }

    public void deleteSong() {
        countSongs--;

    }

    public void setNextSong() {
        songsPlayedCounter++;
        //currentSongNumber = songQueue.get(songsPlayedCounter);
        currentSong = songs.get(songQueue.get(songsPlayedCounter) - 1);
    }

    public void setPrevSong() {
        songsPlayedCounter--;
        currentSong = songs.get(songQueue.get(songsPlayedCounter) - 1);
    }

    public boolean itsPossible(int intent) {
        //return songsPlayedCounter != 0 && songsPlayedCounter != (countSongs - 1);
        if (intent == -1) return songsPlayedCounter != 0;
        else return countSongs > 1 && songsPlayedCounter != (countSongs - 1);
    }


    public void addTestSongs() {
        int count = 100;    //необходимое количество тестовых песен
        for (int i = 0; i < count; i++) {
            addSong("Песня № " + (i + 1),"/test");
        }
    }

}
