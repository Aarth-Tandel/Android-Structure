package com.wozart.route_3.favourites;

/**
 * Created by wozart on 24/10/17.
 */



public class Favourites {
    private String name;
    private int numOfDevices;
    private int thumbnail;

    public Favourites() {
    }

    public Favourites(String name, int numOfSongs, int thumbnail) {
        this.name = name;
        this.numOfDevices = numOfSongs;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfDevices() {
        return numOfDevices;
    }

    public void setNumOfDevices(int numOfSongs) {
        this.numOfDevices = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}