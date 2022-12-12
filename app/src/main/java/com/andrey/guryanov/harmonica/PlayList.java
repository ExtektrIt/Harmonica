package com.andrey.guryanov.harmonica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayList implements Serializable {

//    private static final long serialVersionUID = 8906945642254162505L;
    private String name;                                                                            //название плейлиста
    private final List<Track> tracks;                                                               //список треков этого плейлиста
    private long lastUsedID;                                                                        //нужен для того, чтобы каждой добавляемой песне присваивать уникальный ID, который
                                                                                                    //-- никогда не использовался и уже никому не присвоится
    //инфа по текущему плейлисту. Нужна только для того, чтобы загрузить её в плеер при инициализации

    ////private Track currentTrack;  //можно хранить не сам объект, а указатель на него                 //текущий трек в этом плейлисте
    private int currentTrackNumber;
    private int elapsedTime;                                                                        //пройденное время текущего трека
    private boolean isPlaying;                                                                      //
    private boolean isStopped;                                                                      //состояние плейлиста перед переключением (тру-остановлен, фолс-на паузе)
    private int countTracks;                                                                        //количество треков в плейлисте
    private int tracksPlayedCounter;                                                                //счетчик воспроизведённых треков, нужен для управления очередью
    private List<Integer> trackQueue;                                                               //очередь треков
    private int playMode;                                                                           //режим очереди (0-последовательный, 1-случайно-последовательный, 2-абсолютно случайный)

    public PlayList(String name) {
        tracks = new ArrayList<>();
        this.name = name;
        init();
    }

    private void init() {                                                                           //метод инициализирует базовые переменные
        countTracks = 0;
        lastUsedID = 0;
        tracksPlayedCounter = 0;
        //currentTrack = new Track("", "", "", 0);                                                    //задаём пустой трек как текущий, нужен для корректной работы с плейлистом
        currentTrackNumber = -1;
        trackQueue = new ArrayList<>();//!
        playMode = 0;
    }


    /** УПРАВЛЕНИЕ ТРЕКАМИ */


    public void addTrack(String name, String path, String ext) {                                    //метод создаёт новый объект Трек на основе аргументов и добавляет его в список треков
        tracks.add(new Track(name, path, ext, generateNewId()));                                    //создаёт и добавляет трек в список треков
        trackQueue.add(countTracks);                                                                //добавляет в очередь порядковый номер добавленного трека
        countTracks++;                                                                              //инкрементирует количество треков в плейлисте
    }

//    public void addDemoTrack() {
//        tracks.add(new Track());
//    }

    public void removeTrack(int position) {                                                         //метод удаляет трек из плейлиста
        Integer track = position;                                                                   //делаем
        tracks.remove(position);
        trackQueue.remove(track);
        countTracks--;
    }

//    public boolean switchTrack(int direction) {                                                     //метод переключает трек согласно очереди и направления (-1 - предыдущий, 1 - следующий)
//        if (itsPossible(direction)) {                                                               //если переключение трека в указанном направлении возможно
//            if (currentTrack == tracks.get(getCurrentTrackNumber() - 1 + direction)) {              //если текущий трек равен тому треку, на который хотим переключить
//                tracksPlayedCounter += direction;                                                   //тогда меняем счётчик воспроизведённых треков на 1 согласно направления, чтобы потом
//                                                                                                    //-- переключиться на другой трек согласно очереди, а не включать текущий трек повторно.
//                //в будущем привяжу всё к очереди                                                   //-- Актуально потому, что треки можно переключать ещё и из ТрекЛиста //можно улучшить
//                if (tracksPlayedCounter == 0 || tracksPlayedCounter == countTracks - 1) {           //если счётчик стал равен недопустимым значениям
//                    return false;                                                                   //сообщаем о том, что переключить трек нельзя
//                }
//            }
//            currentTrack = tracks.get(getCurrentTrackNumber() - 1 + direction);                     //берём очередной трек и ставим его как текущий (переключаем)
//            tracksPlayedCounter += direction;                                                       //меняем счетчик воспроизведённых треков на 1 в зависимости от направления переключения
//            return true;                                                                            //возвращаем результат о том, что всё переключилось как надо
//        }
//        else return false;                                                                          //иначе даём знать о том, что трек не переключился
//    }


    /** ГЕТТЕРЫ */


    public int getCountTracks() {
        return countTracks;
    }
//
////    public int getTracksPlayedCount() {
////        return tracksPlayedCounter;
////    }
//
    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

//    public Track getCurrentTrack() {
//        return currentTrack;
//    }

    public int getCurrentTrackNumber() {
        return currentTrackNumber;
    }

    public List<Integer> getQueue() {
        return trackQueue;
    }

    public Object[] getInfo() {
        Object[] info = new Object[10];
        info[0] = name;
        //info[1] = currentTrack;
        info[2] = currentTrackNumber;
        info[3] = elapsedTime;
        info[4] = isPlaying;
        info[5] = isStopped;
        info[6] = tracksPlayedCounter;
        info[7] = playMode;
        return info;
    }


    /** СЕТТЕРЫ */


//    public void refreshInfo() {
//
//    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void setTracksPlayedCounter(int count) {
        tracksPlayedCounter = count;
    }

//    public void setCurrentTrack(Track track) {
//        currentTrack = track;
//    }


    /** ПУБЛИЧНЫЕ СЛУЖЕБНЫЕ МЕТОДЫ */


    public void saveStateToPlayList() {

    }

//    public void setFirstTrackAsCurrent() {
//        currentTrack = tracks.get(0);
//    }

//    public int getCurrentTrackNumber() {
//        return trackQueue.get(tracksPlayedCounter);
//    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


//    private void setSequenceMode() {
//        Collections.sort(trackQueue);
////        for (int i = 0; i < countTracks; i++) {
////            trackQueue.add(i + 1);
////        }
//    }
//
//    private void setRandomSequenceMode() {
//        Collections.shuffle(trackQueue);// = new ArrayList<>(countTracks);
//
//    }
//
//    private void setAbsoluteRandomMode() {
//
//    }
//
//    private boolean itsPossible(int direction) {                                                    //метод вычисляет, можно ли переключить трек в указанном направлении, и возвращает ответ
//        if (direction < 0) return tracksPlayedCounter > 0;                                          //если намерение переключить назад, то вернёт тру, если это не первый трек в очереди
//        else return countTracks > 1 && tracksPlayedCounter < (countTracks - 1);                     //если намерение переключить вперёд, то вернёт тру, если треков > 1 и трек не последний
//                                                                                                    //-- в очереди
//    }

    private long generateNewId() {                                                                  //метод инкрементирует последний использованный ИД и возвращает его
        lastUsedID++;
        return lastUsedID;
    }


    // TEST!!!111




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

//        if (temp != currentTrack) currentTrack = temp;                                              //если текущий трек не равен очередному, то зададим очередной трек как текущий
//        else {                                                                                      //иначе, если очередной трек равен текущему
//            currentTrack = tracks.get(trackQueue.get(tracksPlayedCounter + direction) - 1);         //снова вычисляем очередной трек относительно направления и задаём его как текущий
//            tracksPlayedCounter += direction;
//        }

//    private void setCurrentTrackFromQueue(int direction) { }

//    public boolean setNextTrack() {                                                                    //метод задаёт следущий трек как текущий
//        int next = 1;
//        if (itsPossible(next)) {
//            setCurrentTrackFromQueue(next);
//            tracksPlayedCounter++;//
//            return true;
//        }
//    }
//
//    public void setPrevTrack() {                                                                    //метод задаёт предыдущий трек как текущий
//        setCurrentTrackFromQueue(-1);
//        tracksPlayedCounter--;//
//    }

//    public void switchPlayMode() {
////        if (playMode == 2) playMode = 0;
////        else playMode++;
////        changePlayMode();
//        if (playMode < 2) {
//            playMode++;
//            if (playMode == 1) {
//                setRandomSequenceMode();
//            }
////            else setAbsoluteRandomMode();
//        }
//        else {
//            playMode = 0;
//            setSequenceMode();
//        }
//
//    }