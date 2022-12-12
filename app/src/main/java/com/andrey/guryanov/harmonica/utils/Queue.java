package com.andrey.guryanov.harmonica.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Queue implements Serializable {
    List<Integer> queue;

    public Queue() {
        queue = new ArrayList<>();
    }

    public Queue(List<Integer> trackQueue) {
        this.queue = trackQueue;
    }

    public List<Integer> get() {
        return queue;
    }

//    public void set(List<Integer> trackQueue) {
//        this.queue = trackQueue;
//    }
}
