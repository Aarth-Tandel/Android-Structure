package com.wozart.route_3.noSql;

/**
 * Created by wozart on 12/10/17.
 */

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private ArrayList<String> Devices = new ArrayList<>();
    public void AddDevices(String name){
        Devices.add(name);
    }

    public List<String> GetDevices(){
        return Devices;
    }
}
