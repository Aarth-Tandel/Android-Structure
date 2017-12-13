package com.wozart.route_3.deviceSqlLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.models.nosql.UsersDO;
import com.wozart.route_3.customization.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.Constant.CHECK_DEVICES;
import static com.Constant.CRUD_ROOM;
import static com.Constant.GET_ALL_DEVICES;
import static com.Constant.GET_ALL_HOME;
import static com.Constant.GET_DEVICES_FOR_THING;
import static com.Constant.GET_DEVICES_IN_ROOM;
import static com.Constant.GET_LOADS;
import static com.Constant.GET_ROOMS;
import static com.Constant.GET_THING_NAME;
import static com.Constant.INSERT_DEVICES;
import static com.Constant.INSERT_INITIAL_DATA;
import static com.Constant.INSERT_ROOMS;
import static com.Constant.UPDATE_DEVICE;
import static com.Constant.UPDATE_LOAD1_NAME;
import static com.Constant.UPDATE_LOAD2_NAME;
import static com.Constant.UPDATE_LOAD3_NAME;
import static com.Constant.UPDATE_LOAD4_NAME;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.DEVICE_NAME;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.HOME_NAME;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.LOAD_1;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.LOAD_2;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.LOAD_3;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.LOAD_4;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.ROOM_NAME;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.TABLE_NAME;
import static com.wozart.route_3.deviceSqlLite.DeviceContract.DeviceEntry.THING_NAME;

/**
 * Created for Wozart on 28/09/17.
 * Author - Aarth Tandel
 *
 * Database accessing layer
 *
 * //////////////////////////////
 * Version - 1.0.0 - Initial built
 * //////////////////////////////
 */

public class DeviceDbOperations {

    public List<Device> GetAllDevices(SQLiteDatabase db) {
        List<Device> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_DEVICES, null);
        while (cursor.moveToNext()) {
            Device dummyDevice = new Device();
            dummyDevice.setDevice(cursor.getString(1));
            dummyDevice.setHome(cursor.getString(6));
            dummyDevice.setRoom(cursor.getString(7));
            dummyDevice.setThing(cursor.getString(8));
            devices.add(dummyDevice);
        }
        cursor.close();
        return devices;
    }


    public ArrayList<String> GetAllHome(SQLiteDatabase db) {
        ArrayList<String> home = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_HOME, null);
        while (cursor.moveToNext()) {
            home.add(cursor.getString(0));
        }
        cursor.close();
        return home;
    }

    public void InsertBasicData(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(INSERT_INITIAL_DATA, null);
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
        Cursor cursor = db.rawQuery(GET_ROOMS, params);
        ArrayList<String> room = new ArrayList<>();
        while (cursor.moveToNext()) {
            room.add(cursor.getString(0));
        }
        cursor.close();
        return room;
    }

    public void InsertRoom(SQLiteDatabase db, String home, String room) {
        String x = "null";
        String[] params = new String[]{x};
        Cursor cursor = db.rawQuery(INSERT_ROOMS, params);
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
        db.delete(TABLE_NAME, CRUD_ROOM, new String[]{home, room});
    }

    public void UpdateRoom(SQLiteDatabase db, String home, String previousRoom, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room);
        db.update(TABLE_NAME, cv, CRUD_ROOM, new String[]{home, previousRoom});
    }

    public void UpdateRoomAndHome(SQLiteDatabase db, String home, String room, String device){
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room);
        cv.put(HOME_NAME, home);
        db.update(TABLE_NAME, cv,UPDATE_DEVICE , new String[]{device});
    }

    public void TransferDeletedDevices(SQLiteDatabase db, String home, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, "Hall");
        db.update(TABLE_NAME, cv, CRUD_ROOM, new String[]{home, room});
    }

    public ArrayList<String> GetDevicesInRoom(SQLiteDatabase db, String room, String home) {
        ArrayList<String> devices = new ArrayList<>();
        String[] params = new String[]{home, room};
        Cursor cursor = db.rawQuery(GET_DEVICES_IN_ROOM, params);
        while (cursor.moveToNext()) {
            devices.add(cursor.getString(0));
        }
        cursor.close();
        return devices;
    }

    public ArrayList<String> GetLoads(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(GET_LOADS, params);
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
        Cursor cursor = db.rawQuery(INSERT_DEVICES, null);
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
        Cursor cursor = db.rawQuery(CHECK_DEVICES, params);
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
        Cursor cursor = db.rawQuery(GET_THING_NAME, null);
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
        Cursor cursor = db.rawQuery(GET_DEVICES_FOR_THING, params);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices = cursor.getString(0);
        }
        cursor.close();
        return devices;
    }

    public void updateLoadName(SQLiteDatabase db, String oldName, String home, String room, int loadNumber, String load){
        String[] params = new String[]{home, room, oldName};
        ContentValues values = new ContentValues();
        switch (loadNumber){
            case 0:
                values.put(LOAD_1, load);
                db.update(TABLE_NAME,values, UPDATE_LOAD1_NAME, params);
                break;
            case 1:
                values.put(LOAD_2, load);
                db.update(TABLE_NAME,values, UPDATE_LOAD2_NAME, params);
                break;
            case 2:
                values.put(LOAD_3,load);
                db.update(TABLE_NAME,values, UPDATE_LOAD3_NAME, params);
                break;
            case 3:
                values.put(LOAD_4, load);
                db.update(TABLE_NAME,values, UPDATE_LOAD4_NAME, params);
                break;
        }
    }
}
