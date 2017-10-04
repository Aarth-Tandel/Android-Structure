package com.example.wozart.structure.model;

import java.util.ArrayList;

/**
 * Created by wozart on 29/09/17.
 */

public class Home {
    private String name;
    private ArrayList<Room> room;

    public void setName(String name){
        this.name = name;
    }

    public void addRoom(Room room){
        this.room.add(room);
    }

    public ArrayList<Room> getRoom(){
        return this.room;
    }

    public String getName(){
        return this.name;
    }
}
