package com.andrey.guryanov.harmonica;

import java.io.File;

public class Item {

    private final String path;
    private final String name;
    private boolean flag = false;
    private boolean isFile = false;
    private String extension;

    public Item(boolean isFile, String path, String extension) {
        this.path = path;
        this.name = new File(path).getName();
        this.isFile = isFile;
        this.extension = extension;
    }

    public Item(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public Item(String path) {
        this.path = path;
        this.name = new File(path).getName();
    }


    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getExtension() {
        return this.extension;
    }

    public boolean getFlag() {
        return this.flag;
    }



//    public void setIsFile(boolean isFile) {
//        this.isFile = isFile;
//    }

//    public void setExtension(String extension) {
//        this.extension = extension;
//    }

    public boolean replaceFlag() {
        flag = !flag;
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


}
