package com.andrey.guryanov.harmonica.utils;

public class Computer {

    public Computer() {

    }

    public static int digitModule(int num) {
        if (num > 0) return num;
        else return -num;
    }
}
