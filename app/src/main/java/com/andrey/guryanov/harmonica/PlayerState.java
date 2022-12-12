package com.andrey.guryanov.harmonica;

import com.andrey.guryanov.harmonica.utils.Queue;

import java.io.Serializable;

public class PlayerState implements Serializable {

    private Object[] state;

    private int currentTrackNumber;
    private int tracksPlayedCounter;
    private boolean isPlaying;                                                                      //состояние проигрывания плеера (тру - играет, фолс - на паузе)
    private boolean isStopped;                                                                      //состояние стопа у плеера (тру на стопе, фолс нет)
    private int trackElapsedTime;                                                                  //используется только при загрузке состояния для возобновления воспроизведения трека
                                                                                                    // -- с того места, откуда оно приостановилось
    private int playMode;                                                                           //режим очереди (0-последовательный, 1-случайно-последовательный, 2-абсолютно случайный)
    private Queue trackQueue;

    public PlayerState() {
        state = new Object[7];
        trackQueue = new Queue();
        state[0] = currentTrackNumber;
        state[1] = tracksPlayedCounter;
        state[2] = isPlaying;
        state[3] = isStopped;
        state[4] = trackElapsedTime;
        state[5] = playMode;
        state[6] = trackQueue;
    }

    public void setState(Object[] newState) {
        currentTrackNumber = (int) newState[0];
        tracksPlayedCounter = (int) newState[1];
        isPlaying = (boolean) newState[2];
        isStopped = (boolean) newState[3];
        trackElapsedTime = (int) newState[4];
        playMode = (int) newState[5];
        trackQueue = (Queue) newState[6];
    }

    public Object[] getState() {
        return state;
    }

}
