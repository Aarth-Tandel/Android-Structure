package com.wozart.route_3.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.models.nosql.UsersDO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wozart.route_3.data.DeviceContract.DeviceEntry.DEVICE_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.HOME_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_1;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_2;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_3;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.LOAD_4;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.ROOM_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.TABLE_NAME;
import static com.wozart.route_3.data.DeviceContract.DeviceEntry.THING_NAME;

/**
 * Created by wozart on 28/09/17.
 */

public class DeviceDbOperations {

    public ArrayList<String> GetAllDevices(SQLiteDatabase db) {
        ArrayList<String> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            devices.add(cursor.getString(1));
        }
        cursor.close();
        return devices;
    }


    public ArrayList<String> GetAllHome(SQLiteDatabase db) {
        ArrayList<String> home = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + HOME_NAME + " from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            home.add(cursor.getString(0));
        }
        cursor.close();
        return home;
    }

    public void InsertBasicData(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(HOME_NAME, "Home");
            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, value);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();
                cursor.close();
            }
        }
    }

    public void InsertHome(SQLiteDatabase db, String home) {
        ContentValues value = new ContentValues();
        value.put(HOME_NAME, home);
        try {
            db.beginTransaction();
            db.insert(TABLE_NAME, null, value);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //Too bad :(
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> GetRooms(SQLiteDatabase db, String home) {
        String[] params = new String[]{home};
        Cursor cursor = db.rawQuery("select distinct " + ROOM_NAME + " from " + TABLE_NAME + " where " + HOME_NAME
                + " = ?", params);
        ArrayList<String> room = new ArrayList<>();
        while (cursor.moveToNext()) {
            room.add(cursor.getString(0));
        }
        return room;
    }

    public void InsertRoom(SQLiteDatabase db, String home, String room) {
        String x = "null";
        String[] params = new String[]{x};
        Cursor cursor = db.rawQuery("select " + HOME_NAME + " from " + TABLE_NAME + " where " + ROOM_NAME
                + " = ?", params);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(HOME_NAME, home);
            value.put(ROOM_NAME, room);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, value);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
        cursor.close();
    }

    public void DeleteRoom(SQLiteDatabase db, String home, String room) {
        db.delete(TABLE_NAME, HOME_NAME + " =? and " + ROOM_NAME + " =? ", new String[]{home, room});
    }

    public void UpdateRoom(SQLiteDatabase db, String home, String previousRoom, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room);
        db.update(TABLE_NAME, cv, HOME_NAME + " =?  and " + ROOM_NAME + " =? ", new String[]{home, previousRoom});
    }

    public void TransferDeletedDevices(SQLiteDatabase db, String home, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, "Hall");
        db.update(TABLE_NAME, cv, HOME_NAME + " =? and " + ROOM_NAME + " =? ", new String[]{home, room});
    }

    public ArrayList<String> GetDevicesInRoom(SQLiteDatabase db, String room, String home) {
        ArrayList<String> devices = new ArrayList<>();
        String[] params = new String[]{home, room};
        Cursor cursor = db.rawQuery("select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + HOME_NAME
                + " = ? and " + ROOM_NAME + " =?", params);
        while (cursor.moveToNext()) {
            devices.add(cursor.getString(0));
        }
        cursor.close();
        return devices;
    }

    public ArrayList<String> GetLoads(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery("select " + LOAD_1 + ", " + LOAD_2 + ", " + LOAD_3 + ", " + LOAD_4 + " from " + TABLE_NAME + " where " + DEVICE_NAME
                + " = ?", params);
        ArrayList<String> loads = new ArrayList<>();
        while (cursor.moveToNext()) {
            loads.add(cursor.getString(0));
            loads.add(cursor.getString(1));
            loads.add(cursor.getString(2));
            loads.add(cursor.getString(3));

        }
        return loads;
    }

    public void AddDevice(SQLiteDatabase db, String room, String home, String device) {

        ArrayList<String> devicesDuplicate = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + DEVICE_NAME + " from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devicesDuplicate.add(cursor.getString(0));
        }
        cursor.close();
        Boolean flag = true;

        if (devicesDuplicate.isEmpty()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, room);
            cv.put(HOME_NAME, home);
            cv.put(DEVICE_NAME, device);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, cv);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
            return;
        }

        for (String x : devicesDuplicate) {
            if (x.equals(device))
                flag = false;
        }

        if (flag) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, room);
            cv.put(HOME_NAME, home);
            cv.put(DEVICE_NAME, device);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, cv);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
    }

    public void ThingFromAws(SQLiteDatabase db, UsersDO user) {
        for (String x : user.getDevices()) {
            boolean isThingAlreadyPresent;
            List<String> compositeDataFromAws;
            compositeDataFromAws = Arrays.asList(x.split(","));
            isThingAlreadyPresent = checkDevice(db, compositeDataFromAws.get(1));
            if (!isThingAlreadyPresent) {
                return;
            } else {
                ContentValues value = new ContentValues();
                value.put(ROOM_NAME, "Hall");
                value.put(THING_NAME, compositeDataFromAws.get(0));
                value.put(DEVICE_NAME, compositeDataFromAws.get(1));
                value.put(HOME_NAME, "Home");

                try {
                    db.beginTransaction();
                    db.insert(TABLE_NAME, null, value);

                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    //Too bad :(
                } finally {
                    db.endTransaction();

                }
            }
        }
    }

    private boolean checkDevice(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery("select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + DEVICE_NAME
                + " = ?", params);
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public ArrayList<String> GetThingName(SQLiteDatabase db) {
        ArrayList<String> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + THING_NAME + " from " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices.add(cursor.getString(0));
        }
        cursor.close();
        return devices;
    }

    public String GetDevice(SQLiteDatabase db, String thing) {
        String devices = null;
        String[] params = new String[]{thing};
        Cursor cursor = db.rawQuery("select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + THING_NAME
                + " = ?", params);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices = cursor.getString(0);
        }
        cursor.close();
        return devices;
    }
}
