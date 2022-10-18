package com.andrey.guryanov.harmonica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayList implements Serializable {

    private static final long serialVersionUID = 8906945642254162505L;
    private String name;

    private Track currentTrack;
    private int duration;
    private int elapsed;
    private boolean isPlaying;
    private boolean isStopped;

    private final List<Track> tracks = new ArrayList<>();
    private int countTracks;
    private long lastUsedID;                                                                        //нужен для того, чтобы каждой добавляемой песне присваивать уникальный ID, который --
                                                                                                    //-- никогда не использовался и уже никому не присвоится
//    private int currentTrackID;
    private int prevTrackNumber;
    private int currentTrackNumber;
    private int nextTrackNumber;
    private int tracksPlayedCounter;

    private List<Integer> trackQueue;

    public PlayList(String name) {
        this.name = name;

        initVariable();
        //addTestTracks();
    }

    private void initVariable() {
        countTracks = 0;
        lastUsedID = 0;
        currentTrack = new Track("", "", 0);                                            //пустая песня, нужна для корректной работы с плейлистом
        trackQueue = new ArrayList<>();                                                              //очередь воспроизведения песен. Через неё
    }


//    public void addTrackOLD(Track track) {
//        countTracks++;
//        tracks.add(song);
//
//    }

    public void addTrack(String name, String path) {
        countTracks++;
        tracks.add(new Track(name, path, generateNewId()));
        trackQueue.add(countTracks);

    }

    public int getCountTracks() {
        return countTracks;
    }

    public int getTracksPlayedCount() {
        return tracksPlayedCounter;
    }

    public void setTracksPlayedCount(int count) {
        tracksPlayedCounter = count;
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

    public List<Track> getTracks() {
        return tracks;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

//    public int getCurrentSongID() {
//        return currentSongID;
//    }

    public int getCurrentTrackNumber() {
        return currentTrackNumber;
    }

    public void setCurrentTrack(Track track) {
        currentTrack = track;
    }

    public void setFirstTrackAsCurrent() {
        currentTrack = tracks.get(0);
    }

    public void saveState() {

    }

    public void loadState() {

    }

    public void deleteTrack() {
        countTracks--;

    }

    public void setNextTrack() {
        tracksPlayedCounter++;
        //currentSongNumber = songQueue.get(songsPlayedCounter);
        currentTrack = tracks.get(trackQueue.get(tracksPlayedCounter) - 1);
    }

    public void setPrevTrack() {
        tracksPlayedCounter--;
        currentTrack = tracks.get(trackQueue.get(tracksPlayedCounter) - 1);
    }

    public boolean itsPossible(int intent) {
        //return tracksPlayedCounter != 0 && tracksPlayedCounter != (countTracks - 1);
        if (intent == -1) return tracksPlayedCounter != 0;
        else return countTracks > 1 && tracksPlayedCounter != (countTracks - 1);
    }


    public void addTestTrack() {
        int count = 100;    //необходимое количество тестовых песен
        for (int i = 0; i < count; i++) {
            addTrack("Песня № " + (i + 1),"/test");
        }
    }

}
