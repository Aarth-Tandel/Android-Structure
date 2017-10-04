package com.example.wozart.structure.model;

import java.util.ArrayList;

/**
 * Created by wozart on 28/09/17.
 */

public class Room {
    private String name;
    private ArrayList<Aura> devices;

    public Room(){

    }

    public void setName(String name){
        this.name = name;
    }

    public void setDevices(ArrayList<Aura> devices) {
        this.devices = devices;
    }

    public ArrayList<Aura> getDevices(){
        return this.devices;
    }

    public String getName(){
        return this.name;
    }
}
