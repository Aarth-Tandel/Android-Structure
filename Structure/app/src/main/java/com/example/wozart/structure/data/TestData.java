package com.example.wozart.structure.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    public  static  void insertDummyData(SQLiteDatabase db) {
        if(db == null)
            return;

        List<ContentValues> list = new ArrayList<>();

        ContentValues cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.DEVICE_NAME, "9500E");
        cv.put(DeviceContract.DeviceEntry.LOAD_1, "Lamp");
        cv.put(DeviceContract.DeviceEntry.LOAD_2, "Bulb");
        cv.put(DeviceContract.DeviceEntry.LOAD_3, "TV");
        cv.put(DeviceContract.DeviceEntry.LOAD_4, "Fan");
        cv.put(DeviceContract.DeviceEntry.HOME_NAME, "Home");
        cv.put(DeviceContract.DeviceEntry.ROOM_NAME, "Hall");
        list.add(cv);

        cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.DEVICE_NAME, "250FE");
        cv.put(DeviceContract.DeviceEntry.LOAD_1, "Lava Lamp");
        cv.put(DeviceContract.DeviceEntry.LOAD_2, "Bulb");
        cv.put(DeviceContract.DeviceEntry.LOAD_3, "Sofa");
        cv.put(DeviceContract.DeviceEntry.LOAD_4, "Fan");
        cv.put(DeviceContract.DeviceEntry.HOME_NAME, "Office");
        cv.put(DeviceContract.DeviceEntry.ROOM_NAME, "Lab");
        list.add(cv);

        try{
            db.beginTransaction();
            db.delete(DeviceContract.DeviceEntry.TABLE_NAME,null,null);

            for(ContentValues c : list){
                db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }catch (SQLException e){
            //Too bad :(
        }
        finally {
            db.endTransaction();
        }
    }

}